package de.fastfelix771.townywands.files;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.bukkit.Material;

import lombok.Getter;
import lombok.Setter;

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
