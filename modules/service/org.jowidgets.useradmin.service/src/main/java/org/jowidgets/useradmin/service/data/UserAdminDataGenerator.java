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

package org.jowidgets.useradmin.service.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.jowidgets.useradmin.common.security.AuthKeys;
import org.jowidgets.useradmin.service.persistence.UseradminPersistenceUnitNames;
import org.jowidgets.useradmin.service.persistence.bean.Authorization;
import org.jowidgets.useradmin.service.persistence.bean.Person;
import org.jowidgets.useradmin.service.persistence.bean.PersonRoleLink;
import org.jowidgets.useradmin.service.persistence.bean.Role;
import org.jowidgets.useradmin.service.persistence.bean.RoleAuthorizationLink;
import org.jowidgets.util.EmptyCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UserAdminDataGenerator {

	private final Logger logger = LoggerFactory.getLogger(UserAdminDataGenerator.class);

	public void dropAndCreateData() {
		final Set<String> authorizations = Collections.emptySet();
		dropAndCreateData(null, authorizations);
	}

	public void dropAndCreateData(final String persistenceUnitName, final String roleName, final Collection<String> authorizations) {
		dropAndCreateData(Persistence.createEntityManagerFactory(persistenceUnitName), roleName, authorizations);
	}

	public void dropAndCreateData(final String roleName, final Collection<String> authorizations) {
		dropAndCreateData(UseradminPersistenceUnitNames.USER_ADMIN, roleName, authorizations);
	}

	public void dropAndCreateData(final EntityManagerFactory entityManagerFactory) {
		final Set<String> authorizations = Collections.emptySet();
		dropAndCreateData(entityManagerFactory, null, authorizations);
	}

	public void dropAndCreateData(
		final EntityManagerFactory entityManagerFactory,
		final String roleName,
		final Collection<String> authorizations) {
		dropData(entityManagerFactory);
		createData(entityManagerFactory, roleName, authorizations);
	}

	public void dropData(final EntityManagerFactory entityManagerFactory) {
		final EntityManager em = entityManagerFactory.createEntityManager();
		final EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.createQuery("delete from RoleAuthorizationLink").executeUpdate();
		em.createQuery("delete from PersonRoleLink").executeUpdate();
		em.createQuery("delete from Person").executeUpdate();
		em.createQuery("delete from Role").executeUpdate();
		em.createQuery("delete from Authorization").executeUpdate();
		tx.commit();
		em.close();

		logger.info("DATA DROPPED");
	}

	public void createDataIfRoleNotExists(
		final String persistenceUnitName,
		final String roleName,
		final Collection<String> authorizations) {
		final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
		final EntityManager em = entityManagerFactory.createEntityManager();

		final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		final CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
		final Root<Role> root = criteriaQuery.from(Role.class);
		final Path<Object> path = root.get(Role.NAME_PROPERTY);
		criteriaQuery.where(path.in(roleName));

		if (EmptyCheck.isEmpty(em.createQuery(criteriaQuery).getResultList())) {
			createData(entityManagerFactory, roleName, authorizations);
			logger.info("#############################################################Data created for role: " + roleName);
		}
	}

	public void createData(
		final EntityManagerFactory entityManagerFactory,
		final String roleName,
		final Collection<String> authorizations) {
		final EntityManager em = entityManagerFactory.createEntityManager();
		final EntityTransaction tx = em.getTransaction();
		tx.begin();

		final Person admin = new Person();
		admin.setLoginName("admin");
		admin.setName("Administrator");
		admin.setActive(Boolean.TRUE);
		em.persist(admin);

		final Role adminRole = new Role();
		adminRole.setName("USER_ADMIN");
		adminRole.setDescription("Holds all authorizations neccessary for the user administration");
		em.persist(adminRole);

		PersonRoleLink personRoleLink = new PersonRoleLink();
		personRoleLink.setPerson(admin);
		personRoleLink.setRole(adminRole);
		em.persist(personRoleLink);

		createAuthorizations(em, adminRole, AuthKeys.ALL_AUTHORIZATIONS);

		if (roleName != null) {
			final Role aditionalRole = new Role();
			aditionalRole.setName(roleName);
			em.persist(aditionalRole);

			personRoleLink = new PersonRoleLink();
			personRoleLink.setPerson(admin);
			personRoleLink.setRole(aditionalRole);
			em.persist(personRoleLink);

			createAuthorizations(em, aditionalRole, authorizations);
		}

		tx.commit();
		em.close();

		logger.info("DATA CREATED");
	}

	private void createAuthorizations(final EntityManager em, final Role adminRole, final Collection<String> authorizations) {
		for (final String authorizationKey : authorizations) {
			final Authorization authorization = new Authorization();
			authorization.setKey(authorizationKey);
			em.persist(authorization);

			final RoleAuthorizationLink roleAuthorizationLink = new RoleAuthorizationLink();
			roleAuthorizationLink.setRole(adminRole);
			roleAuthorizationLink.setAuthorization(authorization);
			em.persist(roleAuthorizationLink);
		}
	}

	public static void main(final String[] args) {
		new UserAdminDataGenerator().dropAndCreateData(Persistence.createEntityManagerFactory(UseradminPersistenceUnitNames.USER_ADMIN));
	}

}
