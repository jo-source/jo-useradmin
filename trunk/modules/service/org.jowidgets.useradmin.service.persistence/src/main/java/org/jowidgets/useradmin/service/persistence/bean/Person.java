/*
 * Copyright (c) 2011, H.Westphal
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
package org.jowidgets.useradmin.service.persistence.bean;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Index;
import org.jowidgets.cap.service.jpa.api.query.QueryPath;
import org.jowidgets.useradmin.common.bean.IPerson;
import org.jowidgets.util.Assert;
import org.jowidgets.util.EmptyCheck;
import org.jowidgets.util.security.String2Hash;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"loginName"}))
public class Person extends Bean implements IPerson {

	private static String defaultPasswordValue = createDefaultPasswordValue();

	@Basic
	private String loginName;

	@Basic
	private String passwordHash;

	@Basic
	@Index(name = "PersonNameIndex")
	private String name;

	@Basic
	private Boolean active;

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "person")
	@BatchSize(size = 1000)
	private Set<PersonRoleLink> personRoleLinks = new HashSet<PersonRoleLink>();

	private static String createDefaultPasswordValue() {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			result.append('\u2022');
		}
		return result.toString();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String getLoginName() {
		return loginName;
	}

	@Override
	public void setLoginName(final String loginName) {
		this.loginName = loginName;
	}

	@Override
	public Boolean getActive() {
		return active;
	}

	@Override
	public void setActive(final Boolean active) {
		this.active = active;
	}

	public Set<PersonRoleLink> getPersonRoleLinks() {
		return personRoleLinks;
	}

	public void setPersonRoleLinks(final Set<PersonRoleLink> personRoleLinks) {
		Assert.paramNotNull(personRoleLinks, "personRoleLinks");
		this.personRoleLinks = personRoleLinks;
	}

	@Override
	@QueryPath(path = {"personRoleLinks", "role", "name"})
	public List<String> getRoleNames() {
		final List<String> result = new LinkedList<String>();
		for (final PersonRoleLink personRoleLink : getPersonRoleLinks()) {
			result.add(personRoleLink.getRole().getName());
		}
		return result;
	}

	public Set<String> getAuthorizationNames() {
		final Set<String> result = new HashSet<String>();
		final Set<PersonRoleLink> personRoleLinksSet = getPersonRoleLinks();
		if (personRoleLinksSet != null) {
			for (final PersonRoleLink personRoleLink : personRoleLinksSet) {
				final Role role = personRoleLink.getRole();
				if (role != null) {
					result.addAll(role.getAuthorizationNames());
				}
			}
		}
		return result;
	}

	@Override
	@QueryPath(path = {"passwordHash"})
	public String getPassword() {
		if (passwordHash != null) {
			return defaultPasswordValue;
		}
		else {
			return null;
		}
	}

	@Override
	public void setPassword(final String password) {
		if (!EmptyCheck.isEmpty(password)) {
			setPasswordHash(String2Hash.encode(password));
		}
		else {
			setPasswordHash(null);
		}
	}

	@Override
	@QueryPath(path = {"passwordHash"})
	public String getPasswordRepeat() {
		return getPassword();
	}

	@Override
	public void setPasswordRepeat(final String passwordRepeat) {
		//nothing to do
	}

	private String getPasswordHash() {
		return passwordHash;
	}

	private void setPasswordHash(final String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public boolean isAuthenticated(final String password) {
		String currentPasswordHash = getPasswordHash();
		if (currentPasswordHash == null) {
			final String currentLoginName = getLoginName();
			if (currentLoginName != null) {
				currentPasswordHash = String2Hash.encode(currentLoginName);
			}
			else {
				return false;
			}
		}
		if (String2Hash.encode(password).equals(currentPasswordHash)) {
			return true;
		}
		else {
			return false;
		}
	}

}
