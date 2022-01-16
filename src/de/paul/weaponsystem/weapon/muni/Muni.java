package de.paul.weaponsystem.weapon.muni;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.paul.weaponsystem.config.MuniConfig;
import de.paul.weaponsystem.weapon.consumable.Consumable;
import de.paul.weaponsystem.weapon.throwable.Throwable;

public class Muni {

	private int id;
	private String name;
	private String itemName;
	private int itemID;
	private int itemDamage;
	private Class<? extends MuniItem> itemClass = null;
	private int costs;
	
	public Muni(MuniConfig config) {
		id = config.getId();
		name = config.getName();
		itemName = config.getItemName();
		itemID = config.getItemID();
		itemDamage = config.getItemDamage();
		costs = config.getCosts();
	}
	
	public Muni(int id, String name, String itemName, int itemID, int itemDamage, int costs, Class<? extends MuniItem> itemClass) {
		this.id = id;
		this.name = name;
		this.itemName = itemName;
		this.itemID = itemID;
		this.itemDamage = itemDamage;
		this.itemClass = itemClass;
		this.costs = costs;
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
	
	public int getItemDamage() {
		return itemDamage;
	}
	
	public boolean hasItemClass() {
		return itemClass != null;
	}
	
	public int getCosts() {
		return costs;
	}
	
	public Class<? extends MuniItem> getItemClass() {
		return itemClass;
	}
	
	private static HashMap<Integer, Muni> muni = new HashMap<>();

	public static void register(Muni muni) {
		Muni.muni.put(muni.getId(), muni);
	}
	
	public static Muni getMuniById(int id) {
		return muni.get(id);
	}
	
	public static Muni getMuniByName(String searchName) {
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
						String name = item.getItemMeta().getLocalizedName().split("[_]")[0];
						if (name.equals(getName())) {
							count += item.getAmount();
						}
					}
				}
			}
		}
		return count;
	}

	public void give(Player p) {
		if (hasItemClass()) {
			try {
				Object item = itemClass.getConstructor(Muni.class).newInstance(this);
				p.getInventory().addItem((ItemStack) item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			MuniItem item = new MuniItem(this);
			p.getInventory().addItem(item);
		}
	}

	public void removeItem(PlayerInventory i) {
		for (ItemStack item : i) {
			if (item != null) {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasLocalizedName()) {
						int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
						if (MuniItem.getItems().containsKey(id)) {
							if (getId() == id) {
								item.setAmount(item.getAmount()-1);
								return;
							}
						}
					}
				}
			}
		}
	}

	public static ArrayList<String> getAllNames() {
		ArrayList<String> a = new ArrayList<>();
		for (Muni muni : muni.values()) {
			a.add(muni.getName());
		}
		return a;
	}
	
	public static List<Muni> getAll() {
		return new ArrayList(muni.values());
	}

	public void removeItem(PlayerInventory inv, int i) {
		for (ItemStack item : inv) {
			if (item != null) {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasLocalizedName()) {
						int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
						if (i == id) {
							item.setAmount(item.getAmount()-1);
							return;
						}
					}
				}
			}
		}
	}

	public ItemStack toItemStack(boolean costs) {
		if (costs) {
			if (hasItemClass()) {
				try {
					Object item = itemClass.getConstructor(Muni.class, int.class).newInstance(this, getCosts());
					return (ItemStack) item;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} else {
				MuniItem item = new MuniItem(this, getCosts());
				return item;
			}
		} else {
			if (hasItemClass()) {
				try {
					Object item = itemClass.getConstructor(Muni.class).newInstance(this);
					return (ItemStack) item;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} else {
				MuniItem item = new MuniItem(this);
				return item;
			}
		}
	}

	public Throwable getThrowable() {
		if (hasItemClass()) {
			try {
				Object item = itemClass.getConstructor(Muni.class).newInstance(this);
				return (Throwable) item;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public Consumable getConsumable() {
		if (hasItemClass()) {
			try {
				Object item = itemClass.getConstructor(Muni.class).newInstance(this);
				return (Consumable) item;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
