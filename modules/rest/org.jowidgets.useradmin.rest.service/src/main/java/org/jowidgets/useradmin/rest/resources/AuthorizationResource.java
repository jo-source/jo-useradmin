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
import javax.ws.rs.core.Response;

import org.jowidgets.cap.service.api.transaction.ITransactionTemplate;
import org.jowidgets.cap.service.jpa.api.EntityManagerFactoryProvider;
import org.jowidgets.cap.service.jpa.api.JpaTransactionTemplate;
import org.jowidgets.cap.service.jpa.tools.entity.EntityManagerProvider;
import org.jowidgets.useradmin.common.security.AuthKeys;
import org.jowidgets.useradmin.rest.authorization.AuthorizationChecker;
import org.jowidgets.useradmin.rest.exception.HttpStatusException;
import org.jowidgets.useradmin.service.persistence.UseradminPersistenceUnitNames;
import org.jowidgets.useradmin.service.persistence.bean.Authorization;
import org.jowidgets.useradmin.service.persistence.dao.AuthorizationDAO;
import org.jowidgets.util.EmptyCheck;

@Path("authorizations")
public final class AuthorizationResource {

	private final ITransactionTemplate transactionTemplate;

	public AuthorizationResource() {
		this.transactionTemplate = JpaTransactionTemplate.create(EntityManagerFactoryProvider.get(UseradminPersistenceUnitNames.USER_ADMIN));
	}

	@PUT
	public void addOrModify(final org.jowidgets.useradmin.rest.api.Authorization authorization) {
		AuthorizationChecker.check(AuthKeys.CREATE_AUTHORIZATION);
		AuthorizationChecker.check(AuthKeys.UPDATE_AUTHORIZATION);
		if (authorization == null || EmptyCheck.isEmpty(authorization.getKey())) {
			throw new HttpStatusException(Response.Status.BAD_REQUEST.getStatusCode(), "Authorization key must not be empty");
		}
		transactionTemplate.doInTransaction(new Runnable() {
			@Override
			public void run() {
				addOrModifyInTransaction(authorization);
			}
		});
	}

	@DELETE
	@Path("{key}")
	public void delete(@PathParam("key") final String key) {
		AuthorizationChecker.check(AuthKeys.DELETE_AUTHORIZATION);
		if (EmptyCheck.isEmpty(key)) {
			throw new HttpStatusException(Response.Status.BAD_REQUEST.getStatusCode(), "Authorization key must not be empty");
		}
		transactionTemplate.doInTransaction(new Runnable() {
			@Override
			public void run() {
				deleteInTransaction(key);
			}
		});
	}

	private void addOrModifyInTransaction(final org.jowidgets.useradmin.rest.api.Authorization authorization) {
		final EntityManager em = EntityManagerProvider.get();
		final Authorization existingAuthorization = findAuthorization(authorization.getKey());
		if (existingAuthorization != null) {
			existingAuthorization.setDescription(authorization.getDescription());
		}
		else {
			final Authorization newAuthorization = new Authorization();
			newAuthorization.setKey(authorization.getKey());
			newAuthorization.setDescription(authorization.getDescription());
			em.persist(newAuthorization);
		}
	}

	private void deleteInTransaction(final String key) {
		final EntityManager em = EntityManagerProvider.get();
		final Authorization authorization = findAuthorization(key);
		if (authorization != null) {
			em.remove(authorization);
		}
	}

	private Authorization findAuthorization(final String key) {
		final EntityManager em = EntityManagerProvider.get();
		return AuthorizationDAO.findAuthorizationByKey(em, key);
	}

}
