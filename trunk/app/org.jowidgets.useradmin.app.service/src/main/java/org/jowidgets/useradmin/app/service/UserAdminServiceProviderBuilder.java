/*
 * Copyright (c) 2011, grossmann
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

package org.jowidgets.useradmin.app.service;

import org.jowidgets.cap.common.api.service.IEntityService;
import org.jowidgets.cap.common.api.service.ILookUpService;
import org.jowidgets.cap.service.hibernate.api.HibernateServiceToolkit;
import org.jowidgets.cap.service.hibernate.api.ICancelServicesDecoratorProviderBuilder;
import org.jowidgets.cap.service.hibernate.oracle.api.HibernateOracleServiceToolkit;
import org.jowidgets.cap.service.jpa.api.IJpaServicesDecoratorProviderBuilder;
import org.jowidgets.cap.service.jpa.api.JpaServiceToolkit;
import org.jowidgets.cap.service.tools.CapServiceProviderBuilder;
import org.jowidgets.service.api.IServicesDecoratorProvider;
import org.jowidgets.useradmin.app.service.entity.UserAdminEntityServiceBuilder;
import org.jowidgets.useradmin.app.service.lookup.RolesLookUpService;
import org.jowidgets.useradmin.app.service.security.AuthorizationProviderServiceImpl;
import org.jowidgets.useradmin.common.lookup.LookUpIds;
import org.jowidgets.useradmin.common.security.AuthorizationProviderServiceId;
import org.jowidgets.useradmin.service.persistence.PersistenceUnitNames;

public final class UserAdminServiceProviderBuilder extends CapServiceProviderBuilder {

	public UserAdminServiceProviderBuilder() {
		addService(AuthorizationProviderServiceId.ID, new AuthorizationProviderServiceImpl());
		addService(IEntityService.ID, new UserAdminEntityServiceBuilder(this).build());

		addLookUpService(LookUpIds.ROLE, new RolesLookUpService());

		addServiceDecorator(createJpaServiceDecoratorProvider());
		addServiceDecorator(createCancelServiceDecoratorProvider());
	}

	private IServicesDecoratorProvider createJpaServiceDecoratorProvider() {
		final IJpaServicesDecoratorProviderBuilder builder = JpaServiceToolkit.serviceDecoratorProviderBuilder(PersistenceUnitNames.USER_ADMIN);
		builder.addEntityManagerServices(ILookUpService.class);
		builder.addExceptionDecorator(HibernateServiceToolkit.exceptionDecorator());
		builder.addExceptionDecorator(HibernateOracleServiceToolkit.exceptionDecorator());
		return builder.build();
	}

	private IServicesDecoratorProvider createCancelServiceDecoratorProvider() {
		final ICancelServicesDecoratorProviderBuilder builder = HibernateServiceToolkit.serviceDecoratorProviderBuilder(PersistenceUnitNames.USER_ADMIN);
		builder.addServices(ILookUpService.class);
		return builder.build();
	}

}
