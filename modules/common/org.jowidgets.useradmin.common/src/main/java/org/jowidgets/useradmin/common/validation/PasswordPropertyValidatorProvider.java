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

package org.jowidgets.useradmin.common.validation;

import org.jowidgets.cap.common.api.bean.IBeanDtoDescriptor;
import org.jowidgets.cap.common.api.bean.IProperty;
import org.jowidgets.cap.common.api.service.IEntityService;
import org.jowidgets.service.api.ServiceProvider;
import org.jowidgets.useradmin.common.bean.IPerson;
import org.jowidgets.useradmin.common.entity.EntityIds;
import org.jowidgets.validation.IValidator;
import org.jowidgets.validation.Validator;

public final class PasswordPropertyValidatorProvider {

	private PasswordPropertyValidatorProvider() {}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static IValidator<String> get() {
		final IEntityService entityService = ServiceProvider.getService(IEntityService.ID);
		if (entityService != null) {
			final IBeanDtoDescriptor personDescriptor = entityService.getDescriptor(EntityIds.PERSON);
			if (personDescriptor != null) {
				for (final IProperty property : personDescriptor.getProperties()) {
					if (IPerson.PASSWORD_PROPERTY.equals(property.getName())) {
						final IValidator<?> validator = property.getValidator();
						if (validator != null) {
							return (IValidator) validator;
						}
					}
				}
			}
		}
		return Validator.okValidator();
	}
}
