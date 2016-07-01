package de.fastfelix771.townywands.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Serializer {

	@Getter
	private static final Serializer instance = new Serializer();


	public byte[] serialize(Serializable input) {
		if(input == null) return null;

		try(ByteArrayOutputStream bytes = new ByteArrayOutputStream(); ObjectOutputStream out = new ObjectOutputStream(bytes)) {
			out.writeObject(input);
			return bytes.toByteArray();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Serializable deserialize(byte[] input) {
		if(input == null) return null;

		try(ByteArrayInputStream bytes = new ByteArrayInputStream(input); ObjectInputStream in = new ObjectInputStream(bytes)) {
			return ((Serializable) in.readObject());
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}