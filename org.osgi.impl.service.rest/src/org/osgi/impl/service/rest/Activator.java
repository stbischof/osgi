/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.rest;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * Bundle activator for the REST service RI
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public class Activator implements BundleActivator {

	private Component	component;

	public void start(final BundleContext context) throws Exception {
		component = new Component();
		component.getServers().add(Protocol.HTTP, 8888);
		component.getClients().add(Protocol.CLAP);
		component.getDefaultHost().attach("", new RestService(context));
		component.start();
	}

	public void stop(final BundleContext context) throws Exception {
		component.stop();
		component = null;
	}

}