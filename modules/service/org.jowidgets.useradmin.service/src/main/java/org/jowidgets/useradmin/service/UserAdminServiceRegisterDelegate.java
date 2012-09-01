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

package org.jowidgets.useradmin.service;

import java.util.Collection;
import java.util.Collections;

import org.jowidgets.cap.common.api.bean.IBean;
import org.jowidgets.cap.common.api.execution.IExecutableChecker;
import org.jowidgets.cap.common.api.service.IExecutorService;
import org.jowidgets.cap.service.api.bean.IBeanAccess;
import org.jowidgets.cap.service.api.executor.IBeanExecutor;
import org.jowidgets.cap.service.jpa.api.JpaServiceToolkit;
import org.jowidgets.cap.service.tools.CapServiceProviderBuilder;
import org.jowidgets.service.api.IServiceId;
import org.jowidgets.useradmin.common.bean.IPerson;
import org.jowidgets.useradmin.common.checker.PersonActivateExecutableChecker;
import org.jowidgets.useradmin.common.checker.PersonDeactivateExecutableChecker;
import org.jowidgets.useradmin.common.executor.ExecutorServices;
import org.jowidgets.useradmin.common.service.IPasswordChangeService;
import org.jowidgets.useradmin.service.executor.PersonActivateExecutor;
import org.jowidgets.useradmin.service.executor.PersonDeactivateExecutor;
import org.jowidgets.useradmin.service.password.PasswordChangeServiceImpl;
import org.jowidgets.useradmin.service.persistence.bean.Person;

public final class UserAdminServiceRegisterDelegate {

	private UserAdminServiceRegisterDelegate() {}

	public static void addServices(final CapServiceProviderBuilder builder) {
		addPersonExecutorService(
				builder,
				ExecutorServices.ACTIVATE_PERSON,
				new PersonActivateExecutor(),
				new PersonActivateExecutableChecker());

		addPersonExecutorService(
				builder,
				ExecutorServices.DEACTIVATE_PERSON,
				new PersonDeactivateExecutor(),
				new PersonDeactivateExecutableChecker());

		builder.addService(IPasswordChangeService.ID, new PasswordChangeServiceImpl());
	}

	public static Collection<? extends Class<?>> getTransactionalServices() {
		return Collections.singleton(IPasswordChangeService.class);
	}

	public static Collection<? extends Class<?>> getEntityManagerServices() {
		return Collections.singleton(IPasswordChangeService.class);
	}

	private static <BEAN_TYPE extends IBean, PARAM_TYPE> void addPersonExecutorService(
		final CapServiceProviderBuilder builder,
		final IServiceId<? extends IExecutorService<PARAM_TYPE>> id,
		final IBeanExecutor<? extends BEAN_TYPE, PARAM_TYPE> beanExecutor,
		final IExecutableChecker<? extends BEAN_TYPE> executableChecker) {
		final IBeanAccess<Person> beanAccess = JpaServiceToolkit.serviceFactory().beanAccess(Person.class);
		builder.addExecutorService(id, beanExecutor, executableChecker, beanAccess, IPerson.ALL_PROPERTIES);
	}

}
