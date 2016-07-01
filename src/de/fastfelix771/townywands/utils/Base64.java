package de.fastfelix771.townywands.utils;

import javax.xml.bind.DatatypeConverter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Base64 {

	@Getter
	private static final Base64 instance = new Base64();


	public String print(byte[] input) {
		if(input == null || input.length <= 0) return null;
		return DatatypeConverter.printBase64Binary(input);
	}

	public byte[] parse(String input) {
		if(input == null || input.isEmpty()) return null;
		return DatatypeConverter.parseBase64Binary(input);
	}

}