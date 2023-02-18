package fr.omny.flow.player;


import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * 
 */
public class StoredPlayerInventory {

	private ItemStack[] armorContent = new ItemStack[4];
	private ItemStack[] inventoryContent = new ItemStack[36];
	private ItemStack[] enderChest = new ItemStack[27];

	/**
	 * 
	 */
	public StoredPlayerInventory() {}

	/**
	 * 
	 * @param player
	 */
	public void apply(Player player) {
		player.getInventory().setContents(this.inventoryContent);
		player.getInventory().setArmorContents(this.armorContent);
		player.getEnderChest().setContents(this.enderChest);
	}

	/**
	 * 
	 * @param player
	 */
	public void load(Player player) {
		this.inventoryContent = player.getInventory().getContents();
		this.armorContent = player.getInventory().getArmorContents();
		this.enderChest = player.getEnderChest().getContents();
	}

}
