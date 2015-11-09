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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceLoader;

import org.jowidgets.classloading.api.SharedClassLoader;
import org.jowidgets.util.io.IoUtils;

public final class UserAdminConfig {

	private static final String PROPERTIES_FILENAME = "useradmin.properties";
	private static final String URL = "url";
	private static final String LOGIN = "login";
	private static final String PWD = "pwd";

	private static final IUserAdminConfig INSTANCE = createInstance();

	private UserAdminConfig() {}

	public static IUserAdminConfig getInstance() {
		return INSTANCE;
	}

	private static IUserAdminConfig createInstance() {

		final ServiceLoader<IUserAdminConfig> serviceLoader = ServiceLoader.load(
				IUserAdminConfig.class,
				SharedClassLoader.getCompositeClassLoader());

		final Iterator<IUserAdminConfig> iterator = serviceLoader.iterator();
		if (iterator.hasNext()) {
			final IUserAdminConfig result = iterator.next();
			if (iterator.hasNext()) {
				throw new IllegalStateException("More than one implementation found for '"
					+ IUserAdminConfig.class.getName()
					+ "'");
			}
			return result;
		}
		else {
			return new DefaultUserAdminConfig();
		}
	}

	private static final class DefaultUserAdminConfig implements IUserAdminConfig {

		private final Properties properties;

		private DefaultUserAdminConfig() {
			this.properties = readProperties();
		}

		@Override
		public String getUrl() {
			return properties.getProperty(URL);
		}

		@Override
		public String getLogin() {
			return properties.getProperty(LOGIN);
		}

		@Override
		public String getPwd() {
			return properties.getProperty(PWD);
		}

		private Properties readProperties() {
			try {
				final Properties result = new Properties();
				final InputStream inputStream = UserAdminConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME);
				result.load(inputStream);
				IoUtils.tryCloseSilent(inputStream);
				return result;
			}
			catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

}
