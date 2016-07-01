package de.fastfelix771.townywands.dao;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Inventories")
@NoArgsConstructor
public class EntityInventory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter @Setter
	private int id;

	@Basic
	@Column(name = "gui")
	@Getter @Setter
	private String gui;

	@Basic
	@Column(name = "title", length = 32, nullable = false)
	@Getter @Setter
	private String title = "A TownyWands Inventory";

	@Basic
	@Column(name = "slots", nullable = false)
	@Getter @Setter
	private int slots = 54;

	@Basic
	@Column(name = "active", nullable = false)
	@Getter @Setter
	private boolean enabled = false;

}