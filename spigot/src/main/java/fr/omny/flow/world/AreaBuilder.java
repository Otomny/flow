package fr.omny.flow.world;

import org.bukkit.Location;

import fr.omny.flow.utils.WorldUtils;

public class AreaBuilder {

	private Location start;
	private Location end;

	/**
	 * @param start
	 * @param end
	 */
	public AreaBuilder(Location start, Location end) {

		this.start = WorldUtils.max(start, end);
		this.end = WorldUtils.min(start, end);
	}

	public void expandX(double x) {
		expand(x, 0, 0);
	}

	public void expandY(double y) {
		expand(0, y, 0);
	}

	public void expandZ(double z) {
		expand(0, 0, z);
	}

	public void expand(double scalar) {
		expand(scalar, scalar, scalar);
	}

	public void expand(double x, double y, double z) {
		start.add(x, y, z);
		end.add(-x, -y, -z);
	}

	public Area build() {
		return new Area(start, end);
	}

}
