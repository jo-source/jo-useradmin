/*
 * Copyright (c) 2012, Michael Grossmann
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the jo-widgets.org nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package org.jowidgets.useradmin.ui.defaults;

import org.jowidgets.addons.icons.silkicons.SilkIcons;
import org.jowidgets.api.image.IconsSmall;
import org.jowidgets.api.toolkit.Toolkit;
import org.jowidgets.cap.ui.api.icons.CapIcons;
import org.jowidgets.common.image.IImageRegistry;
import org.jowidgets.useradmin.ui.icons.UserAdminIcons;

public final class UserAdminSilkIconsInitializer {

	private UserAdminSilkIconsInitializer() {}

	public static void initialize() {
		final IImageRegistry registry = Toolkit.getImageRegistry();

		registry.registerImageConstant(IconsSmall.OK, SilkIcons.TICK);
		registry.registerImageConstant(IconsSmall.REFRESH, SilkIcons.ARROW_REFRESH);
		registry.registerImageConstant(IconsSmall.UNDO, SilkIcons.ARROW_UNDO);

		registry.registerImageConstant(CapIcons.ADD_LINK, SilkIcons.LINK_ADD);
		registry.registerImageConstant(CapIcons.REMOVE_LINK, SilkIcons.LINK_BREAK);

		registry.registerImageConstant(UserAdminIcons.USER_ADMINISTRATION_ICON, SilkIcons.GROUP);

		registry.registerImageConstant(UserAdminIcons.PERSON, SilkIcons.USER);
		registry.registerImageConstant(UserAdminIcons.CREATE_PERSON, SilkIcons.USER_ADD);
		registry.registerImageConstant(UserAdminIcons.DELETE_PERSON, SilkIcons.USER_DELETE);
		registry.registerImageConstant(UserAdminIcons.LINKED_PERSONS, SilkIcons.LINK);
		registry.registerImageConstant(UserAdminIcons.ACTIVATE_PERSON, SilkIcons.STATUS_ONLINE);
		registry.registerImageConstant(UserAdminIcons.DEACTIVATE_PERSON, SilkIcons.STATUS_OFFLINE);

		registry.registerImageConstant(UserAdminIcons.ROLE, SilkIcons.GROUP);
		registry.registerImageConstant(UserAdminIcons.CREATE_ROLE, SilkIcons.GROUP_ADD);
		registry.registerImageConstant(UserAdminIcons.DELETE_ROLE, SilkIcons.GROUP_DELETE);
		registry.registerImageConstant(UserAdminIcons.LINKED_ROLES, SilkIcons.LINK);
		registry.registerImageConstant(UserAdminIcons.ROLE_IN_USE, SilkIcons.LINK);
		registry.registerImageConstant(UserAdminIcons.ROLE_NOT_IN_USE, SilkIcons.LINK_BREAK);

		registry.registerImageConstant(UserAdminIcons.AUTHORIZATION, SilkIcons.KEY);
		registry.registerImageConstant(UserAdminIcons.CREATE_AUTHORIZATION, SilkIcons.KEY_ADD);
		registry.registerImageConstant(UserAdminIcons.DELETE_AUTHORIZATION, SilkIcons.KEY_DELETE);
		registry.registerImageConstant(UserAdminIcons.LINKED_AUTHORIZATIONS, SilkIcons.LINK);
		registry.registerImageConstant(UserAdminIcons.AUTHORIZATION_IN_USE, SilkIcons.LINK);
		registry.registerImageConstant(UserAdminIcons.AUTHORIZATION_NOT_IN_USE, SilkIcons.LINK_BREAK);
	}

}
