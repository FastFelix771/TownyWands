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

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.bukkit.Material;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModularItem {

	@Getter @Setter
	@XmlAttribute(name = "slot")
	private int slot = 0;

	@Getter @Setter
	@XmlAttribute(name = "material")
	private Material material = Material.STONE;

	@Getter @Setter
	@XmlAttribute(name = "meta")
	private short metaID = 0;

	@Getter @Setter
	@XmlAttribute(name = "quantity")
	private int amount = 1;

	@Getter @Setter
	@XmlAttribute(name = "enchanted")
	private boolean enchanted = false;

	@Getter @Setter
	@XmlAttribute(name = "hide-flags")
	private boolean hideFlags = true;

	@Getter @Setter
	@XmlElement(name = "name")
	private String displayName = material.toString();

	@Getter
	@XmlElementWrapper(name = "lore")
	@XmlElement(name = "entry")
	private List<String> lore = new LinkedList<>();

	@Getter
	@XmlElementWrapper(name = "commands")
	@XmlElement(name = "command")
	private List<InventoryCommand> commands = new LinkedList<>();

}
