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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Index;
import org.jowidgets.cap.service.jpa.api.query.QueryPath;
import org.jowidgets.cap.service.jpa.tools.entity.EntityManagerProvider;
import org.jowidgets.useradmin.common.bean.IPerson;
import org.jowidgets.util.Assert;
import org.jowidgets.util.EmptyCheck;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"loginName"}))
public class Person extends Bean implements IPerson {

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
	@MapKey(name = "roleId")
	@OrderBy("roleId ASC")
	private Map<Long, PersonRoleLink> personRoleLinks = new LinkedHashMap<Long, PersonRoleLink>();

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
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(final String passwordHash) {
		this.passwordHash = passwordHash;
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

	public Map<Long, PersonRoleLink> getPersonRoleLinks() {
		return personRoleLinks;
	}

	public void setPersonRoleLinks(final Map<Long, PersonRoleLink> personRoleLinks) {
		this.personRoleLinks = personRoleLinks;
	}

	@Override
	@QueryPath(path = {"personRoleLinks", "roleId"})
	public List<Long> getRoleIds() {
		return new LinkedList<Long>(personRoleLinks.keySet());
	}

	@Override
	public void setRoleIds(List<Long> newRoleIds) {
		if (newRoleIds == null) {
			newRoleIds = new LinkedList<Long>();
		}
		final Set<Long> newRoleIdsSet = new LinkedHashSet<Long>(newRoleIds);
		final Map<Long, PersonRoleLink> newPersonRoleLinks = new LinkedHashMap<Long, PersonRoleLink>();
		final EntityManager em = EntityManagerProvider.get();

		for (final Entry<Long, PersonRoleLink> entry : personRoleLinks.entrySet()) {
			if (!newRoleIdsSet.contains(entry.getKey())) {
				em.remove(entry.getValue());
			}
		}

		for (final Long newId : newRoleIds) {
			PersonRoleLink personRoleLink = personRoleLinks.get(newId);
			if (personRoleLink == null) {
				personRoleLink = new PersonRoleLink();
				if (getId() == null) {
					em.persist(this);
				}
				personRoleLink.setPersonId(getId());
				personRoleLink.setRoleId(newId);
				em.persist(personRoleLink);
			}
			newPersonRoleLinks.put(newId, personRoleLink);
		}

		personRoleLinks = newPersonRoleLinks;
	}

	public void setPassword(final String password) {
		if (!EmptyCheck.isEmpty(password)) {
			setPasswordHash(encodePassword(password));
		}
		else {
			setPasswordHash(null);
		}
	}

	public boolean isAuthenticated(final String password) {

		String currentPasswordHash = getPasswordHash();
		if (currentPasswordHash == null) {
			currentPasswordHash = encodePassword(getLoginName());
		}
		if (encodePassword(password).equals(currentPasswordHash)) {
			return true;
		}
		else {
			return false;
		}

	}

	private static String encodePassword(final String password) {
		Assert.paramNotNull(password, "password");

		try {
			final MessageDigest md5 = MessageDigest.getInstance("MD5");
			final StringBuffer strBuf = new StringBuffer();
			md5.update(password.getBytes());

			for (final byte b : md5.digest()) {
				strBuf.append(byteToHexString(b));
			}

			return strBuf.toString();
		}
		catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private static String byteToHexString(final byte b) {
		final int value = (b & 0x7F) + (b < 0 ? 128 : 0);
		String ret = value < 16 ? "0" : "";
		ret = ret + Integer.toHexString(value).toLowerCase();
		return ret;
	}
}
