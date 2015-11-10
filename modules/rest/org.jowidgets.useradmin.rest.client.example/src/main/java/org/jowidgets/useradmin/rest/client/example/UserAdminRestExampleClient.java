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

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jowidgets.useradmin.rest.api.Authorization;
import org.jowidgets.useradmin.rest.api.Credentials;
import org.jowidgets.useradmin.rest.api.PasswordChangeRequest;
import org.jowidgets.useradmin.rest.api.PasswordChangeResult;
import org.jowidgets.useradmin.rest.api.Person;
import org.jowidgets.useradmin.rest.api.Principal;
import org.jowidgets.useradmin.rest.api.Role;
import org.jowidgets.useradmin.rest.client.util.BasicAuthenticationHelper;
import org.jowidgets.useradmin.rest.client.util.UserAdminResourceFactory;
import org.jowidgets.util.security.String2Hash;

public final class UserAdminRestExampleClient {

	private static final String LOGIN_NAME = "admin";
	private static final String PASSWORD = "admin";
	private static final String PASSWORD_2 = "admin2";

	private static final int AUTHORIZATION_COUNT = 10;

	private UserAdminRestExampleClient() {}

	public static void main(final String[] args) {
		final Principal principal = requestAuthenticationService();
		requestAuthorizationService(principal);

		changePassword(PASSWORD, PASSWORD_2);

		changePassword(PASSWORD_2, PASSWORD);

		requestPersonResource();

		createRole();
		createAuthorizations();

		updateRole();
		updateAuthorizations();

		addAuthorizations();

		removeAuthorizations();

		deleteAuthorizations();
		deleteRole();
	}

	private static Principal requestAuthenticationService() {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		final WebTarget authenticationService = resourceFactory.getAuthenticationService();
		final Builder requestBuilder = authenticationService.request().accept(MediaType.APPLICATION_JSON);
		authenticationHelper.setBasicAuthentication(requestBuilder);

		final Response response = requestBuilder.post(Entity.entity(
				new Credentials(LOGIN_NAME, PASSWORD),
				MediaType.APPLICATION_JSON));

		final Principal principal = response.readEntity(Principal.class);
		//CHECKSTYLE:OFF
		System.out.println("After authentication: " + principal);
		//CHECKSTYLE:ON

		return principal;
	}

	private static void requestAuthorizationService(Principal principal) {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		final WebTarget authorizationService = resourceFactory.getAuthorizationService();
		final Builder requestBuilder = authorizationService.request();
		authenticationHelper.setBasicAuthentication(requestBuilder);

		final Response response = requestBuilder.post(Entity.entity(principal, MediaType.APPLICATION_JSON));
		principal = response.readEntity(Principal.class);
		//CHECKSTYLE:OFF
		System.out.println("After authorization: " + principal);
		//CHECKSTYLE:ON
	}

	private static void changePassword(final String oldPassword, final String newPassword) {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		final WebTarget passwordChangeService = resourceFactory.getPasswordChangeService();
		final Builder requestBuilder = passwordChangeService.request();

		//password will be changed for authorized user, so use login and old password for authentication
		authenticationHelper.setBasicAuthentication(requestBuilder, LOGIN_NAME, oldPassword);

		final PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(oldPassword, newPassword);
		final Response response = requestBuilder.post(Entity.entity(passwordChangeRequest, MediaType.APPLICATION_JSON));

		final PasswordChangeResult passwordChangeResult = response.readEntity(PasswordChangeResult.class);
		//CHECKSTYLE:OFF
		System.out.println("Password changed: " + passwordChangeResult);
		//CHECKSTYLE:ON
	}

	private static void requestPersonResource() {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		final WebTarget personResource = resourceFactory.getPerson(LOGIN_NAME);
		final Builder requestBuilder = personResource.request();
		authenticationHelper.setBasicAuthentication(requestBuilder);
		final Person person = requestBuilder.accept(MediaType.APPLICATION_JSON).get(Person.class);
		//CHECKSTYLE:OFF
		System.out.println("Authenticated: " + String2Hash.encode(PASSWORD).equals(person.getPasswordHash()) + ", " + person);
		//CHECKSTYLE:ON
	}

