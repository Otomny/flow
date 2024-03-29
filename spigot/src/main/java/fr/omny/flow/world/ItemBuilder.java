package fr.omny.flow.world;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;

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
	public ItemBuilder nameLegacy(String name) {
		return applyMeta(meta -> meta.displayName(Component.text(name)));
	}

	/**
	 * Set name to item
	 * 
	 * @param name
	 * @return
	 */
	public ItemBuilder name(Component name) {
		return applyMeta(meta -> meta.displayName(name));
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
	public ItemBuilder descriptionLegacy(List<String> texts) {
		return applyMeta(meta -> {
			List<Component> lores = new ArrayList<>();
			lores.addAll(texts.stream().map(Component::text).collect(Collectors.toList()));
			if (!meta.hasLore()) {
				meta.lore(texts.stream().map(Component::text).collect(Collectors.toList()));
			} else {
				meta.lore().addAll(lores);
			}

		});
	}

	/**
	 * Set lore of item only if condition is true
	 * 
	 * @param condition
	 * @param texts
	 * @return
	 */
	public ItemBuilder descriptionLegacy(boolean condition, List<String> texts) {
		return condition ? descriptionLegacy(texts) : this;
	}

	/**
	 * Set lore of item
	 * 
	 * @param strings
	 * @return
	 */
	public ItemBuilder descriptionLegacy(String... strings) {
		return descriptionLegacy(List.of(strings));
	}

	/**
	 * Set lore of item only if condition is true
	 * 
	 * @param condition
	 * @param strings
	 * @return
	 */
	public ItemBuilder descriptionLegacy(boolean condition, String... strings) {
		return condition ? descriptionLegacy(List.of(strings)) : this;
	}

	/**
	 * Set lore of item
	 * 
	 * @param texts
	 * @return
	 */
	public ItemBuilder description(List<Component> texts) {
		return applyMeta(meta -> {
			List<Component> lores = new ArrayList<>();
			lores.addAll(texts);
			if (!meta.hasLore()) {
				meta.lore(texts);
			} else {
				meta.lore().addAll(lores);
			}
		});
	}

	/**
	 * Set lore of item only if condition is true
	 * 
	 * @param condition
	 * @param texts
	 * @return
	 */
	public ItemBuilder description(boolean condition, List<Component> texts) {
		return condition ? description(texts) : this;
	}

	/**
	 * Set lore of item
	 * 
	 * @param strings
	 * @return
	 */
	public ItemBuilder description(Component... strings) {
		return description(List.of(strings));
	}

	/**
	 * Set lore of item only if condition is true
	 * 
	 * @param condition
	 * @param strings
	 * @return
	 */
	public ItemBuilder description(boolean condition, Component... strings) {
		return condition ? description(List.of(strings)) : this;
	}

	public ItemBuilder glow(boolean apply) {
		return applyMeta(itemMeta -> {
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
			itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
			itemMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
			if (apply) {
				itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
				itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
		});
	}

	public ItemBuilder glow() {
		return glow(true);
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
