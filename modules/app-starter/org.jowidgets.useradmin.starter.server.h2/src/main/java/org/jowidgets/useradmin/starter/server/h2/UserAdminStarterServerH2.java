/*
 * Copyright (c) 2011, H.Westphal
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

package org.jowidgets.useradmin.starter.server.h2;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jowidgets.cap.remoting.common.RemotingBrokerId;
import org.jowidgets.cap.tools.starter.server.CapServerStarter;
import org.jowidgets.message.api.IExceptionCallback;
import org.jowidgets.message.api.MessageToolkit;
import org.jowidgets.security.impl.http.server.BasicAuthenticationFilter;
import org.jowidgets.security.impl.http.server.SecurityRemotingServlet;

public final class UserAdminStarterServerH2 {

	private UserAdminStarterServerH2() {}

	public static void main(final String[] args) throws Exception {
		MessageToolkit.addExceptionCallback(RemotingBrokerId.DEFAULT_BROKER_ID, new IExceptionCallback() {
			@Override
			public void exception(final Throwable throwable) {
				//CHECKSTYLE:OFF
				throwable.printStackTrace();
				//CHECKSTYLE:ON
			}
		});
		final Server server = new Server(8080);
		final ServletContextHandler root = new ServletContextHandler(null, "/useradmin-h2-web", ServletContextHandler.SESSIONS);
		root.addServlet(new ServletHolder(new SecurityRemotingServlet(RemotingBrokerId.DEFAULT_BROKER_ID)), "/remoting/");

		final ServletHolder servlet = root.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/rest/*");
		servlet.setInitParameter("jersey.config.server.provider.packages", "org.jowidgets.useradmin.rest");

		root.addFilter(new FilterHolder(new BasicAuthenticationFilter()), "/*", FilterMapping.DEFAULT);
		server.setHandler(root);
		server.start();
		server.join();
		CapServerStarter.startServer();
	}
}
