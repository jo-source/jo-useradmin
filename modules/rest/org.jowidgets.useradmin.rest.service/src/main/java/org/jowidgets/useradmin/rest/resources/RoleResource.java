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

package org.jowidgets.useradmin.rest.resources;

import javax.persistence.EntityManager;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jowidgets.cap.service.api.transaction.ITransactionTemplate;
import org.jowidgets.cap.service.jpa.api.EntityManagerFactoryProvider;
import org.jowidgets.cap.service.jpa.api.JpaTransactionTemplate;
import org.jowidgets.cap.service.jpa.tools.entity.EntityManagerProvider;
import org.jowidgets.useradmin.common.security.AuthKeys;
import org.jowidgets.useradmin.rest.authorization.AuthorizationChecker;
import org.jowidgets.useradmin.service.persistence.UseradminPersistenceUnitNames;
import org.jowidgets.useradmin.service.persistence.bean.Authorization;
import org.jowidgets.useradmin.service.persistence.bean.Role;
import org.jowidgets.useradmin.service.persistence.bean.RoleAuthorizationLink;
import org.jowidgets.useradmin.service.persistence.dao.AuthorizationDAO;
import org.jowidgets.useradmin.service.persistence.dao.RoleAuthorizationLinkDAO;
import org.jowidgets.useradmin.service.persistence.dao.RoleDAO;
import org.jowidgets.util.EmptyCheck;

@Path("roles")
public final class RoleResource {

	private final ITransactionTemplate transactionTemplate;

	public RoleResource() {
		this.transactionTemplate = JpaTransactionTemplate.create(EntityManagerFactoryProvider.get(UseradminPersistenceUnitNames.USER_ADMIN));
	}

	@PUT
	@Path("")
	public void addOrModify(final org.jowidgets.useradmin.rest.api.Role role) {
		AuthorizationChecker.check(AuthKeys.CREATE_ROLE);
		AuthorizationChecker.check(AuthKeys.UPDATE_ROLE);
		if (role == null || EmptyCheck.isEmpty(role.getName())) {
			return;
		}
		transactionTemplate.doInTransaction(new Runnable() {
			@Override
			public void run() {
				addOrModifyInTransaction(role);
			}
		});
	}

	@DELETE
	@Path("{key}")
	public void delete(@PathParam("key") final String key) {
		AuthorizationChecker.check(AuthKeys.DELETE_ROLE);
		if (EmptyCheck.isEmpty(key)) {
			return;
		}
		transactionTemplate.doInTransaction(new Runnable() {
			@Override
			public void run() {
				deleteInTransaction(key);
			}
		});
	}

	@PUT
	@Path("{role-name}")
	public void addAuthorization(
		@PathParam("role-name") final String roleName,
		final org.jowidgets.useradmin.rest.api.Authorization authorization) {
		AuthorizationChecker.check(AuthKeys.CREATE_AUTHORIZATION);
		AuthorizationChecker.check(AuthKeys.CREATE_ROLE_AUTHORIZATION_LINK);
		if (EmptyCheck.isEmpty(roleName) || authorization == null || EmptyCheck.isEmpty(authorization.getKey())) {
			return;
		}
		transactionTemplate.doInTransaction(new Runnable() {
			@Override
			public void run() {
				addAuthorizationInTransaction(roleName, authorization);
			}
		});
	}

	@DELETE
	@Path("{role-name}/{authorization-key}")
	public void deleteAuthorization(
		@PathParam("role-name") final String roleName,
		@PathParam("authorization-key") final String authorizationKey) {
		AuthorizationChecker.check(AuthKeys.DELETE_ROLE_AUTHORIZATION_LINK);
		if (EmptyCheck.isEmpty(roleName) || EmptyCheck.isEmpty(authorizationKey)) {
			return;
		}
		transactionTemplate.doInTransaction(new Runnable() {
			@Override
			public void run() {
				deleteAuthorizationInTransaction(roleName, authorizationKey);
			}
		});
	}

	private void addOrModifyInTransaction(final org.jowidgets.useradmin.rest.api.Role role) {
		final EntityManager em = EntityManagerProvider.get();
		final Role existingRole = findRole(role.getName());
		if (existingRole != null) {
			existingRole.setDescription(role.getDescription());
		}
		else {
			final Role newRole = new Role();
			newRole.setName(role.getName());
			newRole.setDescription(role.getDescription());
			em.persist(newRole);
		}
	}

	private void deleteInTransaction(final String name) {
		final EntityManager em = EntityManagerProvider.get();
		final Role role = findRole(name);
		if (role != null) {
			em.remove(role);
		}
	}

	private void addAuthorizationInTransaction(
		@PathParam("role-name") final String roleName,
		final org.jowidgets.useradmin.rest.api.Authorization authorization) {
		final EntityManager em = EntityManagerProvider.get();

		final Role role = findRole(roleName);
		if (role == null) {
			return;
		}

		Authorization authorizationToLink = findAuthorization(authorization.getKey());
		if (authorizationToLink == null) {
			authorizationToLink = new Authorization();
			authorizationToLink.setKey(authorization.getKey());
			authorizationToLink.setDescription(authorization.getDescription());
			em.persist(authorizationToLink);
			em.flush();
		}

		final RoleAuthorizationLink existingLink = findRoleAuthorizationLink(role.getId(), authorizationToLink.getId());
		if (existingLink == null) {
			final RoleAuthorizationLink link = new RoleAuthorizationLink();
			link.setRoleId(role.getId());
			link.setAuthorizationId(authorizationToLink.getId());
			em.persist(link);
		}

	}

	private void deleteAuthorizationInTransaction(
		@PathParam("role-name") final String roleName,
		@PathParam("authorization-key") final String authorizationKey) {
		final EntityManager em = EntityManagerProvider.get();

		final Role role = findRole(roleName);
		final Authorization authorization = findAuthorization(authorizationKey);

		if (role != null && authorization != null) {
			final RoleAuthorizationLink existingLink = findRoleAuthorizationLink(role.getId(), authorization.getId());
			if (existingLink != null) {
				em.remove(existingLink);
			}
		}
	}

	private RoleAuthorizationLink findRoleAuthorizationLink(final Long roleId, final Long authorizationId) {
		final EntityManager em = EntityManagerProvider.get();
		return RoleAuthorizationLinkDAO.findRoleAuthorizationLink(em, roleId, authorizationId);
	}

	private Role findRole(final String name) {
		final EntityManager em = EntityManagerProvider.get();
		return RoleDAO.findRoleByName(em, name);
	}

	private Authorization findAuthorization(final String key) {
		final EntityManager em = EntityManagerProvider.get();
		return AuthorizationDAO.findAuthorizationByKey(em, key);
	}

}
