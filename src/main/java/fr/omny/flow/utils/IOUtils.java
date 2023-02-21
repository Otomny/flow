package fr.omny.flow.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOUtils {

	private IOUtils() {
	}

	/**
	 * Write a varint to the buffer
	 * 
	 * @param buffer the buffer to write to
	 * @param value  the int value to write to intvar
	 * @throws IOException
	 */
	public static void writeVarInt(OutputStream buffer, int value) throws IOException {
		while ((value & -128) != 0) {
			buffer.write(value & 127 | 128);
			value >>>= 7;
		}
		buffer.write(value);
	}

	/**
	 * Write a varint to the buffer
	 * 
	 * @param buffer the buffer to write to
	 * @param value  the short value to write to intvar
	 * @throws IOException
	 */
	public static void writeVarInt(OutputStream buffer, short value) throws IOException {
		while ((value & -128) != 0) {
			buffer.write(value & 127 | 128);
			value >>>= 7;
		}
		buffer.write(value);
	}

	/**
	 * Reads a varint from the given InputStream and returns the decoded value
	 * as an int.
	 *
	 * @param inputStream the InputStream to read from
	 */
	public static int readVarInt(InputStream inputStream) throws IOException {
		int result = 0;
		int shift = 0;
		int b;
		do {
			if (shift >= 32) {
				// Out of range
				throw new IndexOutOfBoundsException("varint too long");
			}
			// Get 7 bits from next byte
			b = inputStream.read();
			result |= (b & 0x7F) << shift;
			shift += 7;
		} while ((b & 0x80) != 0);
		return result;
	}

	/**
	 * Reads a varint from the given InputStream and returns the decoded value
	 * as an int.
	 *
	 * @param inputStream the InputStream to read from
	 */
	public static short readVarShort(InputStream inputStream) throws IOException {
		short result = 0;
		int shift = 0;
		int b;
		do {
			if (shift >= 16) {
				// Out of range
				throw new IndexOutOfBoundsException("varshort too long");
			}
			// Get 7 bits from next byte
			b = inputStream.read();
			result |= (b & 0x7F) << shift;
			shift += 7;
		} while ((b & 0x80) != 0);
		return result;
	}

}
