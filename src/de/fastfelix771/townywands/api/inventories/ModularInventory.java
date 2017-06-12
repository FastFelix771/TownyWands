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
package de.fastfelix771.townywands.api.inventories;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "inventory")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModularInventory {

	@Getter @Setter
	@XmlAttribute(name = "title")
	private String title = "Modular Inventory";

	@Getter @Setter
	@XmlAttribute(name = "slots")
	private int size = 54;

	@Getter @Setter
	@XmlAttribute(name = "command")
	private String command = "gui-" + new BigInteger(16, new Random()).toString();

	@Getter @Setter
	@XmlAttribute(name = "permission")
	private String permission = "townywands.use.".concat(command);

	@Getter
	@XmlElementWrapper(name = "items")
	@XmlElement(name = "item")
	private List<ModularItem> items = new LinkedList<>();

}
