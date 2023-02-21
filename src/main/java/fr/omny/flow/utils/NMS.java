package fr.omny.flow.utils;

import org.bukkit.Bukkit;

/**
 * 
 */
public class NMS {

	// net.minecraft.server.v1_13_R1.TileEntity.load(NBTTagCompound)
	// net.minecraft.server.v1_13_R1.TileEntity.save(NBTTagCompound)

	private String version;
	private String craftBukkitBase;
	private String nmsBase;

	public NMS() {
		this.version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		this.craftBukkitBase = "org.bukkit.craftbukkit." + version + ".";
		this.nmsBase = "net.minecraft.server." + version + ".";
		var nmsPackage = getClass()
			.getClassLoader()
			.getDefinedPackage("net.minecraft.server." + version);
		if(nmsPackage == null){
			this.nmsBase = "net.minecraft.server.";
		}
	}

	public Class<?> findNmsClass(String nmsClass) throws ClassNotFoundException {
		return Class.forName(this.nmsBase + nmsClass);
	}

	public Class<?> findCraftBukkit(String craftBukkitClass) throws ClassNotFoundException {
		return Class.forName(this.craftBukkitBase + craftBukkitClass);
	}

}
