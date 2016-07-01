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
@Table(name = "GUIs")
@NoArgsConstructor
public class EntityGUI implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter @Setter
	private String name;
	
	@Basic
	@Column(name = "command", nullable = false)
	@Getter @Setter
	private String command;
	
	@Basic
	@Column(name = "permission", nullable = false)
	@Getter @Setter
	private String permission;
	
}