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

import javax.ws.rs.client.Invocation;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.jowidgets.useradmin.rest.client.config.IUserAdminConfig;
import org.jowidgets.useradmin.rest.client.config.UserAdminConfig;
import org.jowidgets.util.Assert;

public final class BasicAuthenticationHelper {

	private final IUserAdminConfig config;

	public BasicAuthenticationHelper() {
		this(UserAdminConfig.getInstance());
	}

	public BasicAuthenticationHelper(final IUserAdminConfig config) {
		Assert.paramNotNull(config, "config");
		this.config = config;
	}

	public void setBasicAuthentication(final Invocation.Builder invocationBuilder) {
		setBasicAuthentication(invocationBuilder, config.getLogin(), config.getPwd());
	}

	public void setBasicAuthentication(final Invocation.Builder invocationBuilder, final String login, final String pwd) {
		final String credentials = login + ":" + pwd;
		final String encodedCredentials = Base64.encodeBase64String(StringUtils.getBytesUtf8(credentials));
		invocationBuilder.header("Authorization", "Basic " + encodedCredentials);
	}

}
