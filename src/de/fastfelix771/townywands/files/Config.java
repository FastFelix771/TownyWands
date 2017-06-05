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
package de.fastfelix771.townywands.files;

import static java.lang.Math.max;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "configuration")
public final class Config {

	@XmlAttribute(name = "config-version", required = true)
	public int version = 2100;
	
	@XmlAttribute(name = "check-for-updates", required = true)
	public boolean updateChecking = true;

	@XmlElement(name = "metrics", required = true)
	public boolean useMetrics = true;
	
	@XmlElement(name = "auto-translate", required = true)
	public boolean autoTranslate = false;
	
	@XmlElement(name = "cpu-threads", required = true)
	public int threads = max(1, Runtime.getRuntime().availableProcessors() - 1);
	
	@XmlElement(name = "bungeecord", required = true)
	public boolean bungee = false;

}
