/*
 * Copyright (c) 2015, grossmann
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
package org.jowidgets.useradmin.rest.api;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Person")
@XmlType(propOrder = {"loginName", "name", "passwordHash", "active", "grantedAuthorities"})
public class Person {

	private String loginName;
	private String name;
	private String passwordHash;
	private boolean active;
	private List<String> grantedAuthorities;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(final String loginName) {
		this.loginName = loginName;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(final String passwordHash) {
		this.passwordHash = passwordHash;
	}

	@XmlElementWrapper()
	@XmlElement(name = "authority")
	public List<String> getGrantedAuthorities() {
		return grantedAuthorities;
	}

	public void setGrantedAuthorities(final List<String> grantedAuthorities) {
		this.grantedAuthorities = grantedAuthorities;
	}

	@Override
	public String toString() {
		return "Person [loginName="
			+ loginName
			+ ", name="
			+ name
			+ ", passwordHash="
			+ passwordHash
			+ ", active="
			+ active
			+ ", grantedAuthorities="
			+ grantedAuthorities
			+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + ((grantedAuthorities == null) ? 0 : grantedAuthorities.hashCode());
		result = prime * result + ((loginName == null) ? 0 : loginName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((passwordHash == null) ? 0 : passwordHash.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Person)) {
			return false;
		}
		final Person other = (Person) obj;
		if (active != other.active) {
			return false;
		}
		if (grantedAuthorities == null) {
			if (other.grantedAuthorities != null) {
				return false;
			}
		}
		else if (!grantedAuthorities.equals(other.grantedAuthorities)) {
			return false;
		}
		if (loginName == null) {
			if (other.loginName != null) {
				return false;
			}
		}
		else if (!loginName.equals(other.loginName)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!name.equals(other.name)) {
			return false;
		}
		if (passwordHash == null) {
			if (other.passwordHash != null) {
				return false;
			}
		}
		else if (!passwordHash.equals(other.passwordHash)) {
			return false;
		}
		return true;
	}

}
