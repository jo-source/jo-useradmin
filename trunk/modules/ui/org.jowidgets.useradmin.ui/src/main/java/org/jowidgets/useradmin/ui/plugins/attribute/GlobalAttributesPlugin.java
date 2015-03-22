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

package org.jowidgets.useradmin.ui.plugins.attribute;

import org.jowidgets.api.widgets.IComboBox;
import org.jowidgets.api.widgets.IComposite;
import org.jowidgets.api.widgets.IInputControl;
import org.jowidgets.api.widgets.IInputField;
import org.jowidgets.cap.ui.api.attribute.IAttributeCollectionModifierBuilder;
import org.jowidgets.cap.ui.api.bean.IBeanProxy;
import org.jowidgets.cap.ui.tools.plugin.AbstractAttributesPlugin;
import org.jowidgets.common.color.IColorConstant;
import org.jowidgets.common.widgets.controller.IInputListener;
import org.jowidgets.common.widgets.factory.ICustomWidgetCreator;
import org.jowidgets.common.widgets.factory.ICustomWidgetFactory;
import org.jowidgets.common.widgets.layout.MigLayoutDescriptor;
import org.jowidgets.tools.widgets.blueprint.BPF;
import org.jowidgets.tools.widgets.wrapper.AbstractInputControl;
import org.jowidgets.useradmin.common.bean.IPerson;
import org.jowidgets.validation.IValidationConditionListener;
import org.jowidgets.validation.IValidationResult;

public class GlobalAttributesPlugin extends AbstractAttributesPlugin {

	@Override
	protected void modifyAttributes(final IAttributeCollectionModifierBuilder modifier) {
		modifier.addModifier(IBeanProxy.META_PROPERTY_PROGRESS).setVisible(false);

		modifier.addModifier(IPerson.NAME_PROPERTY).setControlPanel().setControlCreator(
				new ICustomWidgetCreator<IInputControl<String>>() {

					@Override
					public IInputControl<String> create(final ICustomWidgetFactory widgetFactory) {
						final IComposite container = widgetFactory.create(BPF.composite());
						container.setLayout(new MigLayoutDescriptor("wrap", "0[grow]0", "0[]0[]0"));
						final IComboBox<String> comboBox = container.add(BPF.comboBoxSelection("HALOO", "KUCKCK"), "growx");

						final IInputField<String> inputField = container.add(BPF.inputFieldString(), "growx");

						final AbstractInputControl<String> result = new AbstractInputControl<String>(container) {

							{
								inputField.addInputListener(new IInputListener() {
									@Override
									public void inputChanged() {
										fireInputChanged();
									}
								});

								inputField.addValidationConditionListener(new IValidationConditionListener() {
									@Override
									public void validationConditionsChanged() {
										setValidationCacheDirty();
									}
								});
							}

							@Override
							public boolean hasModifications() {
								return inputField.hasModifications();
							}

							@Override
							public void resetModificationState() {
								inputField.resetModificationState();
							}

							@Override
							public boolean isEditable() {
								return inputField.isEditable();
							}

							@Override
							public void setValue(final String value) {
								inputField.setValue(value);

							}

							@Override
							public String getValue() {
								// TODO Auto-generated method stub
								return inputField.getValue();
							}

							@Override
							public void setEditable(final boolean editable) {
								inputField.setEditable(editable);

							}

							@Override
							public void setBackgroundColor(final IColorConstant colorValue) {
								inputField.setBackgroundColor(colorValue);
								super.setBackgroundColor(colorValue);
							}

							@Override
							protected IValidationResult createValidationResult() {

								return inputField.validate();
							}

						};

						comboBox.addInputListener(new IInputListener() {

							@Override
							public void inputChanged() {
								result.setValue(comboBox.getValue());
							}
						});

						return result;
					}
				});
	}
}
