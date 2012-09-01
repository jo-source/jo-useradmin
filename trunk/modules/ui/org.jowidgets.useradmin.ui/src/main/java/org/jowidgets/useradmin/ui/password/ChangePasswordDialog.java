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
import org.jowidgets.api.toolkit.Toolkit;
import org.jowidgets.api.widgets.IButton;
import org.jowidgets.api.widgets.IComposite;
import org.jowidgets.api.widgets.IContainer;
import org.jowidgets.api.widgets.IFrame;
import org.jowidgets.api.widgets.IInputField;
import org.jowidgets.api.widgets.IProgressBar;
import org.jowidgets.api.widgets.IValidationResultLabel;
import org.jowidgets.api.widgets.blueprint.IDialogBluePrint;
import org.jowidgets.cap.common.api.execution.IResultCallback;
import org.jowidgets.cap.ui.api.CapUiToolkit;
import org.jowidgets.cap.ui.api.execution.IExecutionTask;
import org.jowidgets.cap.ui.tools.execution.AbstractUiResultCallback;
import org.jowidgets.common.types.Dimension;
import org.jowidgets.common.types.Rectangle;
import org.jowidgets.common.widgets.controller.IActionListener;
import org.jowidgets.common.widgets.layout.MigLayoutDescriptor;
import org.jowidgets.service.api.ServiceProvider;
import org.jowidgets.tools.layout.MigLayoutFactory;
import org.jowidgets.tools.validation.MandatoryValidator;
import org.jowidgets.tools.widgets.blueprint.BPF;
import org.jowidgets.useradmin.common.exception.PasswordChangeServiceException;
import org.jowidgets.useradmin.common.service.IPasswordChangeService;
import org.jowidgets.util.EmptyCheck;
import org.jowidgets.util.EmptyCompatibleEquivalence;
import org.jowidgets.validation.IValidationConditionListener;
import org.jowidgets.validation.IValidationResult;
import org.jowidgets.validation.IValidationResultBuilder;
import org.jowidgets.validation.ValidationResult;

final class ChangePasswordDialog {

	private final IPasswordChangeService passwordChangeService;

	private Rectangle bounds;
	private IFrame dialog;
	private IValidationResultLabel validationResultLabel;
	private IInputField<String> oldPassword;
	private IInputField<String> newPassword;
	private IInputField<String> newPasswordRepeat;
	private IButton cancelButton;
	private IButton okButton;
	private IProgressBar progressBar;

	ChangePasswordDialog() {
		this.passwordChangeService = ServiceProvider.getService(IPasswordChangeService.ID);
		if (passwordChangeService == null) {
			throw new IllegalStateException("The password change service must not be null");
		}
	}

	void show(final IExecutionContext executionContext) {

		final IDialogBluePrint dialogBp = BPF.dialog();
		dialogBp.setExecutionContext(executionContext);

		this.dialog = Toolkit.getActiveWindow().createChildWindow(dialogBp);
		if (bounds != null) {
			dialog.setBounds(bounds);
		}
		else {
			dialog.setMinPackSize(new Dimension(350, 180));
		}
		dialog.addDisposeListener(new IDisposeListener() {
			@Override
			public void onDispose() {
				bounds = dialog.getBounds();
			}
		});
		createControlsAndShow(dialog);
	}

	private void createControlsAndShow(final IFrame dialog) {
		dialog.setLayout(new MigLayoutDescriptor("0[grow, 0::]0", "0[grow, 0::][]0"));

		final IComposite content = dialog.add(BPF.scrollComposite(), MigLayoutFactory.GROWING_CELL_CONSTRAINTS + ",wrap");

		this.progressBar = dialog.add(BPF.progressBar().setIndeterminate(true), "growx, w 0::, h 10!");
		progressBar.setVisible(false);

		content.setLayout(new MigLayoutDescriptor("[][grow, 160::]", "[20!]25[]10[][]15[grow]"));

		this.validationResultLabel = content.add(BPF.validationResultLabel(), "span 2, growx, w 0::, wrap");

		this.oldPassword = addInputField(content, "Altes Passwort");
		this.newPassword = addInputField(content, "Neues Passwort");
		this.newPasswordRepeat = addInputField(content, "Passwort Wiederholung");

		final IComposite buttonBar = content.add(BPF.composite(), "alignx r, aligny b, span 2");
		buttonBar.setLayout(new MigLayoutDescriptor("0[][]0", "0[]0"));
		this.okButton = buttonBar.add(BPF.buttonOk(), "sg bg");
		this.cancelButton = buttonBar.add(BPF.buttonCancel(), "sg bg");

		cancelButton.addActionListener(new IActionListener() {
			@Override
			public void actionPerformed() {
				dialog.dispose();
			}
		});

		okButton.addActionListener(new IActionListener() {
			@Override
			public void actionPerformed() {
				changePassword(dialog);
			}
		});

		validate();

		dialog.setVisible(true);
		dialog.dispose();
	}

