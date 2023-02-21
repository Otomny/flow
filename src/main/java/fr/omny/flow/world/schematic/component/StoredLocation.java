package fr.omny.flow.world.schematic.component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoredLocation {

	/**
	 * 
	 * @param location
	 * @return
	 */
	public static StoredLocation fromWorld(Location location) {
		return new StoredLocation(location.getX(), location.getY(), location.getZ());
	}

	/**
	 * 
	 * @param inputStream
	 * @return
	 */
	public static StoredLocation fromIO(DataInputStream inputStream) {
		try {
			double x = inputStream.readDouble();
			double y = inputStream.readDouble();
			double z = inputStream.readDouble();
			return new StoredLocation(x, y, z);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private double x;
	private double y;
	private double z;

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	private StoredLocation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void storeIO(DataOutputStream outputStream) {
		try {
			outputStream.writeDouble(x);
			outputStream.writeDouble(y);
			outputStream.writeDouble(z);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
