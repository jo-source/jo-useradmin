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

import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jowidgets.cap.service.jpa.api.EntityManagerContextTemplate;
import org.jowidgets.cap.service.jpa.api.EntityManagerFactoryProvider;
import org.jowidgets.cap.service.jpa.api.IEntityManagerContextTemplate;
import org.jowidgets.cap.service.jpa.tools.entity.EntityManagerProvider;
import org.jowidgets.useradmin.common.security.AuthKeys;
import org.jowidgets.useradmin.rest.authorization.AuthorizationChecker;
import org.jowidgets.useradmin.service.persistence.UseradminPersistenceUnitNames;
import org.jowidgets.useradmin.service.persistence.bean.Person;
import org.jowidgets.useradmin.service.persistence.dao.PersonDAO;
import org.jowidgets.util.EmptyCheck;

@Path("persons")
public final class PersonResource {

	private final IEntityManagerContextTemplate emContextTemplate;

	public PersonResource() {
		this.emContextTemplate = EntityManagerContextTemplate.create(EntityManagerFactoryProvider.get(UseradminPersistenceUnitNames.USER_ADMIN));
	}

	@GET
	@Path("{loginName}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public org.jowidgets.useradmin.rest.api.Person getPersonByLoginName(@PathParam("loginName") final String loginName) {
		AuthorizationChecker.check(AuthKeys.READ_PERSON);
		if (EmptyCheck.isEmpty(loginName)) {
			return null;
		}
		return findPerson(loginName);
	}

	private org.jowidgets.useradmin.rest.api.Person findPerson(final String loginName) {
		return emContextTemplate.callInEntityManagerContext(new Callable<org.jowidgets.useradmin.rest.api.Person>() {
			@Override
			public org.jowidgets.useradmin.rest.api.Person call() throws Exception {
				final EntityManager em = EntityManagerProvider.get();
				final Person person = PersonDAO.findPersonByLogin(em, loginName, true);
				if (person != null) {
					return createPerson(person);
				}
				return null;
			}
		});
	}

	private org.jowidgets.useradmin.rest.api.Person createPerson(final Person person) {
		final org.jowidgets.useradmin.rest.api.Person result = new org.jowidgets.useradmin.rest.api.Person();
		final String loginName = person.getLoginName();
		result.setLoginName(loginName);
		result.setName(person.getName());
		result.setPasswordHash(person.getPasswordHash());
		final Boolean active = person.getActive();
		result.setActive(active != null ? active.booleanValue() : false);
		result.setGrantedAuthorities(new ArrayList<String>(person.getAuthorizationNames()));
		return result;
	}

}
