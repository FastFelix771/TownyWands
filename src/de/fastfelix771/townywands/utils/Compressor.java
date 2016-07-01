package de.fastfelix771.townywands.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Compressor {

	@Getter
	private static final Compressor instance = new Compressor();


	public byte[] compress(byte[] input) {
		if (isCompressed(input)) return input;
		if ((input == null) || (input.length < 32)) return input;
		try (ByteArrayOutputStream compressed = new ByteArrayOutputStream(); GZIPOutputStream gzip = new GZIPOutputStream(compressed) {
			{
				this.def.setLevel(Deflater.BEST_COMPRESSION);
			}
		}) {
			gzip.write(input);
			return compressed.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isCompressed(byte[] input) {
		if ((input == null) || (input.length < 2)) return false;
		return (input[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (input[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
	}

	public byte[] decompress(byte[] input) {
		if (!isCompressed(input)) return input;
		try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(input)); ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {

			int i;
			while ((i = gzip.read()) != -1) {
				bytes.write(i);
			}

			return bytes.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}