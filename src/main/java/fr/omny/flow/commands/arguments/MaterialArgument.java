package fr.omny.flow.commands.arguments;


import org.bukkit.Material;

public class MaterialArgument extends EnumArgument<Material> {

	public MaterialArgument(boolean optional) {
		super(Material.class, optional);
	}

}
