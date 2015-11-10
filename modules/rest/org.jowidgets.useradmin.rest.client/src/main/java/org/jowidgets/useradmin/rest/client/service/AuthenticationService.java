/*
 * Copyright (c) 2012, grossmann
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

package org.jowidgets.useradmin.rest.client.service;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.jowidgets.security.api.IAuthenticationService;
import org.jowidgets.security.api.ICredentials;
import org.jowidgets.security.api.IPrincipal;
import org.jowidgets.security.tools.DefaultPrincipal;
import org.jowidgets.useradmin.rest.api.Credentials;
import org.jowidgets.useradmin.rest.api.Principal;
import org.jowidgets.useradmin.rest.client.util.BasicAuthenticationHelper;
import org.jowidgets.useradmin.rest.client.util.UserAdminResourceFactory;
import org.jowidgets.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuthenticationService implements IAuthenticationService<IPrincipal<String>, ICredentials> {

	private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	@Override
	public IPrincipal<String> authenticate(final ICredentials credentials) {
		Assert.paramNotNull(credentials, "credentials");

		final String username = credentials.getUsername();
		if (username == null) {
			return null;
		}

		final String password = credentials.getPassword();

		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		final Builder requestBuilder = resourceFactory.getAuthenticationService().request();
		authenticationHelper.setBasicAuthentication(requestBuilder);

		try {
			final Response response = requestBuilder.post(Entity.entity(
					new Credentials(username, password),
					MediaType.APPLICATION_JSON));
			if (Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
				final Principal principal = response.readEntity(Principal.class);
				if (principal != null) {
					return new DefaultPrincipal(principal.getUsername());
				}
			}
			else {
				logger.error(response.getStatusInfo().getReasonPhrase());
			}
		}
		catch (final Exception e) {
			logger.error("Exception invoking rest service", e);
		}

		return null;
	}

}
