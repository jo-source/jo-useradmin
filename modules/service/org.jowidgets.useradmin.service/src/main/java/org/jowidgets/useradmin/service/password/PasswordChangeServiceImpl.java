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

package org.jowidgets.useradmin.service.password;

import javax.persistence.EntityManager;

import org.jowidgets.cap.common.api.exception.PasswordChangeServiceException;
import org.jowidgets.cap.common.api.exception.PasswordChangeServiceException.PasswordChangeExceptionDetail;
import org.jowidgets.cap.common.api.exception.ServiceException;
import org.jowidgets.cap.common.api.execution.IExecutionCallback;
import org.jowidgets.cap.common.api.execution.IResultCallback;
import org.jowidgets.cap.common.api.service.IPasswordChangeService;
import org.jowidgets.cap.service.api.CapServiceToolkit;
import org.jowidgets.cap.service.api.transaction.ITransactionTemplate;
import org.jowidgets.cap.service.jpa.api.EntityManagerFactoryProvider;
import org.jowidgets.cap.service.jpa.api.JpaTransactionTemplate;
import org.jowidgets.cap.service.jpa.tools.entity.EntityManagerProvider;
import org.jowidgets.security.tools.SecurityContext;
import org.jowidgets.useradmin.common.validation.PasswordPropertyValidatorProvider;
import org.jowidgets.useradmin.service.persistence.UseradminPersistenceUnitNames;
import org.jowidgets.useradmin.service.persistence.bean.Person;
import org.jowidgets.useradmin.service.persistence.dao.PersonDAO;
import org.jowidgets.util.Assert;
import org.jowidgets.util.EmptyCheck;
import org.jowidgets.validation.IValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PasswordChangeServiceImpl implements IPasswordChangeService {

	private final Logger logger = LoggerFactory.getLogger(PasswordChangeServiceImpl.class);

	private final ITransactionTemplate txTemplate;

	public PasswordChangeServiceImpl() {
		this(UseradminPersistenceUnitNames.USER_ADMIN);
	}

	public PasswordChangeServiceImpl(final String persistenceUnitName) {
		Assert.paramNotEmpty(persistenceUnitName, "persistenceUnitName");
		this.txTemplate = JpaTransactionTemplate.create(EntityManagerFactoryProvider.get(persistenceUnitName));
	}

	@Override
	public void changePassword(
		final IResultCallback<Void> result,
		final String oldPassword,
		final String newPassword,
		final IExecutionCallback executionCallback) {
		try {
			changePasswordSync(oldPassword, newPassword, executionCallback);
			result.finished(null);
		}
		catch (final Exception exception) {
			logger.error("Error on password change", exception);
			if (exception instanceof PasswordChangeServiceException) {
				result.exception(exception);
			}
			else {
				result.exception(new ServiceException(exception));
			}
		}
	}

	private void changePasswordSync(final String oldPassword, final String newPassword, final IExecutionCallback executionCallback) throws Exception {
		final String username = SecurityContext.getUsername();
		if (!EmptyCheck.isEmpty(username)) {
			validateNewPassword(newPassword);
			changePasswordForUsername(username, oldPassword, newPassword, executionCallback);
		}
		else {
			throw new PasswordChangeServiceException(PasswordChangeExceptionDetail.MISSING_SECURITY_CONTEXT);
		}
	}

	private void validateNewPassword(final String newPassword) {
		final IValidationResult validationResult = PasswordPropertyValidatorProvider.get().validate(newPassword);
		if (!validationResult.isValid()) {
			throw new PasswordChangeServiceException(PasswordChangeExceptionDetail.NEW_PASSWORD_INVALID);
		}
	}

	private void changePasswordForUsername(
		final String username,
		final String oldPassword,
		final String newPassword,
		final IExecutionCallback executionCallback) throws Exception {

		txTemplate.doInTransaction(new Runnable() {
			@Override
			public void run() {
				final EntityManager em = EntityManagerProvider.get();
				final Person person = PersonDAO.findPersonByLogin(em, username, true);
				if (person != null) {
					if (person.isAuthenticated(oldPassword)) {
						person.setPassword(newPassword);
						CapServiceToolkit.checkCanceled(executionCallback);
						em.persist(person);
					}
					else {
						throw new PasswordChangeServiceException(PasswordChangeExceptionDetail.OLD_PASSWORD_INVALID);
					}
				}
				else {
					throw new PasswordChangeServiceException(PasswordChangeExceptionDetail.USER_NOT_FOUND);
				}
			}
		});

	}
}