	private static void createAuthorizations() {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		final WebTarget authorizationsResource = resourceFactory.getAuthorizations();
		final Builder requestBuilder = authorizationsResource.request();
		authenticationHelper.setBasicAuthentication(requestBuilder);
		for (int i = 0; i < AUTHORIZATION_COUNT; i++) {
			final Authorization authorization = new Authorization();
			authorization.setKey("TEST_AUTHORIZATION_" + i);
			authorization.setDescription("TEST_AUTHORIZATION_DESCRIPTION_" + i);
			requestBuilder.put(Entity.entity(authorization, MediaType.APPLICATION_JSON));
		}
	}

	private static void updateAuthorizations() {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		final WebTarget authorizationsResource = resourceFactory.getAuthorizations();
		final Builder requestBuilder = authorizationsResource.request();
		authenticationHelper.setBasicAuthentication(requestBuilder);
		for (int i = 0; i < AUTHORIZATION_COUNT; i++) {
			final Authorization authorization = new Authorization();
			authorization.setKey("TEST_AUTHORIZATION_" + i);
			authorization.setDescription("DESCRIPTION_NEW_" + i);
			requestBuilder.put(Entity.entity(authorization, MediaType.APPLICATION_JSON));
		}
	}

	private static void deleteAuthorizations() {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		for (int i = 0; i < AUTHORIZATION_COUNT; i++) {
			final WebTarget authorizationsResource = resourceFactory.getAuthorization("TEST_AUTHORIZATION_" + i);
			final Builder requestBuilder = authorizationsResource.request();
			authenticationHelper.setBasicAuthentication(requestBuilder);
			requestBuilder.delete();
		}
	}

	private static void createRole() {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		final WebTarget resource = resourceFactory.getRoles();
		final Builder requestBuilder = resource.request();
		authenticationHelper.setBasicAuthentication(requestBuilder);

		final Role role = new Role();
		role.setName("TEST_ROLE");
		role.setDescription("TEST_ROLE_DESCRIPTION");
		requestBuilder.put(Entity.entity(role, MediaType.APPLICATION_JSON));
	}

	private static void updateRole() {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		final WebTarget resource = resourceFactory.getRoles();
		final Builder requestBuilder = resource.request();
		authenticationHelper.setBasicAuthentication(requestBuilder);

		final Role role = new Role();
		role.setName("TEST_ROLE");
		role.setDescription("TEST_ROLE_DESCRIPTION_NEW");
		requestBuilder.put(Entity.entity(role, MediaType.APPLICATION_JSON));
	}

	private static void deleteRole() {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		final WebTarget resource = resourceFactory.getRole("TEST_ROLE");
		final Builder requestBuilder = resource.request();
		authenticationHelper.setBasicAuthentication(requestBuilder);
		requestBuilder.delete();
	}

	private static void addAuthorizations() {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		for (int i = 0; i < AUTHORIZATION_COUNT; i++) {
			final WebTarget resource = resourceFactory.getRole("TEST_ROLE");
			final Builder requestBuilder = resource.request();
			authenticationHelper.setBasicAuthentication(requestBuilder);
			final Authorization authorization = new Authorization();
			authorization.setKey("TEST_AUTHORIZATION_" + i);
			requestBuilder.put(Entity.entity(authorization, MediaType.APPLICATION_JSON));
		}
	}

	private static void removeAuthorizations() {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		for (int i = 0; i < AUTHORIZATION_COUNT; i++) {
			final WebTarget resource = resourceFactory.getRoleAuthorization("TEST_ROLE", "TEST_AUTHORIZATION_" + i);
			final Builder requestBuilder = resource.request();
			authenticationHelper.setBasicAuthentication(requestBuilder);
			requestBuilder.delete();
		}
	}

}
