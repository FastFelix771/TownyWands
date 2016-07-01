package de.fastfelix771.townywands.dao;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.Material;

import de.fastfelix771.townywands.lang.Language;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Items")
@NoArgsConstructor
public class EntityItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter @Setter
	private int id;

	@Basic
	@Column(name = "inventory_id")
	@Getter @Setter
	private int inventory;

	@Basic
	@Column(name = "inventory_slot")
	@Getter @Setter
	private int slot;

	@Column(name = "material", nullable = false)
	@Getter @Setter
	private Material material = Material.STONE;

	@Column(name = "durability", nullable = false)
	@Getter @Setter
	private short metaID = 0;

	@Column(name = "quantity", nullable = false)
	@Getter @Setter
	private int amount = 1;

	@Column(name = "enchanted", nullable = false)
	@Getter @Setter
	private boolean enchanted = false;

	@Column(name = "displayname")
	@Getter @Setter
	private String displayname = "Default Displayname";

	@Column(name = "lore")
	@Getter @Setter
	private String lore = null;

	@Column(name = "commands")
	@Getter @Setter
	private String commands = null;

	@Column(name = "console_commands")
	@Getter @Setter
	private String consoleCommands = null;

	@Column(name = "nbt_tag")
	@Getter @Setter
	private String tag = null;

	@Column(name = "hide_flags", nullable = false)
	@Getter @Setter
	private boolean hideFlags = true;

	@Column(name = "language", nullable = false)
	@Getter @Setter
	private Language language = Language.ENGLISH;

}