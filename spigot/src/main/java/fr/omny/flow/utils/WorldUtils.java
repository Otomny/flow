package fr.omny.flow.utils;

import org.bukkit.Location;

public final class WorldUtils {

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Location min(Location a, Location b) {
		if (a.getWorld() != b.getWorld())
			throw new IllegalArgumentException("Les mondes sont différent");
		return new Location(a.getWorld(), Math.min(a.x(), b.x()), Math.min(a.z(), b.z()), Math.min(a.y(), b.y()));
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Location max(Location a, Location b) {
		if (a.getWorld() != b.getWorld())
			throw new IllegalArgumentException("Les mondes sont différent");
		return new Location(a.getWorld(), Math.max(a.x(), b.x()), Math.max(a.z(), b.z()), Math.max(a.y(), b.y()));
	}

}
