/*******************************************************************************
 * Copyright (c) 2015 Institute for Pervasive Computing, ETH Zurich and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 *
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 ******************************************************************************/
package uk.ac.soton.itinnovation.xifiinteroperability;

import java.io.IOException;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.proxy.resources.ForwardingResource;
import org.eclipse.californium.proxy.resources.ProxyCoapClientResource;

/**
 *
 * CoAP2CoAP: Insert in Copper:
 *     URI: coap://localhost:PORT/PATH
 *
 */
public class COAPProxyExample {

	private static final int PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);
        private static final String targetURL = "coap://coap.me:5683/test";
        private static final String proxyPath = "test";

	private CoapServer targetServerA;

	public COAPProxyExample() throws IOException {
		ForwardingResource coap2coap = new ProxyCoapClientResource(proxyPath, targetURL);

		// Create CoAP Server on PORT with proxy resources form CoAP to CoAP and HTTP
		targetServerA = new CoapServer(PORT);
		targetServerA.add(coap2coap);
		targetServerA.start();

		System.out.println("CoAP Proxy at coap://localhost:" + PORT + "/" + proxyPath);
                System.out.println("Proxy redirects to:" + targetURL);
	}

	public static void main(String[] args) throws Exception {
		new COAPProxyExample();
	}

}