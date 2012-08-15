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

import org.jowidgets.api.toolkit.Toolkit;
import org.jowidgets.api.widgets.IInputControl;
import org.jowidgets.cap.ui.api.attribute.IAttributeBluePrint;
import org.jowidgets.cap.ui.api.attribute.IAttributeCollectionModifierBuilder;
import org.jowidgets.cap.ui.api.attribute.IControlPanelProviderBluePrint;
import org.jowidgets.cap.ui.tools.plugin.AbstractAttributesPlugin;
import org.jowidgets.common.widgets.factory.ICustomWidgetCreator;
import org.jowidgets.common.widgets.factory.ICustomWidgetFactory;
import org.jowidgets.tools.widgets.blueprint.BPF;
import org.jowidgets.useradmin.common.bean.IPerson;

public class PersonAttributesPlugin extends AbstractAttributesPlugin {

	@Override
	protected void modifyAttributes(final IAttributeCollectionModifierBuilder modifier) {
		final IAttributeBluePrint<String> passwordAttributeBp = modifier.addModifier(IPerson.PASSWORD_PROPERTY);
		addPasswordControlPanel(passwordAttributeBp);
		final IAttributeBluePrint<String> repeatPasswordAttributeBp = modifier.addModifier(IPerson.PASSWORD_REPEAT_PROPERTY);
		addPasswordControlPanel(repeatPasswordAttributeBp);
	}

	private void addPasswordControlPanel(final IAttributeBluePrint<String> attributeBp) {
		final IControlPanelProviderBluePrint<String> controlPanel = attributeBp.setControlPanel();
		controlPanel.setObjectLabelConverter(Toolkit.getConverterProvider().passwordPresentationConverter());
		controlPanel.setControlCreator(new ICustomWidgetCreator<IInputControl<String>>() {
			@Override
			public IInputControl<String> create(final ICustomWidgetFactory widgetFactory) {
				return widgetFactory.create(BPF.inputFieldString().setPasswordPresentation(true));
			}
		});
	}
}