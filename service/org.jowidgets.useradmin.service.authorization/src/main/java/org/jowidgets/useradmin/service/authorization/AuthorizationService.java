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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jowidgets.cap.service.jpa.api.EntityManagerFactoryProvider;
import org.jowidgets.security.api.IAuthorizationService;
import org.jowidgets.security.api.IPrincipal;
import org.jowidgets.security.tools.DefaultPrincipal;
import org.jowidgets.useradmin.common.bean.IPerson;
import org.jowidgets.useradmin.service.persistence.bean.Authorization;
import org.jowidgets.useradmin.service.persistence.bean.Person;
import org.jowidgets.useradmin.service.persistence.bean.PersonRoleLink;
import org.jowidgets.useradmin.service.persistence.bean.Role;
import org.jowidgets.useradmin.service.persistence.bean.RoleAuthorizationLink;

public final class AuthorizationService implements IAuthorizationService<IPrincipal<String>> {

	private final EntityManagerFactory entityManagerFactory;

	public AuthorizationService() {
		this.entityManagerFactory = EntityManagerFactoryProvider.get("userAdminPersistenceUnit");
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
				return getAuthorizations(getPerson(em, username));

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

	private Person getPerson(final EntityManager em, final String username) {
		try {
			final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			final CriteriaQuery<Person> query = criteriaBuilder.createQuery(Person.class);
			final Root<?> root = query.from(Person.class);
			final Path<String> loginPath = root.get(IPerson.LOGIN_NAME_PROPERTY);
			final String usernameUpper = username != null ? username.toUpperCase() : null;
			final Predicate predicate = criteriaBuilder.like(criteriaBuilder.upper(loginPath), usernameUpper);
			query.where(predicate);
			return em.createQuery(query).getSingleResult();
		}
		catch (final NoResultException e) {
			return null;
		}
	}

	private Set<String> getAuthorizations(final Person person) {
		final Set<String> result = new HashSet<String>();
		if (person != null) {
			for (final PersonRoleLink personRoleLink : person.getPersonRoleLinks().values()) {
				result.addAll(getAuthorizations(personRoleLink.getRole()));
			}
		}
		return result;
	}

	private Set<String> getAuthorizations(final Role role) {
		final Set<String> result = new HashSet<String>();
		if (role != null) {
			for (final RoleAuthorizationLink roleAuthorizationLink : role.getRoleAuthorizationLinks()) {
				final Authorization authorization = roleAuthorizationLink.getAuthorization();
				result.add(authorization.getKey());
			}
		}
		return result;
	}
}
