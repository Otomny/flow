package fr.omny.flow.world.schematic.component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import fr.omny.flow.api.utils.IOUtils;
import lombok.Getter;

@Getter
public class StoredChest {

  public static StoredChest fromTile(Chest chest) {
    Inventory inv = chest.getBlockInventory();
    StoredLocation location = StoredLocation.fromWorld(chest.getLocation());
    StoredChest storedChest = new StoredChest();
    storedChest.size = inv.getSize();
    storedChest.location = location;
    storedChest.content = inv.getContents();
    return storedChest;
  }

  public static StoredChest fromTile(Chest chest, Location offset) {
    Inventory inv = chest.getBlockInventory();
    StoredLocation location =
        StoredLocation.fromWorld(chest.getLocation().clone().subtract(offset));
    StoredChest storedChest = new StoredChest();
    storedChest.size = inv.getSize();
    storedChest.location = location;
    storedChest.content = inv.getContents();
    return storedChest;
  }

  public static StoredChest fromTile(BlockInventoryHolder holder, int x, int y,
                                     int z) {
    Inventory inv = holder.getInventory();
    StoredLocation location = StoredLocation.fromWorld(x, y, z);
    StoredChest storedChest = new StoredChest();
    storedChest.size = inv.getSize();
    storedChest.location = location;
    storedChest.content = inv.getContents();
    return storedChest;
  }

  public static StoredChest fromTile(Chest chest, int x, int y, int z) {
    Inventory inv = chest.getBlockInventory();
    StoredLocation location = StoredLocation.fromWorld(x, y, z);
    StoredChest storedChest = new StoredChest();
    storedChest.size = inv.getSize();
    storedChest.location = location;
    storedChest.content = inv.getContents();
    return storedChest;
  }

  public static StoredChest fromIO(DataInputStream inputStream) {
    try {
      StoredLocation location = StoredLocation.fromIO(inputStream);
      int size = IOUtils.readVarInt(inputStream);
      BukkitObjectInputStream dataInput =
          new BukkitObjectInputStream(inputStream);
      ItemStack[] content = new ItemStack[size];
      for (int i = 0; i < size; i++) {
        var item = (ItemStack)dataInput.readObject();
        content[i] = item;
      }
      var storedChest = new StoredChest();
      storedChest.content = content;
      storedChest.size = size;
      storedChest.location = location;
      return storedChest;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private StoredLocation location;
  private int size;
  private ItemStack[] content;

  private StoredChest() {}

  public void storeIO(DataOutputStream outputStream) {
    try {
      this.location.storeIO(outputStream);
      IOUtils.writeVarInt(outputStream, size);
      BukkitObjectOutputStream dataOutput =
          new BukkitObjectOutputStream(outputStream);
      for (int i = 0; i < size; i++) {
        dataOutput.writeObject(content[i]);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
