
package org.osgi.impl.service.zigbee.basedriver.configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.zigbee.basedriver.ZCLAttributeImpl;
import org.osgi.impl.service.zigbee.basedriver.ZigBeeHostImpl;
import org.osgi.impl.service.zigbee.basedriver.ZigBeeNodeImpl;
import org.osgi.impl.service.zigbee.basedriver.descriptors.ZigBeeNodeDescriptorImpl;
import org.osgi.impl.service.zigbee.basedriver.descriptors.ZigBeePowerDescriptorImpl;
import org.osgi.impl.service.zigbee.basedriver.descriptors.ZigBeeSimpleDescriptorImpl;
import org.osgi.impl.service.zigbee.descriptions.ZCLAttributeDescriptionImpl;
import org.osgi.impl.service.zigbee.descriptions.ZCLClusterDescriptionImpl;
import org.osgi.impl.service.zigbee.descriptions.ZCLGlobalClusterDescriptionImpl;
import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription;
import org.osgi.service.zigbee.descriptions.ZCLGlobalClusterDescription;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author $Id$
 */
public class ConfigurationFileReader {

	private static ConfigurationFileReader	instance;
	private ZigBeeHost						host;
	public ZigBeeNodeConf[]					nodes;
	private int								headerMinSize						= 1;
	private int								headerMaxSize						= 1;
	private ZCLAttribute					firstAttributeWithBooleanDatatype	= null;
	private BundleContext					bc;

	private ConfigurationFileReader(String pathFile, BundleContext bc) {
		this.bc = bc;
		readXmlFile(pathFile);
	}

	public static ConfigurationFileReader getInstance(String pathFile,
			BundleContext bc) {
		if (instance == null) {
			return new ConfigurationFileReader(pathFile, bc);
		}
		return instance;
	}

	public void readXmlFile(String pathFile) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			try {
				Document doc = dBuilder.parse(new InputSource(
						new ByteArrayInputStream(pathFile.getBytes("utf-8"))));
				getHost(doc);
				getNodes(doc);
				getFrameInfo(doc);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void getFrameInfo(Document doc) {
		NodeList nList = doc.getElementsByTagName("frame");
		Node node = nList.item(0);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element frameElement = (Element) node;
			String maxSize = frameElement.getAttribute("headerMaxSize");
			String minSize = frameElement.getAttribute("headerMinSize");
			headerMaxSize = Integer.parseInt(maxSize);
			headerMinSize = Integer.parseInt(minSize);
		}

	}

	/**
	 * 
	 * @return the first node in the configuration file
	 */
	public ZigBeeNodeConf getNode0() {
		ZigBeeNodeConf node = nodes[0];

		return node;
	}

	/**
	 * Returns the value read from the configuration file that must contain the
	 * ZCL minimum frame size.
	 * 
	 * @return the ZCL frames possible minimum frame size.
	 */

	public int getHeaderMinSize() {
		return headerMinSize;
	}

	public int getHeaderMaxSize() {
		return headerMaxSize;
	}

	public ZCLAttribute getFirstAttributeWithBooleanDatatype() {
		return firstAttributeWithBooleanDatatype;
	}

	public ZigBeeHost getZigBeeHost() {
		return host;
	}

	public ZigBeeEndpoint[] getEnpoints(ZigBeeNodeImpl node) {
		return node.getEndpoints();
	}

