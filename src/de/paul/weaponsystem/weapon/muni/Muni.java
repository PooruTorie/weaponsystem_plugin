package de.paul.weaponsystem.weapon.muni;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.paul.weaponsystem.config.MuniConfig;

public class Muni {

	private int id;
	private String name;
	private String itemName;
	private int itemID;
	private ArrayList<String> itemLore = new ArrayList<>();
	
	public Muni(MuniConfig config) {
		id = config.getId();
		name = config.getName();
		itemName = config.getItemName();
		itemID = config.getItemID();
		itemLore = config.getItemLore();
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public ArrayList<String> getItemLore() {
		return itemLore;
	}
	
	private static HashMap<Integer, Muni> muni = new HashMap<>();

	public static void register(Muni muni) {
		Muni.muni.put(muni.getId(), muni);
	}
	
	public static Muni getWeaponById(int id) {
		return muni.get(id);
	}
	
	public static Muni getWeaponByName(String searchName) {
		for (Muni muni : muni.values()) {
			if (muni.getName().equalsIgnoreCase(searchName)) {
				return muni;
			}
		}
		return null;
	}

	public int getMuniItems(Inventory i) {
		int count = 0;
		for (ItemStack item : i) {
			if (item != null) {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasLocalizedName()) {
						int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
						if (MuniItem.getItems().containsKey(id)) {
							if (getId() == id) {
								count++;
							}
						}
					}
				}
			}
		}
		return count;
	}

	public void give(Player p) {
		MuniItem item = new MuniItem(this);
		p.getInventory().addItem(item);
	}

}
