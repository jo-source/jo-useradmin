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

import org.jowidgets.cap.common.api.execution.IExecutionCallback;
import org.jowidgets.cap.common.api.execution.IResultCallback;
import org.jowidgets.cap.service.jpa.tools.entity.EntityManagerProvider;
import org.jowidgets.security.tools.SecurityContext;
import org.jowidgets.useradmin.common.exception.PasswordChangeServiceException;
import org.jowidgets.useradmin.common.exception.PasswordChangeServiceException.PasswordChangeExceptionDetail;
import org.jowidgets.useradmin.common.service.IPasswordChangeService;
import org.jowidgets.useradmin.service.persistence.bean.Person;
import org.jowidgets.useradmin.service.persistence.dao.PersonDAO;

public final class PasswordChangeServiceImpl implements IPasswordChangeService {

	@Override
	public void changePassword(
		final IResultCallback<Void> result,
		final String oldPassword,
		final String newPassword,
		final IExecutionCallback executionCallback) {
		try {
			changePasswordSync(oldPassword, newPassword);
			result.finished(null);
		}
		catch (final Exception exception) {
			result.exception(exception);
		}
	}

	private void changePasswordSync(final String oldPassword, final String newPassword) {
		final String username = SecurityContext.getUsername();
		if (username != null) {
			final EntityManager entityManager = EntityManagerProvider.get();
			final Person person = PersonDAO.findPersonByLogin(entityManager, username, true);
			if (person != null) {
				if (person.isAuthenticated(oldPassword)) {
					person.setPassword(newPassword);
					entityManager.persist(person);
				}
				else {
					throw new PasswordChangeServiceException(PasswordChangeExceptionDetail.OLD_PASSWORD_INVALID);
				}
			}
			else {
				throw new PasswordChangeServiceException(PasswordChangeExceptionDetail.USER_NOT_FOUND);
			}
		}
		else {
			throw new PasswordChangeServiceException(PasswordChangeExceptionDetail.MISSING_SECURITY_CONTEXT);
		}

	}
}