	private void getHost(Document doc) {
		NodeList nList = doc.getElementsByTagName("host");
		Node node = nList.item(0);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element hostElement = (Element) node;
			// get host infos
			String hostPId = hostElement.getAttribute("pid");
			String panId = hostElement.getAttribute("panId");
			String channel = hostElement.getAttribute("channel");
			String securityLevel = hostElement.getAttribute("securityLevel");
			String ieeeAddress = hostElement.getAttribute("ieeeAddress");
			// get descriptor
			ZigBeeNodeDescriptor nodeDesc = getZigBeeNodeDescriptor(hostElement);
			host = new ZigBeeHostImpl(hostPId,
					Integer.parseInt(panId),
					Integer.parseInt(channel),
					Integer.parseInt(securityLevel),
					new BigInteger(ieeeAddress),
					null,
					nodeDesc,
					null,
					null);

			NodeList enpointsList = hostElement
					.getElementsByTagName("endpoint");
			Node enpointNode = enpointsList.item(0);
			if (enpointNode != null
					&& enpointNode.getNodeType() == Node.ELEMENT_NODE) {
				Element enpointElement = (Element) enpointNode;
				String endpointId = enpointElement.getAttribute("id");

				NodeList serverClusters = enpointElement.getElementsByTagName("serverClusters");
				ZCLCluster[] clusters = getServerClusters(serverClusters);

				NodeList clientClustersNodes = enpointElement.getElementsByTagName("clientClusters");
				ZCLCluster[] clientClusters = getClientClusters(clientClustersNodes);
				ZigBeeEndpointImpl[] endpointsHost = new ZigBeeEndpointImpl[1];

				ZigBeeSimpleDescriptor desc = getSimpleDescriptor(enpointElement);

				endpointsHost[0] = new ZigBeeEndpointImpl(Short.parseShort(endpointId), clusters, clientClusters, desc);

				host = new ZigBeeHostImpl(hostPId,
						Integer.parseInt(panId),
						Integer.parseInt(channel),
						Integer.parseInt(securityLevel),
						new BigInteger(ieeeAddress),
						endpointsHost,
						nodeDesc,
						null,
						null);
			}
		}
	}

	private void getNodes(Document doc) {

		NodeList nList = doc.getElementsByTagName("nodes");
		Node nodesNode = nList.item(0);
		if (nodesNode.getNodeType() == Node.ELEMENT_NODE) {
			Element nodesElement = (Element) nodesNode;

			NodeList nodeList = nodesElement.getElementsByTagName("node");
			ZigBeeEndpoint[] ZEnpoints = null;
			int listLength = nodeList.getLength();
			nodes = new ZigBeeNodeConf[listLength];
			for (int i = 0; i < listLength; i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element nodeElement = (Element) node;

					// get NB of enpoints
					int enpointNb = Integer.parseInt(nodeElement.getAttribute("endpointNb"));

					// get host infos
					String hostPId = nodeElement.getAttribute("hostPid");
					String ieeeAddress = nodeElement.getAttribute("ieeeAddress");
					String userDescription = nodeElement.getAttribute("userDescription");
					String endpointNb = nodeElement.getAttribute("endpointNb");
					String nwkAddress = nodeElement.getAttribute("nwkAddress");

					NodeList enpointsList = nodeElement.getElementsByTagName("endpoints");
					Node endpoints = enpointsList.item(0);
					if (endpoints != null && endpoints.getNodeType() == Node.ELEMENT_NODE) {
						Element endpointsElement = (Element) endpoints;
						NodeList enpointList = endpointsElement
								.getElementsByTagName("endpoint");

						int endpointsLength = enpointList.getLength();
						int[] endpointUsedIds = new int[endpointsLength];
						ZEnpoints = new ZigBeeEndpoint[enpointNb];
						for (int j = 0; j < endpointsLength; j++) {
							Node enpointNode = enpointList.item(j);
							if (enpointNode != null && enpointNode.getNodeType() == Node.ELEMENT_NODE) {
								Element enpointElement = (Element) enpointNode;
								String endpointId = enpointElement.getAttribute("id");
								endpointUsedIds[i] = Integer.parseInt(endpointId);

								NodeList serverClusters = enpointElement.getElementsByTagName("serverClusters");
								ZCLCluster[] clusters = getServerClusters(serverClusters);

								NodeList clientClustersNodes = enpointElement.getElementsByTagName("clientClusters");
								ZCLCluster[] clientClusters = getClientClusters(clientClustersNodes);
								ZigBeeSimpleDescriptor desc = getSimpleDescriptor(enpointElement);
								ZEnpoints[i] = new ZigBeeEndpointImpl(Short.parseShort(endpointId), clusters, clientClusters, desc);
							}
						}
						createFakeEndpointsIfNeeded(ZEnpoints, endpointsLength, endpointUsedIds, enpointNb);
					}

					ZigBeeNodeDescriptor nodeDesc = getZigBeeNodeDescriptor(nodeElement);
					ZigBeePowerDescriptor powerDesc = getZigBeePowerDescriptor(nodeElement);

					nodes[i] = new ZigBeeNodeImpl(host, new BigInteger(ieeeAddress), ZEnpoints, nodeDesc, powerDesc, userDescription);
				}
			}
		}
	}

	private void createFakeEndpointsIfNeeded(ZigBeeEndpoint[] zEnpoints,
			int endpointsLength, int[] endpointUsedIds, int enpointNb) {
		int maxId = 0;
		for (int i = 0; i < endpointsLength; i++) {

			if (maxId < endpointUsedIds[i]) {
				maxId = endpointUsedIds[i];
			}
		}

		for (int j = endpointsLength; j < enpointNb; j++) {
			maxId++;
			zEnpoints[j] = new ZigBeeEndpointConf((short) maxId,
					null,
					null,
					null);
		}
	}

	private ZigBeeNodeDescriptor getZigBeeNodeDescriptor(Element nodeElement) {
		ZigBeeNodeDescriptor result = null;
		NodeList descList = nodeElement.getElementsByTagName("nodeDescriptor");
		Node nodeDesc = descList.item(0);
		if (nodeDesc != null && nodeDesc.getNodeType() == Node.ELEMENT_NODE) {
			Element nodeDescElt = (Element) nodeDesc;
			short type = Short.parseShort(nodeDescElt.getAttribute("type"));
			short band = 0;
			if (nodeDescElt.getAttribute("band") != null
					&& !"".equals(nodeDescElt.getAttribute("band"))) {
				band = Short.parseShort(nodeDescElt.getAttribute("band"));
			}
			Integer manufCode = null;
			if (nodeDescElt.getAttribute("manufCode") != null
					&& !"".equals(nodeDescElt.getAttribute("manufCode"))) {
				manufCode = new Integer(nodeDescElt.getAttribute("manufCode"));
			}

			int maxBufSize = 0;
			if (nodeDescElt.getAttribute("maxBufSize") != null
					&& !"".equals(nodeDescElt.getAttribute("maxBufSize"))) {
				maxBufSize = Integer.parseInt(nodeDescElt
						.getAttribute("maxBufSize"));
			}

			boolean isComplexAv = false;
			if (nodeDescElt.getAttribute("isComplexAv") != null
					&& !"".equals(nodeDescElt.getAttribute("isComplexAv"))) {
				isComplexAv = new Boolean(
						nodeDescElt.getAttribute("isComplexAv")).booleanValue();
			}

			boolean isUserAv = false;
			if (nodeDescElt.getAttribute("isUserAv") != null
					&& !"".equals(nodeDescElt.getAttribute("isUserAv"))) {
				isUserAv = new Boolean(nodeDescElt.getAttribute("isUserAv"))
						.booleanValue();
			}

			result = new ZigBeeNodeDescriptorImpl(type,
					band,
					manufCode,
					maxBufSize,
					isComplexAv,
					isUserAv);
		}
		return result;
	}

	public NetworkAttributeIds getFirstReportableAttribute() {
		BigInteger ieeeAddresss;
		short endpointId;
		int clusterId;
		int attributeId;

		for (int i = 0; i < nodes.length; i++) {
			ZigBeeEndpoint[] endpoints = nodes[i].getEndpoints();
			ieeeAddresss = nodes[i].getIEEEAddress();
			for (int j = 0; j < endpoints.length; j++) {
				ZCLCluster[] serverClusters = endpoints[j].getServerClusters();
				endpointId = endpoints[j].getId();
				for (int k = 0; k < serverClusters.length; k++) {

					// This is poor practice as we shouldn't block on a Promise
					try {
						ZCLAttribute[] attributes = (ZCLAttribute[]) ((ZCLClusterConf) serverClusters[k]).getAttributes().getValue();
						clusterId = serverClusters[k].getId();
						for (int l = 0; l < attributes.length; l++) {
							if (((ZCLAttributeImpl) attributes[l]).getAttributeDescription().isReportable()) {
								attributeId = attributes[l].getId();
								return new NetworkAttributeIds(ieeeAddresss, endpointId, clusterId, attributeId);
							}
						}
					} catch (Exception e) {
						throw new RuntimeException("A problem occurred!",
								e instanceof InvocationTargetException ? ((InvocationTargetException) e).getTargetException() : e);
					}
				}
			}
		}
		return null;
	}

	private ZigBeePowerDescriptor getZigBeePowerDescriptor(Element nodeElement) {
		ZigBeePowerDescriptor result = null;

		NodeList descList = nodeElement.getElementsByTagName("powerDescriptor");
		Node nodeDesc = descList.item(0);
		if (nodeDesc != null && nodeDesc.getNodeType() == Node.ELEMENT_NODE) {
			Element powerDescElt = (Element) nodeDesc;

			short powerMode = Short.parseShort(powerDescElt
					.getAttribute("powerMode"));
			short powerSource = Short.parseShort(powerDescElt
					.getAttribute("powerSource"));
			short powerSourceLevel = Short.parseShort(powerDescElt
					.getAttribute("powerSourceLevel"));

			boolean isconstant = new Boolean(
					powerDescElt.getAttribute("isconstant")).booleanValue();

			result = new ZigBeePowerDescriptorImpl(powerMode,
					powerSource,
					powerSourceLevel,
					isconstant);
		}
		return result;
	}

	private ZCLCluster[] getServerClusters(NodeList serverClusters) {

		Node node = serverClusters.item(0);
		ZCLCluster[] result = null;
		if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
			Element serverClustersElement = (Element) node;
			NodeList clusterList = serverClustersElement
					.getElementsByTagName("cluster");

			int clusterLength = clusterList.getLength();
			result = new ZCLCluster[clusterLength];
			for (int i = 0; i < clusterLength; i++) {

				ZCLClusterDescription serverClusterDescription = null;
				int[] ids = null;
				ZCLAttribute[] attributes = null;

				Node clusterNode = clusterList.item(i);
				if (clusterNode != null
						&& node.getNodeType() == Node.ELEMENT_NODE) {
					Element clusterElement = (Element) clusterNode;
					String clusterId = clusterElement.getAttribute("id");

					// get serverGlobalDescription
					NodeList globalDescList = clusterElement
							.getElementsByTagName("globalServerDescription");
					Node globalDesc = globalDescList.item(0);
					if (globalDesc != null
							&& globalDesc.getNodeType() == Node.ELEMENT_NODE) {
						Element desc = (Element) globalDesc;
						String id = desc.getAttribute("id");
						String name = desc.getAttribute("name");
						String domain = desc.getAttribute("domain");
						ZCLGlobalClusterDescription ZCLGlobalDesc = new ZCLGlobalClusterDescriptionImpl(
								new Integer(id).intValue(),
								name,
								domain,
								null,
								null);
						serverClusterDescription = new ZCLClusterDescriptionImpl(
								new Integer(clusterId).intValue(),
								ZCLGlobalDesc);
					}

					// get command ids
					NodeList commandIdsList = clusterElement
							.getElementsByTagName("commandIds");
					Node commandIds = commandIdsList.item(0);
					if (commandIds != null
							&& commandIds.getNodeType() == Node.ELEMENT_NODE) {
						Element commandIdsElt = (Element) commandIds;
						NodeList idlist = commandIdsElt
								.getElementsByTagName("id");

						int length = idlist.getLength();
						ids = new int[length];
						for (int j = 0; j < length; j++) {
							Node commandId = idlist.item(j);
							if (commandId != null
									&& commandId.getNodeType() == Node.ELEMENT_NODE) {
								Element commandIdElt = (Element) commandId;
								String idString = commandIdElt
										.getAttribute("id");
								ids[j] = new Integer(idString).intValue();
							}
						}
					}

					// get attributes
					clusterElement.getElementsByTagName("attributes");
					Node nodeAttributes = serverClusters.item(0);
					if (nodeAttributes != null
							&& nodeAttributes.getNodeType() == Node.ELEMENT_NODE) {
						Element attributesElement = (Element) nodeAttributes;
						NodeList attributeList = attributesElement
								.getElementsByTagName("attribute");
						int length = attributeList.getLength();
						attributes = new ZCLAttribute[length];

						for (int k = 0; k < length; k++) {
							Node attributeNode = attributeList.item(k);
							if (attributeNode != null
									&& attributeNode.getNodeType() == Node.ELEMENT_NODE) {
								Element attributeElement = (Element) attributeNode;
								String tempId = attributeElement
										.getAttribute("id");
								ZCLAttribute ZCLAttr = getZCLAttribute(attributeElement);
								attributes[k] = ZCLAttr;
							}

						}
					}
				}
				ZCLClusterConf clusterImpl = new ZCLClusterConf(ids,
						attributes,
						serverClusterDescription);
				result[i] = clusterImpl;
			}
		}

		return result;
	}

	private ZCLAttribute getZCLAttribute(Element attrElt) {
		ZCLAttribute attr = null;

		NodeList attrList = attrElt
				.getElementsByTagName("attributeDescription");
		Node attributeDescriptionNode = attrList.item(0);
		if (attributeDescriptionNode != null
				&& attributeDescriptionNode.getNodeType() == Node.ELEMENT_NODE) {
			Element attrDescriptionsElement = (Element) attributeDescriptionNode;
			String id = attrDescriptionsElement.getAttribute("id");
			String isReadOnly = attrDescriptionsElement
					.getAttribute("isReadOnly");
			String defaultValue = attrDescriptionsElement
					.getAttribute("defaultValue");
			String name = attrDescriptionsElement.getAttribute("name");
			String isMandatory = attrDescriptionsElement
					.getAttribute("isMandatory");
			String isReportable = attrDescriptionsElement
					.getAttribute("isReportable");
			String datatype = attrDescriptionsElement.getAttribute("dataType");

			Class cls;
			try {
				cls = Class
						.forName("org.osgi.service.zigbee.types." + datatype);
				Class[] classes = {};
				Method method = cls.getMethod("getInstance", classes);
				ZCLDataTypeDescription dataTypeDesc = (ZCLDataTypeDescription) method
						.invoke(null, null);
				ZCLAttributeDescriptionImpl attrDescImpl = new ZCLAttributeDescriptionImpl(
						new Integer(id).intValue(),
						new Boolean(isReadOnly).booleanValue(),
						defaultValue,
						name,
						new Boolean(isMandatory).booleanValue(),
						new Boolean(isReportable).booleanValue(),
						dataTypeDesc);
				attr = new ZCLAttributeImpl(attrDescImpl);
				if ("ZigBeeBoolean".equals(datatype)) {
					firstAttributeWithBooleanDatatype = attr;
				}

			} catch (ClassNotFoundException e) {

				e.printStackTrace();
			} catch (NoSuchMethodException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			}

		}

		return attr;
	}

	private ZigBeeSimpleDescriptorImpl getSimpleDescriptor(Element endPoint) {

		List serverClusterList = new ArrayList();
		// List clientClusterList = new ArrayList();

		ZigBeeSimpleDescriptorImpl result = null;
		NodeList endpointDescList = endPoint
				.getElementsByTagName("simpleDescriptor");
		Node descriptorNode = endpointDescList.item(0);
		if (descriptorNode != null
				&& descriptorNode.getNodeType() == Node.ELEMENT_NODE) {
			Element descElt = (Element) descriptorNode;
			String deviceId = descElt.getAttribute("deviceId");
			String version = descElt.getAttribute("version");
			String profileId = descElt.getAttribute("profileId");
			String nbServerCluster = descElt.getAttribute("nbServerCluster");
			String nbClientCluster = descElt.getAttribute("nbClientCluster");
			result = new ZigBeeSimpleDescriptorImpl(
					new Integer(deviceId).intValue(),
					Byte.parseByte(version),
					new Integer(profileId).intValue());
			result.setInputClusters(new int[Integer.parseInt(nbServerCluster)]);
			result.setOutputClusters(new int[Integer.parseInt(nbClientCluster)]);
		}
		return result;
	}

	private ZCLCluster[] getClientClusters(NodeList clientClusters) {

		Node node = clientClusters.item(0);
		ZCLCluster[] result = null;
		if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
			Element serverClustersElement = (Element) node;
			NodeList clusterList = serverClustersElement
					.getElementsByTagName("cluster");

			int clusterLength = clusterList.getLength();
			result = new ZCLCluster[clusterLength];
			for (int i = 0; i < clusterLength; i++) {

				ZCLClusterDescription serverClusterDescription = null;
				int[] ids = null;
				ZCLAttribute[] attributes = null;

				Node clusterNode = clusterList.item(i);
				if (clusterNode != null
						&& node.getNodeType() == Node.ELEMENT_NODE) {
					Element clusterElement = (Element) clusterNode;
					String clusterId = clusterElement.getAttribute("id");

					// get serverGlobalDescription
					NodeList globalDescList = clusterElement
							.getElementsByTagName("globalServerDescription");
					Node globalDesc = globalDescList.item(0);
					if (globalDesc != null
							&& globalDesc.getNodeType() == Node.ELEMENT_NODE) {
						Element desc = (Element) globalDesc;
						String id = desc.getAttribute("id");
						String name = desc.getAttribute("name");
						String domain = desc.getAttribute("domain");
						ZCLGlobalClusterDescription ZCLGlobalDesc = new ZCLGlobalClusterDescriptionImpl(
								new Integer(id).intValue(),
								name,
								domain,
								null,
								null);
						serverClusterDescription = new ZCLClusterDescriptionImpl(
								new Integer(clusterId).intValue(),
								ZCLGlobalDesc);
					}

					// get attributes
					clusterElement.getElementsByTagName("attributes");
					Node nodeAttributes = clientClusters.item(0);
					if (nodeAttributes != null
							&& nodeAttributes.getNodeType() == Node.ELEMENT_NODE) {
						Element attributesElement = (Element) nodeAttributes;
						NodeList attributeList = attributesElement
								.getElementsByTagName("attribute");

						int length = attributeList.getLength();
						attributes = new ZCLAttribute[length];

						for (int k = 0; k < length; k++) {
							Node attributeNode = attributeList.item(k);
							if (attributeNode != null
									&& attributeNode.getNodeType() == Node.ELEMENT_NODE) {
								Element attributeElement = (Element) node;
								ZCLAttribute ZCLAttr = getZCLAttribute(attributeElement);
								attributes[k] = ZCLAttr;
							}
						}
					}
				}
				ZCLClusterConf clusterImpl = new ZCLClusterConf(ids,
						attributes,
						serverClusterDescription);
				result[i] = clusterImpl;
			}
		}

		return result;
	}
}