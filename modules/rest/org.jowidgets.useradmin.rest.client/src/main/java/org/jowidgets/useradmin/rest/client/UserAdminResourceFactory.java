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

package org.jowidgets.useradmin.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.jowidgets.util.Assert;

public final class UserAdminResourceFactory {

	private static final String AUTHENTICATION_SERVICE = "service/security/authenticate";
	private static final String AUTHORIZATION_SERVICE = "service/security/authorize";

	private static final String PERSONS_RESOURCE = "persons";

	private final String url;
	private final Client client;

	public UserAdminResourceFactory() {
		this(UserAdminConfig.getInstance());
	}

	public UserAdminResourceFactory(final IUserAdminConfig config) {
		Assert.paramNotNull(config, "config");
		this.url = config.getUrl();
		this.client = ClientBuilder.newClient();
	}

	public WebTarget getPerson(final String login) {
		Assert.paramNotEmpty(login, "login");
		return client.target(url + PERSONS_RESOURCE + "/" + login);
	}

	public WebTarget getAuthenticationService() {
		return client.target(url + AUTHENTICATION_SERVICE);
	}

	public WebTarget getAuthorizationService() {
		return client.target(url + AUTHORIZATION_SERVICE);
	}
}
