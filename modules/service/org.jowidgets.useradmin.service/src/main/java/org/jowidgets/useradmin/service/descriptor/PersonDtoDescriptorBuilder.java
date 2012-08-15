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

package org.jowidgets.useradmin.service.descriptor;

import org.jowidgets.cap.common.api.bean.IBeanPropertyBluePrint;
import org.jowidgets.i18n.api.IMessage;
import org.jowidgets.useradmin.common.bean.IPerson;
import org.jowidgets.useradmin.common.i18n.entity.EntityMessages;
import org.jowidgets.util.Assert;

public final class PersonDtoDescriptorBuilder extends AbstractDtoDescriptorBuilder {

	public PersonDtoDescriptorBuilder() {
		this(getMessage("user"), getMessage("users"));
	}

	public PersonDtoDescriptorBuilder(final IMessage labelSingular, final IMessage labelPlural) {
		super(IPerson.class);

		setLabelSingular(labelSingular);
		setLabelPlural(labelPlural);

		setRenderingPattern("$" + IPerson.NAME_PROPERTY + "$" + " ($" + IPerson.LOGIN_NAME_PROPERTY + "$)");

		addIdProperty();

		IBeanPropertyBluePrint propertyBp;

		propertyBp = addProperty(IPerson.LOGIN_NAME_PROPERTY);
		propertyBp.setLabel(getMessage("login.label"));
		propertyBp.setLabelLong(getMessage("login.label.long"));
		propertyBp.setDescription(getMessage("login.description"));
		propertyBp.setMandatory(true);

		propertyBp = addProperty(IPerson.NAME_PROPERTY);
		propertyBp.setLabel(getMessage("name.label"));
		propertyBp.setDescription(getMessage("name.description"));
		propertyBp.setMandatory(true);

		propertyBp = addProperty(IPerson.PASSWORD_PROPERTY);
		propertyBp.setLabel(getMessage("password.label"));
		propertyBp.setDescription(getMessage("password.description"));
		propertyBp.setVisible(false);

		propertyBp = addProperty(IPerson.PASSWORD_REPEAT_PROPERTY);
		propertyBp.setLabel(getMessage("passwordRepeat.label"));
		propertyBp.setDescription(getMessage("passwordRepeat.description"));
		propertyBp.setVisible(false);

		propertyBp = addProperty(IPerson.ROLE_NAMES_PROPERTY);
		propertyBp.setLabel(getMessage("roleNames.label"));
		propertyBp.setDescription(getMessage("roleNames.description"));
		propertyBp.setElementValueType(String.class);
		propertyBp.setSortable(false);
		propertyBp.setFilterable(true);

		propertyBp = addProperty(IPerson.ACTIVE_PROPERTY);
		propertyBp.setLabel(getMessage("active.label"));
		propertyBp.setDescription(getMessage("active.description"));
		propertyBp.setDefaultValue(Boolean.TRUE);
		propertyBp.setMandatory(true);
		propertyBp.setEditable(false);

		addVersionProperty();
	}

	private static IMessage getMessage(final String keySuffix) {
		Assert.paramNotEmpty(keySuffix, "keySuffix");
		return EntityMessages.getMessage("PersonDtoDescriptorBuilder." + keySuffix);
	}
}
