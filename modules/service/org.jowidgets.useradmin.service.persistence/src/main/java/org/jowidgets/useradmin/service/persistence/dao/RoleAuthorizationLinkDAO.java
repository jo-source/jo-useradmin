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

package org.jowidgets.useradmin.service.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jowidgets.useradmin.service.persistence.bean.RoleAuthorizationLink;

public final class RoleAuthorizationLinkDAO {

	private RoleAuthorizationLinkDAO() {}

	public static RoleAuthorizationLink findRoleAuthorizationLink(
		final EntityManager em,
		final Long roleId,
		final Long authorizationId) {

		try {
			final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			final CriteriaQuery<RoleAuthorizationLink> query = criteriaBuilder.createQuery(RoleAuthorizationLink.class);
			final Root<RoleAuthorizationLink> root = query.from(RoleAuthorizationLink.class);
			final Path<String> roleIdPath = root.get(RoleAuthorizationLink.ROLE_ID_PROPERTY);
			final Path<String> authIdPath = root.get(RoleAuthorizationLink.AUTHORIZATION_ID_PROPERTY);
			final Predicate rolePredicate = criteriaBuilder.equal(roleIdPath, roleId);
			final Predicate authPredicate = criteriaBuilder.equal(authIdPath, authorizationId);
			query.where(criteriaBuilder.and(rolePredicate, authPredicate));
			return em.createQuery(query).getSingleResult();
		}
		catch (final NoResultException e) {
			return null;
		}
	}

}
