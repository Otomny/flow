package fr.omny.flow.world;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Item builder class
 */
public class ItemBuilder {

	private ItemStack item;

	/**
	 * Create a new item builder
	 * 
	 * @param material
	 */
	public ItemBuilder(Material material) {
		this(1, material);
	}

	/**
	 * Create a new item builder
	 * 
	 * @param amount
	 * @param material
	 */
	public ItemBuilder(int amount, Material material) {
		this.item = new ItemStack(material, amount);
	}

	/**
	 * Set name to item
	 * 
	 * @param name
	 * @return
	 */
	public ItemBuilder name(String name) {
		return applyMeta(meta -> meta.setDisplayName(name));
	}

	/**
	 * Apply meta to item
	 * 
	 * @param applier
	 * @return
	 */
	public ItemBuilder applyMeta(Consumer<ItemMeta> applier) {
		var meta = this.item.getItemMeta();
		applier.accept(meta);
		this.item.setItemMeta(meta);
		return this;
	}

	/**
	 * Set lore of item
	 * 
	 * @param texts
	 * @return
	 */
	public ItemBuilder description(List<String> texts) {
		return applyMeta(meta -> {
			if (!meta.hasLore()) {
				meta.setLore(new ArrayList<>());
			}
			meta.getLore().addAll(texts);
		});
	}

	/**
	 * Set lore of item only if condition is true
	 * 
	 * @param condition
	 * @param texts
	 * @return
	 */
	public ItemBuilder description(boolean condition, List<String> texts) {
		return condition ? description(texts) : this;
	}

	/**
	 * Set lore of item
	 * 
	 * @param strings
	 * @return
	 */
	public ItemBuilder description(String... strings) {
		return description(List.of(strings));
	}

	/**
	 * Set lore of item only if condition is true
	 * 
	 * @param condition
	 * @param strings
	 * @return
	 */
	public ItemBuilder description(boolean condition, String... strings) {
		return condition ? description(List.of(strings)) : this;
	}

	/**
	 * Apply enchantment to item
	 * 
	 * @param enchantment
	 * @param level
	 * @return
	 */
	public ItemBuilder applyEnchantment(Enchantment enchantment, int level) {
		this.item.addUnsafeEnchantment(enchantment, level);
		return this;
	}

	/**
	 * @return A copy of the item stack
	 */
	public ItemStack buildClone() {
		return this.item.clone();
	}

	/**
	 * @return The item stack
	 */
	public ItemStack build() {
		return this.item;
	}

}
