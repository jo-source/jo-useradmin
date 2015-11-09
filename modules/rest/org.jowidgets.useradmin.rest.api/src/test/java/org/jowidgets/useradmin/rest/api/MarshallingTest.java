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

import java.util.Arrays;

import junit.framework.Assert;

import org.jowidgets.util.security.String2Hash;
import org.junit.Test;

public class MarshallingTest {

	@Test
	public void testXmlMarschallingCredentials() {
		final Credentials credentials = createCredentials();

		final String xml = MarshallerHelper.marshallXml(credentials);
		final Credentials credentials2 = MarshallerHelper.unmarshallXml(xml, Credentials.class);

		Assert.assertFalse(credentials == credentials2);
		Assert.assertEquals(credentials, credentials2);
	}

	@Test
	public void testJsonMarschallingCredentials() {
		final Credentials credentials = createCredentials();

		final String json = MarshallerHelper.marshallJson(credentials);
		final Credentials credentials2 = MarshallerHelper.unmarshallJson(json, Credentials.class);

		Assert.assertFalse(credentials == credentials2);
		Assert.assertEquals(credentials, credentials2);
	}

	@Test
	public void testXmlMarschallingPrincipal() {
		final Principal principal = createPrincipal();

		final String xml = MarshallerHelper.marshallXml(principal);
		final Principal principal2 = MarshallerHelper.unmarshallXml(xml, Principal.class);

		Assert.assertFalse(principal == principal2);
		Assert.assertEquals(principal, principal2);
	}

	@Test
	public void testJsonMarschallingPrincipal() {
		final Principal principal = createPrincipal();

		final String json = MarshallerHelper.marshallJson(principal);
		final Principal principal2 = MarshallerHelper.unmarshallJson(json, Principal.class);

		Assert.assertFalse(principal == principal2);
		Assert.assertEquals(principal, principal2);
	}

	@Test
	public void testXmlMarschallingPerson() {
		final Person person = createPerson();

		final String xml = MarshallerHelper.marshallXml(person);
		final Person person2 = MarshallerHelper.unmarshallXml(xml, Person.class);

		Assert.assertFalse(person == person2);
		Assert.assertEquals(person, person2);
	}

	@Test
	public void testJsonMarschallingPerson() {
		final Person person = createPerson();

		final String json = MarshallerHelper.marshallJson(person);
		final Person person2 = MarshallerHelper.unmarshallJson(json, Person.class);

		Assert.assertFalse(person == person2);
		Assert.assertEquals(person, person2);
	}

	@Test
	public void testXmlMarschallingAuthorization() {
		final Authorization authorization = createAuthorization();

		final String xml = MarshallerHelper.marshallXml(authorization);
		final Authorization authorization2 = MarshallerHelper.unmarshallXml(xml, Authorization.class);

		Assert.assertFalse(authorization == authorization2);
		Assert.assertEquals(authorization, authorization2);
	}

	@Test
	public void testJsonMarschallingAuthorization() {
		final Authorization authorization = createAuthorization();

		final String json = MarshallerHelper.marshallJson(authorization);
		final Authorization authorization2 = MarshallerHelper.unmarshallJson(json, Authorization.class);

		Assert.assertFalse(authorization == authorization2);
		Assert.assertEquals(authorization, authorization2);
	}

	@Test
	public void testXmlMarschallingRole() {
		final Role role = createRole();

		final String xml = MarshallerHelper.marshallXml(role);
		final Role role2 = MarshallerHelper.unmarshallXml(xml, Role.class);

		Assert.assertFalse(role == role2);
		Assert.assertEquals(role, role2);
	}

	@Test
	public void testJsonMarschallingRole() {
		final Role role = createRole();

		final String json = MarshallerHelper.marshallJson(role);
		final Role role2 = MarshallerHelper.unmarshallJson(json, Role.class);

		Assert.assertFalse(role == role2);
		Assert.assertEquals(role, role2);
	}

	private static Credentials createCredentials() {
		final Credentials credentials = new Credentials();
		credentials.setUsername("Foo");
		credentials.setPassword("Bar");
		return credentials;
	}

	private static Principal createPrincipal() {
		final Principal principal = new Principal();
		principal.setUsername("Foo");
		principal.setGrantedAuthorities(Arrays.asList("foo1", "foo2", "foo3", "foo4"));
		return principal;
	}

	private static final Person createPerson() {
		final Person person = new Person();
		person.setLoginName("BE");
		person.setName("Ben Ebelt");
		person.setPasswordHash(String2Hash.encode("secret password"));
		person.setActive(true);
		person.setGrantedAuthorities(Arrays.asList("AUTHORITY_1", "AUTHORITY_2"));
		return person;
	}

	private static final Authorization createAuthorization() {
		final Authorization authorization = new Authorization();
		authorization.setKey("FOO");
		authorization.setDescription("DECRIPTION OF FOO");
		return authorization;
	}

	private static final Role createRole() {
		final Role role = new Role();
		role.setName("FOO");
		role.setDescription("DECRIPTION OF FOO");
		return role;
	}

}
