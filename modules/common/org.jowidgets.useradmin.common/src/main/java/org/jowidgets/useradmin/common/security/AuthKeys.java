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

package org.jowidgets.useradmin.common.security;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class AuthKeys {

	//Executor services
	public static final String EXECUTOR_ACTIVATE_PERSON = "USERADMIN_EXECUTOR_ACTIVATE_PERSON";
	public static final String EXECUTOR_DEACTIVATE_PERSON = "USERADMIN_EXECUTOR_DEACTIVATE_PERSON";

	//CRUD services
	public static final String CREATE_PERSON = "USERADMIN_CREATE_PERSON";
	public static final String READ_PERSON = "USERADMIN_READ_PERSON";
	public static final String UPDATE_PERSON = "USERADMIN_UPDATE_PERSON";
	public static final String DELETE_PERSON = "USERADMIN_DELETE_PERSON";

	public static final String CREATE_ROLE = "USERADMIN_CREATE_ROLE";
	public static final String READ_ROLE = "USERADMIN_READ_ROLE";
	public static final String UPDATE_ROLE = "USERADMIN_UPDATE_ROLE";
	public static final String DELETE_ROLE = "USERADMIN_DELETE_ROLE";

	public static final String CREATE_AUTHORIZATION = "USERADMIN_CREATE_AUTHORIZATION";
	public static final String READ_AUTHORIZATION = "USERADMIN_READ_AUTHORIZATION";
	public static final String UPDATE_AUTHORIZATION = "USERADMIN_UPDATE_AUTHORIZATION";
	public static final String DELETE_AUTHORIZATION = "USERADMIN_DELETE_AUTHORIZATION";

	public static final String CREATE_PERSON_ROLE_LINK = "USERADMIN_CREATE_PERSON_ROLE_LINK";
	public static final String READ_PERSON_ROLE_LINK = "USERADMIN_READ_PERSON_ROLE_LINK";
	public static final String UPDATE_PERSON_ROLE_LINK = "USERADMIN_UPDATE_PERSON_ROLE_LINK";
	public static final String DELETE_PERSON_ROLE_LINK = "USERADMIN_DELETE_PERSON_ROLE_LINK";

	public static final String CREATE_ROLE_AUTHORIZATION_LINK = "USERADMIN_CREATE_ROLE_AUTHORIZATION_LINK";
	public static final String READ_ROLE_AUTHORIZATION_LINK = "USERADMIN_READ_ROLE_AUTHORIZATION_LINK";
	public static final String UPDATE_ROLE_AUTHORIZATION_LINK = "USERADMIN_UPDATE_ROLE_AUTHORIZATION_LINK";
	public static final String DELETE_ROLE_AUTHORIZATION_LINK = "USERADMIN_DELETE_ROLE_AUTHORIZATION_LINK";

	//Authorizations collection
	public static final Collection<String> ALL_AUTHORIZATIONS = createAuthorizations();

	private AuthKeys() {}

	private static List<String> createAuthorizations() {
		final List<String> result = new LinkedList<String>();
		for (final Field field : AuthKeys.class.getDeclaredFields()) {
			if (field.getType().equals(String.class)) {
				try {
					result.add((String) field.get(AuthKeys.class));
				}
				catch (final Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return result;
	}

}
