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

@XmlRootElement(name = "Credentials")
@XmlType(propOrder = {"username", "grantedAuthorities"})
public final class Principal {

	private String username;
	private List<String> grantedAuthorities;

	public Principal() {
		this(null);
	}

	public Principal(final String username) {
		this(username, null);
	}

	public Principal(final String username, final List<String> grantedAuthorities) {
		this.username = username;
		this.grantedAuthorities = grantedAuthorities;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
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
		return "Principal [username=" + username + ", grantedAuthorities=" + grantedAuthorities + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grantedAuthorities == null) ? 0 : grantedAuthorities.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		if (!(obj instanceof Principal)) {
			return false;
		}
		final Principal other = (Principal) obj;
		if (grantedAuthorities == null) {
			if (other.grantedAuthorities != null) {
				return false;
			}
		}
		else if (!grantedAuthorities.equals(other.grantedAuthorities)) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		}
		else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}

}
