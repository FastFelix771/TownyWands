package de.fastfelix771.townywands.files;

import javax.xml.bind.annotation.XmlAttribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class InventoryCommand {

	@Getter @Setter
	@XmlAttribute(name = "console")
	private boolean console = false;

	@Getter @Setter
	@XmlAttribute(name = "value")
	private String value = "help";

}
