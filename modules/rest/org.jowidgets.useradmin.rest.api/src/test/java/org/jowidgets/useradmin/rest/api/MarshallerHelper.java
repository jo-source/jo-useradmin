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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;

final class MarshallerHelper {

	private static final Map<String, Object> JSON_MARSHALLER_PROPERTIES = createJsonMarshallerProperties();

	private static Map<String, Object> createJsonMarshallerProperties() {
		final Map<String, Object> result = new HashMap<String, Object>();
		result.put(MarshallerProperties.MEDIA_TYPE, "application/json");
		result.put(MarshallerProperties.JSON_INCLUDE_ROOT, Boolean.TRUE);
		return result;
	}

	static String marshallXml(final Object object) {
		return marschall(object, null);
	}

	static <OBJECT_TYPE> OBJECT_TYPE unmarshallXml(final String string, final Class<OBJECT_TYPE> type) {
		return unmarschall(string, type, null);
	}

	static String marshallJson(final Object object) {
		return marschall(object, JSON_MARSHALLER_PROPERTIES);
	}

	static <OBJECT_TYPE> OBJECT_TYPE unmarshallJson(final String string, final Class<OBJECT_TYPE> type) {
		return unmarschall(string, type, JSON_MARSHALLER_PROPERTIES);
	}

	private static String marschall(final Object object, final Map<String, Object> marchallerProps) {
		try {
			return marschallImpl(object, marchallerProps);
		}
		catch (final JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	private static <OBJECT_TYPE> OBJECT_TYPE unmarschall(
		final String stringRepresentation,
		final Class<OBJECT_TYPE> type,
		final Map<String, Object> unmarchallerProps) {
		try {
			return unmarschallImpl(stringRepresentation, type, unmarchallerProps);
		}
		catch (final JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	private static String marschallImpl(final Object object, final Map<String, Object> marchallerProps) throws JAXBException {

		final JAXBContext context = JAXBContext.newInstance(object.getClass());
		final Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		if (marchallerProps != null) {
			for (final Entry<String, Object> property : marchallerProps.entrySet()) {
				marshaller.setProperty(property.getKey(), property.getValue());
			}
		}

		final StringWriter stringWriter = new StringWriter();
		marshaller.marshal(object, stringWriter);

		return stringWriter.toString();
	}

	@SuppressWarnings("unchecked")
	private static <OBJECT_TYPE> OBJECT_TYPE unmarschallImpl(
		final String stringRepresentation,
		final Class<OBJECT_TYPE> type,
		final Map<String, Object> unmarchallerProps) throws JAXBException {

		final JAXBContext context = JAXBContext.newInstance(type);
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		if (unmarchallerProps != null) {
			for (final Entry<String, Object> property : unmarchallerProps.entrySet()) {
				unmarshaller.setProperty(property.getKey(), property.getValue());
			}
		}
		return (OBJECT_TYPE) unmarshaller.unmarshal(new StringReader(stringRepresentation));
	}

}
