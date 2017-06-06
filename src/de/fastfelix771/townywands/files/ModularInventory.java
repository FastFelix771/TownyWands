package de.fastfelix771.townywands.files;

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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "inventory")
public class ModularInventory {

	@Getter @Setter
	@XmlAttribute(name = "title")
	private String title = "Modular Inventory";

	@Getter @Setter
	@XmlAttribute(name = "slots")
	private int size = 54;

	@Getter @Setter
	@XmlElement(name = "command")
	private String command = "gui-" + new BigInteger(16, new Random()).toString();

	@Getter @Setter
	@XmlElement(name = "permission")
	private String permission = "townywands.use.".concat(command);

	@Getter
	@XmlElementWrapper(name = "items")
	@XmlElement(name = "item")
	private List<ModularItem> items = new LinkedList<>();

}
