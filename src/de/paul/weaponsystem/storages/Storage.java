package de.paul.weaponsystem.storages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.crates.Crate;
import de.paul.weaponsystem.weapon.Weapon;

public class Storage extends Crate {
	
	public static HashMap<Entity, Storage> storagesEnitys = new HashMap<>();
	public static HashMap<Location, Storage> storages = new HashMap<>();
	public static ItemStack none = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
	
	public static void save() {
		Config weapons = WeaponSystem.loadConfig("data");
		JSONArray ws = (JSONArray) weapons.get("storages");
		for (Location loc : storages.keySet()) {
			Config c = new Config(new JSONObject());
			Storage s = storages.get(loc);
			c.setLocation("loc", loc);
			c.set("type", s.getType());
			ws.add(c.toJSON());
			
			s.getEntity().remove();
		}
		weapons.set("storages", ws);
	}
	
	public static void load() {
		Config weapons = WeaponSystem.loadConfig("data");
		JSONArray ws = (JSONArray) weapons.get("storages");
		for (Object o : ws) {
			Config c = new Config((JSONObject) o);
			Location loc = c.getLocation("loc");
			String name = (String) c.get("type");
			storages.put(loc, new Storage(loc, StorageType.valueOf(name)));
		}
		ws.clear();
		weapons.set("storages", ws);
		
		ItemMeta m = none.getItemMeta();
		m.setDisplayName("§4");
		none.setItemMeta(m);
		
		Bukkit.getPluginManager().registerEvents(new StorageEventListener(), WeaponSystem.plugin);
	}
	
	private StorageType type;

	private Witch v;
	
	public Storage(Location loc, StorageType type) {
		super("storage"+type.name);
		this.type = type;
		
		v = (Witch) loc.getWorld().spawnEntity(loc, EntityType.WITCH);
		v.setAI(false);
		v.setCollidable(false);
		v.setCanPickupItems(false);
		v.setSilent(true);
		v.setCustomName(type.getName());
		
		storagesEnitys.put(v, this);
		storages.put(loc, this);
	}
	
	public Entity getEntity() {
		return v;
	}
	
	public StorageType getType() {
		return type;
	}
	
	public static HashMap<UUID, Inventory> invs = new HashMap<>();
	public static HashMap<UUID, Storage> invStorages = new HashMap<>();
	
	public void openInv(Player p) {
		Inventory inv = Bukkit.createInventory(p, 9*6, type.getName());
		
		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, none);
		}
		
		int i = 11;
		if (type == StorageType.weapon) {
			for (Weapon w : PlayerWeapons.getForPlayer(p).getBuyedWeapons()) {
				inv.setItem(i, w.toItemStack(false));
				i++;
				if (i == 16) {
					i+=2;
				}
			}
		} else if (type == StorageType.muni) {
			
		}
		for (; i <= 42;) {
			inv.setItem(i, null);
			i++;
			if (i == 36) {
				i+=2;
			}
		}
		
		p.openInventory(inv);
		invs.put(p.getUniqueId(), inv);
		invStorages.put(p.getUniqueId(), this);
	}
	
	public enum StorageType {
		weapon("§6Waffen Schrank"), muni("§6Munitions Schrank");

		private String name;

		private StorageType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		public static List<String> names() {
			ArrayList<String> l = new ArrayList<>();
			for (StorageType t : values()) {
				l.add(t.name());
			}
			return l;
		}
	}
	
}
