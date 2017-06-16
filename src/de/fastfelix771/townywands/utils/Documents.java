/*******************************************************************************
 * Copyright (C) 2017 Felix Drescher-Hackel
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.fastfelix771.townywands.utils;

import java.io.File;
import java.nio.file.Paths;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Provides convenience methods for loading and saving of XML documents via JAXB.
 */
public final class Documents {

	/**
	 * The suffix for XML documents
	 */
	private static final String XML_SUFFIX = ".xml";

	/**
	 * Tries to load a document from disk. <br>
	 * If the file doesn't exist, <code>null</code> will be returned.
	 * @param namespace the parent folder of the file
	 * @param name the file name
	 * @param type the class representing the file structure in java
	 * @return an instance of the given type, populated with the values from the file
	 * @throws JAXBException if the de-serialization fails for any reason
	 */
	public static <T> T load(String namespace, String name, Class<T> type) throws JAXBException {
		File file = Paths.get("plugins", "TownyWands", namespace, name.concat(XML_SUFFIX)).toFile();
		if (!file.exists()) return null;

		return JAXB.unmarshal(file, type);
	}

	/**
	 * Saves the given document to disk. <br>
	 * The file path will look like this: <b>/plugins/TownyWands/namespace/name.xml</b>
	 * @param namespace the parent folder of the file
	 * @param name the file name
	 * @param document the instance containing the JAXB annotations and the values that should be persisted
	 * @throws JAXBException if anything goes wrong while persisting the object to disk
	 */
	public static <T> void save(String namespace, String name, T document) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(document.getClass());
		Marshaller marshaller = context.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		File file = Paths.get("plugins", "TownyWands", namespace, name.concat(XML_SUFFIX)).toFile();
		if (!file.exists()) file.getParentFile().mkdirs();

		marshaller.marshal(document, file);
	}

	/**
	 * Saves the given configuration to disk only if it doesn't exist yet!
	 * @param namespace the parent folder of the file
	 * @param name the file name
	 * @param document the instance containing the JAXB annotations and the values that should be persisted
	 * @throws JAXBException if anything goes wrong while persisting the object to disk
	 */
	public static <T> void saveDefault(String namespace, String name, T document) throws JAXBException {
		File file = Paths.get("plugins", "TownyWands", namespace, name.concat(XML_SUFFIX)).toFile();
		if (file.exists()) return;

		save(namespace, name, document);
	}

	/**
	 * Checks if the document can be loaded from disk without any errors.
	 * @param namespace the parent folder of the file
	 * @param name the file name
	 * @param type the class representing the file structure in java
	 * @return <code>true</code> if the file loads successful, <code>false</code> otherwise.
	 */
	public static <T> boolean validate(String namespace, String name, Class<T> type) {
		try {
			load(namespace, name, type);
			return true;
		} catch (JAXBException e) {
			return false;
		}
	}

}
