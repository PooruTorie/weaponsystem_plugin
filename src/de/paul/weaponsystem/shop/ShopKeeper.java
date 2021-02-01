package de.paul.weaponsystem.shop;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Career;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.muni.Muni;

public class ShopKeeper {
	
	public static HashMap<Entity, ShopKeeper> shopKepperEnitys = new HashMap<>();
	public static HashMap<Location, ShopKeeper> shopKepper = new HashMap<>();
	public static ItemStack none = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
	
	public static void save() {
		Config weapons = WeaponSystem.loadConfig("data");
		JSONArray ws = (JSONArray) weapons.get("shopkeeper");
		for (Location loc : shopKepper.keySet()) {
			Config c = new Config(new JSONObject());
			ShopKeeper s = shopKepper.get(loc);
			c.setLocation("loc", loc);
			c.set("type", s.getType());
			ws.add(c.toJSON());
			
			s.getEntity().remove();
		}
		weapons.set("shopkeeper", ws);
	}
	
	public static void load() {
		Config weapons = WeaponSystem.loadConfig("data");
		JSONArray ws = (JSONArray) weapons.get("shopkeeper");
		for (Object o : ws) {
			Config c = new Config((JSONObject) o);
			Location loc = c.getLocation("loc");
			String name = (String) c.get("type");
			shopKepper.put(loc, new ShopKeeper(loc, ShopType.valueOf(name)));
		}
		ws.clear();
		weapons.set("shopkeeper", ws);
		
		ItemMeta m = none.getItemMeta();
		m.setDisplayName("§4");
		none.setItemMeta(m);
		
		Bukkit.getPluginManager().registerEvents(new ShopKeeperEventListener(), WeaponSystem.plugin);
	}
	
	private ShopType type;

	private Villager v;
	
	public ShopKeeper(Location loc, ShopType type) {
		this.type = type;
		
		v = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
		v.setAdult();
		v.setAI(false);
		v.setAgeLock(true);
		v.setCollidable(false);
		if (type == ShopType.muni) {
			v.setProfession(Profession.LIBRARIAN);
		} else {
			v.setProfession(Profession.BLACKSMITH);
		}
		v.setCanPickupItems(false);
		v.setSilent(true);
		v.setCustomName(type.getName());
		
		shopKepperEnitys.put(v, this);
		shopKepper.put(loc, this);
	}
	
	public Entity getEntity() {
		return v;
	}
	
	public ShopType getType() {
		return type;
	}
	
	public static HashMap<UUID, Inventory> invs = new HashMap<>();
	
	public void openInv(Player p) {
		Inventory inv = Bukkit.createInventory(p, 9*6, type.getName()+" §3| §e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(WeaponSystem.economy.getBalance(p))+"$");
		
		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, none);
		}
		
		int i = 11;
		if (type == ShopType.weapon) {
			for (Weapon w : Weapon.getAll()) {
				inv.setItem(i, w.toItemStack(true));
				i++;
				if (i == 16) {
					i+=2;
				}
				if (i == 36) {
					i+=2;
				}
			}
		} else if (type == ShopType.muni) {
			for (Muni m : Muni.getAll()) {
				inv.setItem(i, m.toItemStack(true));
				i++;
				if (i == 16) {
					i+=2;
				}
				if (i == 36) {
					i+=2;
				}
			}
		}
		for (; i <= 42;) {
			inv.setItem(i, null);
			i++;
			if (i == 16) {
				i+=2;
			}
			if (i == 36) {
				i+=2;
			}
		}
		
		p.openInventory(inv);
		invs.put(p.getUniqueId(), inv);
	}
	
	public enum ShopType {
		weapon("§6Waffen Shop"), muni("§6Munitions Shop");

		private String name;

		private ShopType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}

		public static List<String> names() {
			ArrayList<String> l = new ArrayList<>();
			for (ShopType t : values()) {
				l.add(t.name());
			}
			return l;
		}
	}
}	
