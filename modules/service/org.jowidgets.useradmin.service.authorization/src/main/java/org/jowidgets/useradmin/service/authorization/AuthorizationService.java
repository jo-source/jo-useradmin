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

package org.jowidgets.useradmin.service.authorization;

import java.util.Collections;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jowidgets.cap.service.jpa.api.EntityManagerFactoryProvider;
import org.jowidgets.security.api.IAuthorizationService;
import org.jowidgets.security.api.IPrincipal;
import org.jowidgets.security.tools.DefaultPrincipal;
import org.jowidgets.useradmin.service.persistence.PersistenceUnitNames;
import org.jowidgets.useradmin.service.persistence.bean.Person;
import org.jowidgets.useradmin.service.persistence.dao.PersonDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuthorizationService implements IAuthorizationService<IPrincipal<String>> {

	private final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

	private final EntityManagerFactory entityManagerFactory;

	public AuthorizationService() {
		this.entityManagerFactory = EntityManagerFactoryProvider.get(PersistenceUnitNames.USER_ADMIN);
	}

	@Override
	public IPrincipal<String> authorize(final IPrincipal<String> principal) {
		final String username = principal.getUsername();
		return new DefaultPrincipal(username, getAuthorizations(username));
	}

	private Set<String> getAuthorizations(final String username) {
		EntityManager em = null;
		if (username != null) {
			try {
				em = entityManagerFactory.createEntityManager();
				final Person person = PersonDAO.findPersonByLogin(em, username, true);
				if (person != null) {
					return person.getAuthorizationNames();
				}
			}
			catch (final Exception e) {
				logger.error("Error on authorization", e);
			}
			finally {
				if (em != null) {
					try {
						em.close();
					}
					catch (final Exception e) {
						logger.error("Error on authorization", e);
					}
				}
			}
		}
		return Collections.emptySet();
	}

}
