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
