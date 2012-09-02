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

package org.jowidgets.useradmin.ui.password;

import org.jowidgets.api.command.IExecutionContext;
import org.jowidgets.api.controller.IDisposeListener;
import org.jowidgets.api.password.IPasswordChangeExecutor;
import org.jowidgets.api.password.IPasswordChangeResult;
import org.jowidgets.api.toolkit.Toolkit;
import org.jowidgets.api.widgets.IPasswordChangeDialog;
import org.jowidgets.api.widgets.blueprint.IPasswordChangeDialogBluePrint;
import org.jowidgets.cap.common.api.bean.IBeanDtoDescriptor;
import org.jowidgets.cap.common.api.bean.IProperty;
import org.jowidgets.cap.common.api.execution.IResultCallback;
import org.jowidgets.cap.common.api.service.IEntityService;
import org.jowidgets.cap.ui.api.CapUiToolkit;
import org.jowidgets.cap.ui.api.execution.IExecutionTask;
import org.jowidgets.cap.ui.tools.execution.AbstractUiResultCallback;
import org.jowidgets.common.types.Rectangle;
import org.jowidgets.service.api.ServiceProvider;
import org.jowidgets.tools.widgets.blueprint.BPF;
import org.jowidgets.useradmin.common.bean.IPerson;
import org.jowidgets.useradmin.common.entity.EntityIds;
import org.jowidgets.useradmin.common.exception.PasswordChangeServiceException;
import org.jowidgets.useradmin.common.service.IPasswordChangeService;
import org.jowidgets.util.event.ICancelListener;
import org.jowidgets.util.event.ICancelObservable;
import org.jowidgets.validation.IValidator;

final class PasswordChangeDialog {

	private final IPasswordChangeExecutor executor;
	private final IValidator<String> passwordValidator;

	private Rectangle bounds;

	PasswordChangeDialog() {
		final IPasswordChangeService passwordChangeService = ServiceProvider.getService(IPasswordChangeService.ID);
		if (passwordChangeService == null) {
			throw new IllegalStateException("The password change service must not be null");
		}
		this.executor = new PasswordChangeExecutor(passwordChangeService);
		this.passwordValidator = getPasswordValidator();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static IValidator<String> getPasswordValidator() {
		final IEntityService entityService = ServiceProvider.getService(IEntityService.ID);
		if (entityService != null) {
			final IBeanDtoDescriptor personDescriptor = entityService.getDescriptor(EntityIds.PERSON);
			if (personDescriptor != null) {
				for (final IProperty property : personDescriptor.getProperties()) {
					if (IPerson.PASSWORD_PROPERTY.equals(property.getName())) {
						return (IValidator) property.getValidator();
					}
				}
			}
		}
		return null;
	}

	void show(final IExecutionContext executionContext) {
		final IPasswordChangeDialogBluePrint dialogBp = BPF.passwordChangeDialog(executor);
		dialogBp.setExecutionContext(executionContext).setAutoDispose(true);
		dialogBp.setPasswordValidator(passwordValidator);
		if (bounds != null) {
			dialogBp.setPosition(bounds.getPosition()).setSize(bounds.getSize());
		}

		final IPasswordChangeDialog dialog = Toolkit.getActiveWindow().createChildWindow(dialogBp);
		dialog.addDisposeListener(new IDisposeListener() {
			@Override
			public void onDispose() {
				bounds = dialog.getBounds();
			}
		});

		dialog.setVisible(true);
	}

	private static final class PasswordChangeExecutor implements IPasswordChangeExecutor {

		private final IPasswordChangeService passwordChangeService;

		private PasswordChangeExecutor(final IPasswordChangeService passwordChangeService) {
			this.passwordChangeService = passwordChangeService;
		}

		@Override
		public void changePassword(
			final IPasswordChangeResult passwordChangeResult,
			final String oldPassword,
			final String newPassword,
			final ICancelObservable cancelObsersable) {

			final IExecutionTask executionTask = CapUiToolkit.executionTaskFactory().create();

			final ICancelListener cancelListener = new ICancelListener() {
				@Override
				public void canceled() {
					executionTask.cancel();
				}
			};

			final IResultCallback<Void> resultCallback = new AbstractUiResultCallback<Void>() {

				@Override
				protected void finishedUi(final Void result) {
					cancelObsersable.removeCancelListener(cancelListener);
					passwordChangeResult.success();
				}

				@Override
				protected void exceptionUi(final Throwable exception) {
					cancelObsersable.removeCancelListener(cancelListener);
					passwordChangeResult.error(createErrorString(exception));
				}

			};

			cancelObsersable.addCancelListener(cancelListener);

			passwordChangeService.changePassword(resultCallback, oldPassword, newPassword, executionTask);

		}

		private String createErrorString(final Throwable exception) {
			if (exception instanceof PasswordChangeServiceException) {
				return ((PasswordChangeServiceException) exception).getDetail().toString();
			}
			else {
				return "Unbekannter Fehler";
			}
		}
	}
}
