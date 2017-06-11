package de.fastfelix771.townywands.files;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class InventoryCommand {

	@Getter @Setter
	@XmlAttribute(name = "value")
	private String value = "help";

	@Getter @Setter
	@XmlAttribute(name = "console")
	private boolean console = false;

}
