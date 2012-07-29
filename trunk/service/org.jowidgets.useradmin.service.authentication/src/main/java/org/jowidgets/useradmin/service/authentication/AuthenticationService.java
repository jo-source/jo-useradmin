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

package org.jowidgets.useradmin.service.authentication;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jowidgets.cap.service.jpa.api.EntityManagerFactoryProvider;
import org.jowidgets.security.api.IAuthenticationService;
import org.jowidgets.security.tools.DefaultCredentials;
import org.jowidgets.security.tools.DefaultPrincipal;
import org.jowidgets.useradmin.service.persistence.PersistenceUnitNames;
import org.jowidgets.useradmin.service.persistence.bean.Person;
import org.jowidgets.useradmin.service.persistence.dao.PersonDAO;

public final class AuthenticationService implements IAuthenticationService<DefaultPrincipal, DefaultCredentials> {

	private final EntityManagerFactory entityManagerFactory;

	public AuthenticationService() {
		this.entityManagerFactory = EntityManagerFactoryProvider.get(PersistenceUnitNames.USER_ADMIN);
	}

	@Override
	public DefaultPrincipal authenticate(final DefaultCredentials credentials) {

		final String username = credentials.getUsername();
		final String password = credentials.getPassword();

		EntityManager em = null;
		if (username != null && password != null) {
			try {
				em = entityManagerFactory.createEntityManager();
				final Person person = PersonDAO.findPersonByLogin(em, username, true);
				if (person != null && person.isAuthenticated(password)) {
					return new DefaultPrincipal(username);
				}
			}
			catch (final Exception e) {
				//TODO log exception
				return null;
			}
			finally {
				if (em != null) {
					try {
						em.close();
					}
					catch (final Exception e) {
						//TODO log exception
						return null;
					}
				}
			}
		}
		return null;
	}

}
