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

package org.jowidgets.useradmin.rest.service.security;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jowidgets.cap.common.api.exception.PasswordChangeServiceException;
import org.jowidgets.cap.common.api.exception.PasswordChangeServiceException.PasswordChangeExceptionDetail;
import org.jowidgets.cap.common.api.service.IPasswordChangeService;
import org.jowidgets.cap.common.tools.execution.DummyExecutionCallback;
import org.jowidgets.cap.common.tools.execution.SyncResultCallback;
import org.jowidgets.service.api.ServiceProvider;
import org.jowidgets.useradmin.rest.api.PasswordChangeRequest;
import org.jowidgets.useradmin.rest.api.PasswordChangeResult;
import org.jowidgets.useradmin.rest.exception.HttpStatusException;

@Path("service/security")
public final class PasswordChangeService {

	@POST
	@Path("change-password")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public PasswordChangeResult changePassword(final PasswordChangeRequest passwordChangeRequest) {
		if (passwordChangeRequest == null) {
			throw new HttpStatusException(Response.Status.BAD_REQUEST.getStatusCode(), "PasswordChangeRequest must not be null");
		}

		final IPasswordChangeService passwordChangeService = ServiceProvider.getService(IPasswordChangeService.ID);
		if (passwordChangeService == null) {
			throw new HttpStatusException(
				Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				"Underlying password change service not found");
		}

		final SyncResultCallback<Void> resultCallback = new SyncResultCallback<Void>();
		passwordChangeService.changePassword(
				resultCallback,
				passwordChangeRequest.getOldPassword(),
				passwordChangeRequest.getNewPassword(),
				new DummyExecutionCallback());

		try {
			resultCallback.getResultSynchronious();
			return PasswordChangeResult.OK;
		}
		catch (final PasswordChangeServiceException exception) {
			final PasswordChangeExceptionDetail detail = exception.getDetail();
			if (PasswordChangeExceptionDetail.MISSING_SECURITY_CONTEXT.equals(detail)) {
				return PasswordChangeResult.USER_NOT_FOUND;
			}
			else if (PasswordChangeExceptionDetail.USER_NOT_FOUND.equals(detail)) {
				return PasswordChangeResult.USER_NOT_FOUND;
			}
			else if (PasswordChangeExceptionDetail.OLD_PASSWORD_INVALID.equals(detail)) {
				return PasswordChangeResult.OLD_PASSWORD_INVALID;
			}
			else if (PasswordChangeExceptionDetail.NEW_PASSWORD_INVALID.equals(detail)) {
				return PasswordChangeResult.NEW_PASSWORD_INVALID;
			}
			else {
				throw new HttpStatusException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Unkown exception detail: '"
					+ detail
					+ "'.");
			}
		}

	}

}
