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

package org.jowidgets.useradmin.rest.client.util;

import java.util.Collection;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.jowidgets.useradmin.rest.api.Authorization;
import org.jowidgets.useradmin.rest.api.Role;

public final class DefaultAuthorisationsCreator {

	private DefaultAuthorisationsCreator() {}

	public static void createDefaultRoleAndAuthorizations(
		final String roleName,
		final String roleDescription,
		final Collection<String> authorizations) {
		createDefaultRole(roleName, roleDescription);
		addDefaultAuthorizations(roleName, authorizations);
	}

	private static void createDefaultRole(final String roleName, final String roleDescription) {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		final WebTarget resource = resourceFactory.getRoles();
		final Builder requestBuilder = resource.request();
		authenticationHelper.setBasicAuthentication(requestBuilder);

		final Role role = new Role();
		role.setName(roleName);
		role.setDescription(roleDescription);
		requestBuilder.put(Entity.entity(role, MediaType.APPLICATION_JSON));
	}

	private static void addDefaultAuthorizations(final String roleName, final Collection<String> authorizations) {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		for (final String authorizationKey : authorizations) {
			final WebTarget resource = resourceFactory.getRole(roleName);
			final Builder requestBuilder = resource.request();
			authenticationHelper.setBasicAuthentication(requestBuilder);
			final Authorization authorization = new Authorization();
			authorization.setKey(authorizationKey);
			requestBuilder.put(Entity.entity(authorization, MediaType.APPLICATION_JSON));
		}
	}

}