	private void changePassword(final IFrame dialog) {
		validationResultLabel.setResult(ValidationResult.infoError("Try to change password ..."));
		setEnabled(false);
		progressBar.setVisible(true);

		final IExecutionTask executionTask = CapUiToolkit.executionTaskFactory().create();

		final IActionListener cancelListener = new IActionListener() {
			@Override
			public void actionPerformed() {
				executionTask.cancel();
			}
		};

		final IResultCallback<Void> resultCallback = new AbstractUiResultCallback<Void>() {

			@Override
			protected void finishedUi(final Void result) {
				finishedCommon();
				dialog.dispose();
			}

			@Override
			protected void exceptionUi(final Throwable exception) {
				finishedCommon();
				onError(exception);
			}

			private void finishedCommon() {
				cancelButton.removeActionListener(cancelListener);
				progressBar.setVisible(false);
				setEnabled(true);
			}

		};

		cancelButton.addActionListener(cancelListener);

		passwordChangeService.changePassword(resultCallback, oldPassword.getValue(), newPassword.getValue(), executionTask);
	}

	private void setEnabled(final boolean enabled) {
		oldPassword.setEnabled(enabled);
		newPassword.setEnabled(enabled);
		newPasswordRepeat.setEnabled(enabled);
		okButton.setEnabled(enabled);
	}

	private IInputField<String> addInputField(final IContainer content, final String label) {
		content.add(BPF.textLabel(label).alignRight(), "alignx r");
		final IInputField<String> result = content.add(BPF.inputFieldString().setPasswordPresentation(true), "growx, w 0::, wrap");
		result.addValidator(new MandatoryValidator<String>(ValidationResult.infoError("Bitte füllen Sie die Pflichtfelder aus!")));
		result.addValidationConditionListener(new IValidationConditionListener() {
			@Override
			public void validationConditionsChanged() {
				validate();
			}
		});
		return result;
	}

	private void validate() {
		final IValidationResultBuilder builder = ValidationResult.builder();
		builder.addResult(oldPassword.validate());
		builder.addResult(newPassword.validate());
		builder.addResult(newPasswordRepeat.validate());
		builder.addResult(validatePasswordEquality());
		final IValidationResult validationResult = builder.build();

		validationResultLabel.setResult(validationResult);

		if (validationResult.isValid()) {
			okButton.setEnabled(true);
			okButton.setToolTipText(null);
		}
		else {
			okButton.setEnabled(false);
			okButton.setToolTipText(validationResult.getWorstFirst().getText());
		}
	}

	private IValidationResult validatePasswordEquality() {
		final String oldPasswordValue = oldPassword.getValue();
		final String newPasswordValue = newPassword.getValue();
		final String newPasswordRepeatValue = newPasswordRepeat.getValue();
		if (!EmptyCheck.isEmpty(newPasswordValue) && newPasswordValue.equals(oldPasswordValue)) {
			return ValidationResult.error("Altes und neues Passwort sind gleich!");
		}
		if (!EmptyCompatibleEquivalence.equals(newPasswordRepeatValue, newPasswordValue)) {
			if (newPasswordRepeatValue != null
				&& newPasswordValue != null
				&& newPasswordValue.length() > newPasswordRepeatValue.length()) {
				return ValidationResult.infoError("Bitte Wiederholen sie das Passwort!");
			}
			else {
				return ValidationResult.error("Passwort stimmt mit Wiederholung nicht überein!");
			}
		}
		else {
			return ValidationResult.ok();
		}
	}

	private void onError(final Throwable exception) {
		validationResultLabel.setResult(ValidationResult.error(createErrorString(exception)));
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
