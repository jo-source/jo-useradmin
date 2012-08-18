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

package org.jowidgets.useradmin.ui.plugins.tree;

import org.jowidgets.cap.common.api.sort.Sort;
import org.jowidgets.cap.ui.api.tree.IBeanRelationNodeModelBluePrint;
import org.jowidgets.cap.ui.api.tree.IBeanRelationNodeModelConfigurator;
import org.jowidgets.cap.ui.api.types.IEntityTypeId;
import org.jowidgets.useradmin.common.bean.IAuthorization;
import org.jowidgets.useradmin.common.bean.IPerson;
import org.jowidgets.useradmin.common.bean.IRole;
import org.jowidgets.useradmin.ui.icons.UserAdminIcons;

final class BeanRelationNodeModelConfigurator implements IBeanRelationNodeModelConfigurator {

	@Override
	public <CHILD_BEAN_TYPE> void configureNode(
		final IEntityTypeId<CHILD_BEAN_TYPE> entityTypeId,
		final IBeanRelationNodeModelBluePrint<CHILD_BEAN_TYPE, IBeanRelationNodeModelBluePrint<?, ?>> bluePrint) {

		final Class<CHILD_BEAN_TYPE> beanType = entityTypeId.getBeanType();

		//sort setting
		if (beanType == IPerson.class) {
			bluePrint.setDefaultSort(Sort.create(IPerson.NAME_PROPERTY));
			bluePrint.setIcon(UserAdminIcons.LINKED_PERSONS);
		}
		else if (beanType == IRole.class) {
			bluePrint.setDefaultSort(Sort.create(IRole.NAME_PROPERTY));
			bluePrint.setIcon(UserAdminIcons.LINKED_ROLES);
		}
		else if (beanType == IAuthorization.class) {
			bluePrint.setDefaultSort(Sort.create(IAuthorization.KEY_PROPERTY));
			bluePrint.setIcon(UserAdminIcons.LINKED_AUTHORIZATIONS);
		}
	}
}
