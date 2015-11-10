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

package org.jowidgets.useradmin.rest.client.service;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.jowidgets.cap.common.api.exception.PasswordChangeServiceException;
import org.jowidgets.cap.common.api.exception.PasswordChangeServiceException.PasswordChangeExceptionDetail;
import org.jowidgets.cap.common.api.exception.ServiceException;
import org.jowidgets.cap.common.api.execution.IExecutionCallback;
import org.jowidgets.cap.common.api.execution.IResultCallback;
import org.jowidgets.cap.common.api.service.IPasswordChangeService;
import org.jowidgets.security.tools.SecurityContext;
import org.jowidgets.useradmin.rest.api.PasswordChangeRequest;
import org.jowidgets.useradmin.rest.api.PasswordChangeResult;
import org.jowidgets.useradmin.rest.client.util.BasicAuthenticationHelper;
import org.jowidgets.useradmin.rest.client.util.UserAdminResourceFactory;
import org.jowidgets.util.EmptyCheck;

public final class PasswordChangeService implements IPasswordChangeService {

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
			changePassword(username, oldPassword, newPassword);
		}
		else {
			throw new PasswordChangeServiceException(PasswordChangeExceptionDetail.MISSING_SECURITY_CONTEXT);
		}
	}

	private void changePassword(final String username, final String oldPassword, final String newPassword) {
		final UserAdminResourceFactory resourceFactory = new UserAdminResourceFactory();
		final BasicAuthenticationHelper authenticationHelper = new BasicAuthenticationHelper();

		final WebTarget passwordChangeService = resourceFactory.getPasswordChangeService();
		final Builder requestBuilder = passwordChangeService.request();

		//password will be changed for authorized user, so use login and old password for authentication
		authenticationHelper.setBasicAuthentication(requestBuilder, username, oldPassword);

		final PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(oldPassword, newPassword);
		final Response response = requestBuilder.post(Entity.entity(passwordChangeRequest, MediaType.APPLICATION_JSON));

		if (Response.Status.UNAUTHORIZED.getStatusCode() == response.getStatus()) {
			//TODO assume that old password is incorrect, because normally user received from security context is known
			throw new PasswordChangeServiceException(PasswordChangeExceptionDetail.OLD_PASSWORD_INVALID);
		}
		else if (!Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
			throw new ServiceException(response.getStatusInfo().getReasonPhrase());
		}

		final PasswordChangeResult passwordChangeResult = response.readEntity(PasswordChangeResult.class);

		if (PasswordChangeResult.USER_NOT_FOUND.equals(passwordChangeResult)) {
			throw new PasswordChangeServiceException(PasswordChangeExceptionDetail.USER_NOT_FOUND);
		}
		else if (PasswordChangeResult.OLD_PASSWORD_INVALID.equals(passwordChangeResult)) {
			throw new PasswordChangeServiceException(PasswordChangeExceptionDetail.OLD_PASSWORD_INVALID);
		}
		else if (PasswordChangeResult.NEW_PASSWORD_INVALID.equals(passwordChangeResult)) {
			throw new PasswordChangeServiceException(PasswordChangeExceptionDetail.NEW_PASSWORD_INVALID);
		}
	}
}
