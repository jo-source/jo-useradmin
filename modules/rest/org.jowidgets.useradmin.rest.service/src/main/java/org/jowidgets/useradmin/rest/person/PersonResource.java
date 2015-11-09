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

package org.jowidgets.useradmin.rest.person;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jowidgets.cap.common.api.bean.IBeanDto;
import org.jowidgets.cap.common.api.filter.ArithmeticFilter;
import org.jowidgets.cap.common.api.filter.ArithmeticOperator;
import org.jowidgets.cap.common.api.filter.IArithmeticFilterBuilder;
import org.jowidgets.cap.common.api.filter.IFilter;
import org.jowidgets.cap.common.api.service.IBeanServicesProvider;
import org.jowidgets.cap.common.api.service.IEntityService;
import org.jowidgets.cap.common.api.service.IReaderService;
import org.jowidgets.cap.common.tools.execution.DummyExecutionCallback;
import org.jowidgets.cap.common.tools.execution.SyncResultCallback;
import org.jowidgets.service.api.ServiceProvider;
import org.jowidgets.useradmin.common.bean.IPerson;
import org.jowidgets.useradmin.common.entity.EntityIds;
import org.jowidgets.useradmin.rest.api.Person;
import org.jowidgets.useradmin.rest.api.Principal;
import org.jowidgets.useradmin.rest.service.security.AuthorizationService;
import org.jowidgets.util.EmptyCheck;

@Path("persons")
public final class PersonResource {

	@GET
	@Path("{loginName}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Person getPersonByLoginName(@PathParam("loginName") final String loginName) {
		if (EmptyCheck.isEmpty(loginName)) {
			return null;
		}
		final IReaderService<Void> readerService = getReaderService();
		if (readerService == null) {
			return null;
		}
		return findPerson(readerService, loginName);
	}

	private Person findPerson(final IReaderService<Void> readerService, final String loginName) {
		final SyncResultCallback<List<IBeanDto>> resultCalback = new SyncResultCallback<List<IBeanDto>>();
		readerService.read(resultCalback, null, createFilter(loginName), null, 0, 2, null, new DummyExecutionCallback());
		final List<IBeanDto> persons = resultCalback.getResultSynchronious();
		if (!EmptyCheck.isEmpty(persons) && persons.size() == 1) {
			return createPerson(persons.get(0));
		}
		return null;
	}

	private Person createPerson(final IBeanDto personDto) {
		final Person result = new Person();
		final String loginName = (String) personDto.getValue(IPerson.LOGIN_NAME_PROPERTY);
		result.setLoginName(loginName);
		result.setName((String) personDto.getValue(IPerson.NAME_PROPERTY));
		result.setPasswordHash((String) personDto.getValue(IPerson.PASSWORD_HASH_PROPERTY));
		final Boolean active = (Boolean) personDto.getValue(IPerson.ACTIVE_PROPERTY);
		result.setActive(active != null ? active.booleanValue() : false);
		result.setGrantedAuthorities(getAuthoritiesOfPerson(loginName));
		return result;
	}

	private List<String> getAuthoritiesOfPerson(final String loginName) {
		if (EmptyCheck.isEmpty(loginName)) {
			return Collections.emptyList();
		}
		final AuthorizationService authorizationService = new AuthorizationService();
		final Principal principal = authorizationService.authorize(new Principal(loginName));
		if (principal == null) {
			return Collections.emptyList();
		}

		final List<String> grantedAuthorities = principal.getGrantedAuthorities();
		if (EmptyCheck.isEmpty(grantedAuthorities)) {
			return Collections.emptyList();
		}

		return grantedAuthorities;
	}

	private IFilter createFilter(final String loginName) {
		final IArithmeticFilterBuilder builder = ArithmeticFilter.builder();
		builder.setPropertyName(IPerson.LOGIN_NAME_PROPERTY);
		builder.setOperator(ArithmeticOperator.EQUAL);
		builder.setParameter(loginName);
		return builder.build();
	}

	private IReaderService<Void> getReaderService() {
		final IEntityService entityService = ServiceProvider.getService(IEntityService.ID);
		if (entityService != null) {
			final IBeanServicesProvider beanServices = entityService.getBeanServices(EntityIds.PERSON);
			if (beanServices != null) {
				return beanServices.readerService();
			}
		}
		return null;
	}

}
