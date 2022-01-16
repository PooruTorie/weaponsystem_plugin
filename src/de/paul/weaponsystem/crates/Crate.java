package de.paul.weaponsystem.crates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.dyroxplays.revieve.objects.DeathPlayer;
import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.config.CrateConfig;
import de.paul.weaponsystem.config.CrateConfig.CratePos;
import de.paul.weaponsystem.config.CrateConfig.CratePos.ItemType;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.WeaponItem;
import de.paul.weaponsystem.weapon.muni.Muni;

public class Crate implements Listener {

	public static ItemStack none = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
	public static HashMap<Location, Crate> placedCrates = new HashMap<>();
	public static HashMap<Villager, Crate> crateEnitys = new HashMap<>();
	public static HashMap<UUID, Inventory> invs = new HashMap<>();
	public static HashMap<UUID, Crate> open = new HashMap<>();
	
	public static void save() {
		Config weapons = WeaponSystem.loadConfig("data");
		JSONArray ws = (JSONArray) weapons.get("crates");
		for (Location loc : placedCrates.keySet()) {
			Config c = new Config(new JSONObject());
			Crate crate = placedCrates.get(loc);
			c.setLocation("loc", loc);
			c.set("crateName", crate.getName());
			ws.add(c.toJSON());
			
			crate.getV().remove();
		}
		weapons.set("crates", ws);
	}
	
	public static void load() {
		Config weapons = WeaponSystem.loadConfig("data");
		JSONArray ws = (JSONArray) weapons.get("crates");
		for (Object o : ws) {
			Config c = new Config((JSONObject) o);
			Location loc = c.getLocation("loc");
			String name = (String) c.get("crateName");
			Crate.getCrateByName(name).place(loc);
		}
		ws.clear();
		weapons.set("crates", ws);
		
		ItemMeta m = none.getItemMeta();
		m.setDisplayName("§4");
		none.setItemMeta(m);
		
		Bukkit.getPluginManager().registerEvents(new CrateEventListener(), WeaponSystem.plugin);
	}
	
	private String name;
	private String invName;
	private String permission;
	private int size;
	private Villager v;
	private ArrayList<CratePos> items;
	
	public Crate(String name) {
		this.name = name;
	}
	
	public Crate(CrateConfig config) {
		name = config.getName();
		invName = config.getInvName();
		permission = config.getPermission();
		size = config.getInventorySize();
		items = config.getItems();
	}
	
	public String getName() {
		return name;
	}
	
	public String getInvName() {
		return invName;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public int getInventorySize() {
		return size;
	}
	
	public ArrayList<CratePos> getItems() {
		return items;
	}
	
	public Villager getV() {
		return v;
	}
	
	public Object place(Location loc) {
		v = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
		v.setAdult();
		v.setAI(false);
		v.setAgeLock(true);
		v.setCollidable(false);
		v.setProfession(Profession.BUTCHER);
		v.setCanPickupItems(false);
		v.setSilent(true);
		v.setCustomName("§8• "+getInvName());
		
		crateEnitys.put(v, this);
		placedCrates.put(loc, this);
		return this;
	}
	
	public void openInv(Player p) {
		if (!DeathPlayer.isDead(p)) {
			if (p.hasPermission(permission)) {
				Inventory inv = Bukkit.createInventory(p, getInventorySize(), invName);
				
				for (int i = 0; i < inv.getSize(); i++) {
					inv.setItem(i, none);
				}
				
				for (CratePos pos : items) {
					if (pos.getItemType() == ItemType.weapon) {
						Weapon weapon = Weapon.getWeaponByName(pos.getItemName());
						if (weapon != null) {
							inv.setItem(pos.getSlot(), weapon.toItemStack(false));
						} else {
							p.sendMessage("§cCan't find Weapon: "+pos.getItemName());
						}
					}
					if (pos.getItemType() == ItemType.muni) {
						Muni muni = Muni.getMuniByName(pos.getItemName());
						if (muni != null) {
							inv.setItem(pos.getSlot(), muni.toItemStack(false));
						} else {
							p.sendMessage("§cCan't find Muni: "+pos.getItemName());
						}
					}
				}
				
				WeaponSystem.playSound(p.getLocation(), "minecraft:block.chest.open", 6, 1);
				p.openInventory(inv);
				invs.put(p.getUniqueId(), inv);
				open.put(p.getUniqueId(), this);
			} else {
				p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
			}
		}
	}
	
	public boolean playerHasWeapon(Player p, Weapon w) {
		for (ItemStack item : p.getInventory()) {
			if (item != null) {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasLocalizedName()) {
						if (item.getItemMeta().getLocalizedName().contains(w.getName())) {
							if (item.getItemMeta().getLocalizedName().contains(getName())) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public void removeWeaopons(Player p) {
		for (CratePos pos : items) {
			if (pos.getItemType() == ItemType.weapon) {
				Weapon weapon = Weapon.getWeaponByName(pos.getItemName());
				if (weapon != null) {
					if (playerHasWeapon(p, weapon)) {
						int i = 0;
						for (ItemStack item : p.getInventory()) {
							if (item != null) {
								if (item.hasItemMeta()) {
									if (item.getItemMeta().hasLocalizedName()) {
										if (item.getItemMeta().getLocalizedName().contains(weapon.getName())) {
											if (item.getItemMeta().getLocalizedName().contains(getName())) {
												int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
												WeaponItem.items.get(id).remove(p);
												p.getInventory().setItem(i, new ItemStack(Material.AIR));
											}
										}
									}
								}
							}
							i++;
						}
					}
				}
			}
		}
	}
	
	public void removeWeaopon(Player p, Weapon weapon) {
		if (weapon != null) {
			if (playerHasWeapon(p, weapon)) {
				int i = 0;
				for (ItemStack item : p.getInventory()) {
					if (item != null) {
						if (item.hasItemMeta()) {
							if (item.getItemMeta().hasLocalizedName()) {
								if (item.getItemMeta().getLocalizedName().contains(weapon.getName())) {
									if (item.getItemMeta().getLocalizedName().contains(getName())) {
										int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
										WeaponItem.items.get(id).remove(p);
										p.getInventory().setItem(i, new ItemStack(Material.AIR));
									}
								}
							}
						}
					}
					i++;
				}
			}
		}
	}
	
	public static HashMap<Integer, Crate> crates = new HashMap<>();
	private static int index = 0;

	public static void register(Crate crate) {
		crates.put(index, crate);
		index++;
	}
	
	public static Crate getCrateByIndex(int index) {
		return crates.get(index);
	}
	
	public static Crate getCrateByName(String searchName) {
		for (Crate crate : crates.values()) {
			if (crate.getName().equalsIgnoreCase(searchName)) {
				return crate;
			}
		}
		return null;
	}
	
	public static ArrayList<String> getAllNames() {
		ArrayList<String> a = new ArrayList<>();
		for (Crate crate : crates.values()) {
			a.add(crate.getName());
		}
		return a;
	}

}
