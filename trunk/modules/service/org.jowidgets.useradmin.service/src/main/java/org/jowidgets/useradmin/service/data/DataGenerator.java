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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.jowidgets.useradmin.common.security.AuthKeys;
import org.jowidgets.useradmin.service.persistence.PersistenceUnitNames;
import org.jowidgets.useradmin.service.persistence.bean.Authorization;
import org.jowidgets.useradmin.service.persistence.bean.Person;
import org.jowidgets.useradmin.service.persistence.bean.PersonRoleLink;
import org.jowidgets.useradmin.service.persistence.bean.Role;
import org.jowidgets.useradmin.service.persistence.bean.RoleAuthorizationLink;

public final class DataGenerator {

	public void dropAndCreateData(final EntityManagerFactory entityManagerFactory) {
		dropData(entityManagerFactory);
		createData(entityManagerFactory);
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
		//CHECKSTYLE:OFF
		System.out.println("DATA DROPPED");
		//CHECKSTYLE:ON
	}

	public void createData(final EntityManagerFactory entityManagerFactory) {
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

		final PersonRoleLink personRoleLink = new PersonRoleLink();
		personRoleLink.setPerson(admin);
		personRoleLink.setRole(adminRole);
		em.persist(personRoleLink);

		for (final String authorizationKey : AuthKeys.ALL_AUTHORIZATIONS) {
			final Authorization authorization = new Authorization();
			authorization.setKey(authorizationKey);
			em.persist(authorization);

			final RoleAuthorizationLink roleAuthorizationLink = new RoleAuthorizationLink();
			roleAuthorizationLink.setRole(adminRole);
			roleAuthorizationLink.setAuthorization(authorization);
			em.persist(roleAuthorizationLink);
		}

		tx.commit();
		em.close();

		//CHECKSTYLE:OFF
		System.out.println("DATA CREATED");
		//CHECKSTYLE:ON
	}

	public static void main(final String[] args) {
		new DataGenerator().dropAndCreateData(Persistence.createEntityManagerFactory(PersistenceUnitNames.USER_ADMIN));
	}

}
