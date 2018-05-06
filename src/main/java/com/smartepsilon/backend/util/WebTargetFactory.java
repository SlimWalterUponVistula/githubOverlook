package com.smartepsilon.backend.util;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class WebTargetFactory {
	
	public static WebTarget create(final String uriBasepath) {
		Client client = ClientBuilder.newClient();
	 	return client.target(uriBasepath);
	}
}
