package org.osgi.test.cases.residentialmanagement;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public abstract class RMTTestBase extends DefaultTestBundleControl implements
		RMTConstants {

	DmtAdmin dmtAdmin;
	DmtSession session;
	
	Bundle testBundle1 = null;
	Bundle testBundle2 = null;
	Bundle testBundle3 = null;
	Bundle testBundle4 = null;

	private static Set<String> operations;

	static {
		operations = new HashSet<String>();
		operations.add("A");
		operations.add("G");
		operations.add("R");
		operations.add("D");
		// what about EXECUTE?
//		operations.add("E");
	}

	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("setting up");
		dmtAdmin = getService(DmtAdmin.class);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("tearing down");
		if (this.testBundle1 != null
				&& testBundle1.getState() != Bundle.UNINSTALLED)
			this.testBundle1.uninstall();
		this.testBundle1 = null;
		if (this.testBundle2 != null
				&& testBundle2.getState() != Bundle.UNINSTALLED)
			this.testBundle2.uninstall();
		this.testBundle2 = null;
		if (this.testBundle3 != null
				&& testBundle3.getState() != Bundle.UNINSTALLED)
			this.testBundle3.uninstall();
		this.testBundle3 = null;
		if (this.testBundle4 != null
				&& testBundle4.getState() != Bundle.UNINSTALLED)
			this.testBundle4.uninstall();
		this.testBundle4 = null;

		if (session != null && session.getState() == DmtSession.STATE_OPEN)
			session.close();
		unregisterAllServices();
		ungetAllServices();
	}

	
	// -----Utilities-----
	Bundle installAndStartBundle(String location) throws IOException,
			BundleException {
		URL url = getContext().getBundle().getResource(location);
		InputStream is = url.openStream();
		Bundle bundle = getContext().installBundle(location, is);
		bundle.start();
		is.close();
		return bundle;
	}

	/**
	 * takes the nodes uri and checks meta-data against the formal descriptions from the TreeSummary 
	 * @param uri
	 * @param can ... encoded String from the Tree summary
	 * @param cardinality
	 * @param scope
	 * @throws Exception
	 */
	void assertMetaData( String uri, boolean isLeaf, String canStr, String cardinality, int scope, int format) throws Exception {
		MetaNode metaNode = session.getMetaNode(uri);
		assertNotNull("The metadata for " + uri + " must not be null!", metaNode);
		
		assertEquals( uri + " must " + (isLeaf ? "":" not ") + " be a leaf node", isLeaf, metaNode.isLeaf() );
		assertOperations(uri, metaNode, canStr);
		assertCardinality(uri, metaNode, cardinality);

		assertEquals( uri + " must have scope: " + scope, scope, metaNode.getScope() );
		assertEquals( "The MetaData of " + uri + " has a wrong data format.", format, metaNode.getFormat() );
	}
	
	
	String getBundleStateString( int state ) {
		switch (state) {
		case Bundle.ACTIVE:
			return "ACTIVE";
		case Bundle.INSTALLED:
			return "INSTALLED";
		case Bundle.RESOLVED:
			return "RESOLVED";
		case Bundle.STARTING:
			return "STARTING";
		case Bundle.STOPPING:
			return "STOPPING";
		case Bundle.UNINSTALLED:
			return "UNINSTALLED";
		}
		return null;
	}
	
	void dumpTree(DmtSession session, String uri ) throws Exception {
		System.out.println( uri );
		if ( session.isLeafNode(uri) )
			System.out.println( ">>>" + session.getNodeValue(uri));
		else {
			String[] children = session.getChildNodeNames(uri);
			for (String child : children)
				dumpTree(session, uri + "/" + child );
		}
	}

	/**
	 * returns the pathes of all file entries in the given bundle recursively
	 * directory entries are filtered out
	 * @param bundle
	 * @return
	 */
	Set<String> getBundleEntries( Bundle bundle, boolean encode ) {
		Set<String> entries = new HashSet<String>();
		addBundleEntryFolder(entries, bundle, "", encode);
		return entries;
	}
	
	private void addBundleEntryFolder(Set<String> results, Bundle bundle, String folder, boolean encode ) {
		Enumeration<String> pathes = bundle.getEntryPaths(folder);
		while (pathes.hasMoreElements()) {
			// filter out directories
			String path = pathes.nextElement();
			if ( path.endsWith("/"))
				addBundleEntryFolder(results, bundle, path, encode );
			else {
				if ( encode )
					results.add(Uri.encode(path));
				else 
					results.add(path);
			}
		}
	}
	
	/**
	 * asserts that the metanode reports support for the specified operations
	 * @param uri ... the uri that the metadata belongs to
	 * @param metanode ... the metanode to be checked
	 * @param canStr ... a string defining the assumed capabilities of the node (eg. "_GR_" or "AGRD")
	 */
	private void assertOperations( final String uri, final MetaNode metaNode, final String canStr ) {
		for (int i = 0; i < canStr.length(); i++) {
			boolean should = operations.contains( canStr.substring(i,i+1) );
			// switch cases are in the order of the encoded actions in canStr ("AGRD")
			// TODO: what about EXECUTE ?
			switch (i) {
			case 0:
				assertEquals( uri + " must " + (should ? "":" not ") + " support ADD operation!", should, metaNode.can( MetaNode.CMD_ADD ) );
				break;
			case 1:
				assertEquals( uri + " must " + (should ? "":" not ") + " support GET operation!", should, metaNode.can( MetaNode.CMD_GET ) );
				break;
			case 2:
				assertEquals( uri + " must " + (should ? "":" not ") + " support REPLACE operation!", should, metaNode.can( MetaNode.CMD_REPLACE ) );
				break;
			case 3:
				assertEquals( uri + " must " + (should ? "":" not ") + " support DELETE operation!", should, metaNode.can( MetaNode.CMD_DELETE ) );
				break;
	
			default:
				break;
			}
		}
	}
	
	/**
	 * asserts that the metanode reports the correct cardinality
	 * @param uri ... the uri that the metadata belongs to
	 * @param metaNode ... the metanode to be checked
	 * @param cardinality ... a String defining the specified cardinalities (e.g. "0,1", "0..*" or "1")
	 */
	private void assertCardinality(final String uri, final MetaNode metaNode, String cardinality ) {
		// check first character
		boolean zeroAllowed = "0".equals(cardinality.substring(0,1));
		// check last character
		long max = "*".equals(cardinality.substring(cardinality.length()-1,cardinality.length())) ? Integer.MAX_VALUE : 1;
		assertEquals( "The MetaData of " + uri + " provides wrong value for 'zero occurrence allowed'!", zeroAllowed, metaNode.isZeroOccurrenceAllowed() );
		assertTrue( "The MetaData of " + uri + " provides wrong value for max occurence.", metaNode.getMaxOccurrence() > 0 && metaNode.getMaxOccurrence() <= max );
	}

	


}