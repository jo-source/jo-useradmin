/*
 * Copyright (c) 2015, grossmann
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * * Neither the name of the jo-widgets.org nor the
 *   names of its contributors may be used to endorse or promote products
 *   derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL jo-widgets.org BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package org.jowidgets.useradmin.rest.client.example;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jowidgets.useradmin.rest.api.Credentials;
import org.jowidgets.useradmin.rest.api.Person;
import org.jowidgets.useradmin.rest.api.Principal;

public final class UseradminRestExampleClient {

	private static final String LOGIN_NAME = "admin";
	private static final String PASSWORD = "admin";

	private static final String SERVER_URL = "http://localhost:8081/";
	private static final String AUTHENTICATION_SERVICE = SERVER_URL + "service/security/AuthenticationService/authenticate";
	private static final String AUTHORIZATION_SERVICE = SERVER_URL + "service/security/AuthorizationService/authorize";

	private static final String PERSON_RESOURCE = SERVER_URL + "Person";
	private static final String PERSON_BY_LOGIN_NAME = PERSON_RESOURCE + "/personByLoginName";

	private UseradminRestExampleClient() {}

	public static void main(final String[] args) {
		final Client client = ClientBuilder.newClient();

		final WebTarget authenticationService = client.target(AUTHENTICATION_SERVICE);
		final Credentials credentials = new Credentials(LOGIN_NAME, PASSWORD);
		Response response = authenticationService.request().post(Entity.entity(credentials, MediaType.APPLICATION_JSON));

		Principal principal = response.readEntity(Principal.class);
		//CHECKSTYLE:OFF
		System.out.println("After authentication: " + principal);
		//CHECKSTYLE:ON

		final WebTarget authorizationService = client.target(AUTHORIZATION_SERVICE);
		response = authorizationService.request().post(Entity.entity(principal, MediaType.APPLICATION_JSON));
		principal = response.readEntity(Principal.class);
		//CHECKSTYLE:OFF
		System.out.println("After authorization: " + principal);
		//CHECKSTYLE:ON

		final WebTarget personResource = client.target(PERSON_BY_LOGIN_NAME);
		final Person person = personResource.queryParam("loginName", LOGIN_NAME).request().accept(MediaType.APPLICATION_JSON).get(
				Person.class);
		//CHECKSTYLE:OFF
		System.out.println("Person: " + person);
		//CHECKSTYLE:ON

	}
}
