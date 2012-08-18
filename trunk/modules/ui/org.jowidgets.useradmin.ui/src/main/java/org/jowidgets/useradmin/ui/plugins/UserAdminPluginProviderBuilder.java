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

import org.jowidgets.cap.ui.api.plugin.IAttributePlugin;
import org.jowidgets.cap.ui.api.plugin.IBeanFormPlugin;
import org.jowidgets.cap.ui.api.plugin.IBeanProxyLabelRendererPlugin;
import org.jowidgets.cap.ui.api.plugin.IBeanRelationTreeModelPlugin;
import org.jowidgets.cap.ui.api.plugin.IBeanTableMenuContributionPlugin;
import org.jowidgets.cap.ui.api.plugin.IBeanTableMenuInterceptorPlugin;
import org.jowidgets.cap.ui.api.plugin.IBeanTablePlugin;
import org.jowidgets.plugin.tools.PluginProviderBuilder;
import org.jowidgets.useradmin.common.bean.IAuthorization;
import org.jowidgets.useradmin.common.bean.IPerson;
import org.jowidgets.useradmin.common.bean.IRole;
import org.jowidgets.useradmin.common.entity.EntityIds;
import org.jowidgets.useradmin.ui.plugins.attribute.GlobalAttributesPlugin;
import org.jowidgets.useradmin.ui.plugins.attribute.PersonAttributesPlugin;
import org.jowidgets.useradmin.ui.plugins.bean.AuthorizationRendererPlugin;
import org.jowidgets.useradmin.ui.plugins.bean.PersonRendererPlugin;
import org.jowidgets.useradmin.ui.plugins.bean.RoleRendererPlugin;
import org.jowidgets.useradmin.ui.plugins.form.PersonFormPlugin;
import org.jowidgets.useradmin.ui.plugins.table.AuthorizationMenuInterceptorPlugin;
import org.jowidgets.useradmin.ui.plugins.table.PersonMenuContributionPlugin;
import org.jowidgets.useradmin.ui.plugins.table.PersonMenuInterceptorPlugin;
import org.jowidgets.useradmin.ui.plugins.table.RoleMenuInterceptorPlugin;
import org.jowidgets.useradmin.ui.plugins.table.SearchFilterOnTablePlugin;
import org.jowidgets.useradmin.ui.plugins.tree.RelationTreeModelPlugin;

public final class UserAdminPluginProviderBuilder extends PluginProviderBuilder {

	public UserAdminPluginProviderBuilder() {

		addPlugin(IAttributePlugin.ID, new GlobalAttributesPlugin());

		addPlugin(IBeanRelationTreeModelPlugin.ID, new RelationTreeModelPlugin());

		addAttributePlugin(new PersonAttributesPlugin(), IPerson.class);

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
				new PersonMenuInterceptorPlugin(),
				IBeanTableMenuInterceptorPlugin.ENTITIY_ID_PROPERTY_KEY,
				EntityIds.PERSON);

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

		addPlugin(IBeanFormPlugin.ID, new PersonFormPlugin(), IBeanFormPlugin.BEAN_TYPE_PROPERTY_KEY, IPerson.class);

		addBeanProxyLabelRendererPlugin(new PersonRendererPlugin(), IPerson.class);
		addBeanProxyLabelRendererPlugin(new RoleRendererPlugin(), IRole.class);
		addBeanProxyLabelRendererPlugin(new AuthorizationRendererPlugin(), IAuthorization.class);
	}

	private void addAttributePlugin(final IAttributePlugin plugin, final Class<?> beanType) {
		addPlugin(IAttributePlugin.ID, plugin, IAttributePlugin.BEAN_TYPE_PROPERTY_KEY, beanType);
	}

	private void addBeanProxyLabelRendererPlugin(final IBeanProxyLabelRendererPlugin<?> plugin, final Class<?> beanType) {
		addPlugin(IBeanProxyLabelRendererPlugin.ID, plugin, IBeanProxyLabelRendererPlugin.BEAN_TYPE_PROPERTY_KEY, beanType);
	}
}
