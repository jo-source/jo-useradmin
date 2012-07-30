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

package org.jowidgets.useradmin.ui.plugins;

import org.jowidgets.cap.ui.api.plugin.IBeanTableMenuContributionPlugin;
import org.jowidgets.cap.ui.api.plugin.IBeanTableMenuInterceptorPlugin;
import org.jowidgets.cap.ui.api.plugin.IBeanTablePlugin;
import org.jowidgets.plugin.tools.PluginProviderBuilder;
import org.jowidgets.useradmin.common.entity.EntityIds;
import org.jowidgets.useradmin.ui.plugins.table.AuthorizationMenuInterceptorPlugin;
import org.jowidgets.useradmin.ui.plugins.table.PersonMenuContributionPlugin;
import org.jowidgets.useradmin.ui.plugins.table.RoleMenuInterceptorPlugin;
import org.jowidgets.useradmin.ui.plugins.table.SearchFilterOnTablePlugin;

public final class UserAdminPluginProviderBuilder extends PluginProviderBuilder {

	public UserAdminPluginProviderBuilder() {

		addPlugin(
				IBeanTablePlugin.ID,
				new SearchFilterOnTablePlugin(),
				IBeanTablePlugin.ENTITIY_ID_PROPERTY_KEY,
				EntityIds.PERSON,
				EntityIds.ROLE,
				EntityIds.AUTHORIZATION);

		addPlugin(
				IBeanTableMenuContributionPlugin.ID,
				new PersonMenuContributionPlugin(),
				IBeanTableMenuContributionPlugin.ENTITIY_ID_PROPERTY_KEY,
				EntityIds.PERSON,
				EntityIds.LINKED_PERSONS_OF_ROLES);

		addPlugin(
				IBeanTableMenuInterceptorPlugin.ID,
				new RoleMenuInterceptorPlugin(),
				IBeanTableMenuInterceptorPlugin.ENTITIY_ID_PROPERTY_KEY,
				EntityIds.ROLE);

		addPlugin(
				IBeanTableMenuInterceptorPlugin.ID,
				new AuthorizationMenuInterceptorPlugin(),
				IBeanTableMenuInterceptorPlugin.ENTITIY_ID_PROPERTY_KEY,
				EntityIds.AUTHORIZATION);

	}
}
