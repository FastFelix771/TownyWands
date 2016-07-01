package de.fastfelix771.townywands.packets;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Version {
	UNKNOWN(-1, null, null), 
	v1_10(110, new de.fastfelix771.townywands.packets.v1_10.ProtocolLibHandler(), new de.fastfelix771.townywands.packets.v1_10.ProtocolLibvSign()), 
	v1_9(99, new de.fastfelix771.townywands.packets.v1_9.ProtocolLibHandler(), new de.fastfelix771.townywands.packets.v1_9.ProtocolLibvSign()), 
	v1_8(88, new de.fastfelix771.townywands.packets.v1_8.ProtocolLibHandler(), new de.fastfelix771.townywands.packets.v1_8.ProtocolLibvSign()), 
	v1_7(77, new de.fastfelix771.townywands.packets.v1_7.ProtocolLibHandler(), new de.fastfelix771.townywands.packets.v1_7.ProtocolLibvSign()), 
	v1_6(66, new de.fastfelix771.townywands.packets.v1_6.ProtocolLibHandler(), new de.fastfelix771.townywands.packets.v1_6.ProtocolLibvSign()),
	v1_5(55, null, null),
	v1_4(44, null, null),
	v1_3(33, null, null),
	v1_2(22, null, null),
	v1_1(11, null, null), 
	v1_0(0, null, null);

	@Getter(AccessLevel.PRIVATE) 
	private final int integer;

	@Getter
	private final PacketHandler packetHandler;

	@Getter
	private final VirtualSign virtualSign;

	public boolean isBetween(Version newer, Version older) {
		return this.getInteger() <= newer.getInteger() && this.getInteger() >= older.getInteger();
	}

	public boolean isNewerThan(Version version) {
		return version.getInteger() < this.getInteger();
	}

	public boolean isOlderThan(Version version) {
		return version.getInteger() > this.getInteger();
	}

	public static Set<Version> newerThan(Version version) {
		Set<Version> versions = new HashSet<>();
		for(Version v : values()) {
			if(v == UNKNOWN) continue;
			if(v.getInteger() > version.getInteger()) versions.add(v);
		}
		return Collections.unmodifiableSet(versions);
	}

	public static Set<Version> olderThan(Version version) {
		Set<Version> versions = new HashSet<>();
		for(Version v : values()) {
			if(v == UNKNOWN) continue;
			if(v.getInteger() < version.getInteger()) versions.add(v);
		}
		return Collections.unmodifiableSet(versions);
	}

	public static Version fromString(String input) {
		String tmp = input.replaceAll("[^0-9+_0-9+]", "");

		if (tmp.startsWith("1_10")) return v1_10;
		if (tmp.startsWith("1_9")) return v1_9;
		if (tmp.startsWith("1_8")) return v1_8;
		if (tmp.startsWith("1_7")) return v1_7;
		if (tmp.startsWith("1_6")) return v1_6;
		if (tmp.startsWith("1_5")) return v1_5;
		if (tmp.startsWith("1_4")) return v1_4;
		if (tmp.startsWith("1_3")) return v1_3;
		if (tmp.startsWith("1_2")) return v1_2;
		if (tmp.startsWith("1_1")) return v1_1;
		if (tmp.startsWith("1_0")) return v1_0;

		return Version.UNKNOWN;
	}
}