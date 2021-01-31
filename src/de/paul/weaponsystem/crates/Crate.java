package de.paul.weaponsystem.crates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.config.CrateConfig;
import de.paul.weaponsystem.config.CrateConfig.CratePos;
import de.paul.weaponsystem.config.CrateConfig.CratePos.ItemType;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.WeaponItem;
import de.paul.weaponsystem.weapon.muni.Muni;

public class Crate implements Listener {

	private static ItemStack none = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
	private static HashMap<Location, Crate> placedCrates = new HashMap<>();
	
	public static void save() {
		Config weapons = WeaponSystem.loadConfig("data");
		JSONArray ws = (JSONArray) weapons.get("crates");
		for (Location loc : placedCrates.keySet()) {
			Config c = new Config(new JSONObject());
			Crate crate = placedCrates.get(loc);
			c.setLocation("loc", loc);
			c.set("crateName", crate.getName());
			ws.add(c.toJSON());
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
			placedCrates.put(loc, Crate.getCrateByName(name));
		}
		ws.clear();
		weapons.set("crates", ws);
		
		ItemMeta m = none.getItemMeta();
		m.setDisplayName("§4");
		none.setItemMeta(m);
	}
	
	private String name;
	private String invName;
	private String permission;
	private int blockMat;
	private ArrayList<CratePos> items;
	
	public Crate(CrateConfig config) {
		name = config.getName();
		invName = config.getInvName();
		permission = config.getPermission();
		blockMat = config.getBlockMat();
		items = config.getItems();
		
		Bukkit.getPluginManager().registerEvents(this, WeaponSystem.plugin);
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
	
	public int getBlockMat() {
		return blockMat;
	}
	
	public ArrayList<CratePos> getItems() {
		return items;
	}
	
	public void place(Location loc) {
		loc = loc.getBlock().getLocation();
		
		loc.getBlock().setTypeId(blockMat);
		
		placedCrates.put(loc, this);
	}
	
	public void openInv(Player p) {
		if (p.hasPermission(permission)) {
			Inventory inv = Bukkit.createInventory(p, 9*3, invName);
			
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
		} else {
			p.sendMessage(WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
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
												WeaponItem.items.remove(id);
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
	
	public static HashMap<UUID, Inventory> invs = new HashMap<>();
	
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getClickedInventory() != null) {
			if (invs.containsKey(p.getUniqueId())) {
				if (invs.get(p.getUniqueId()).equals(e.getClickedInventory())) {
					if (e.getCurrentItem() != null) {
						if (e.getCurrentItem().equals(none)) {
							e.setCancelled(true);
						} else {
							e.setCancelled(true);
							ItemStack item = e.getCurrentItem();
							if (item != null) {
								Weapon w = Weapon.getWeaponByName(item.getItemMeta().getLocalizedName().split("[_]")[0]);
								if (w != null) {
									if (playerHasWeapon(p, w)) {
										return;
									} else {
										w.give(p, this);
										return;
									}
								}
								
								p.getInventory().addItem(item);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	private void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (invs.containsKey(p.getUniqueId())) {
			invs.remove(p.getUniqueId());
		}
	}
	
	@EventHandler
	private void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getHand() == EquipmentSlot.HAND) {
				if (e.getClickedBlock() != null) {
					Block b = e.getClickedBlock();
					if (placedCrates.containsKey(b.getLocation())) {
						placedCrates.get(b.getLocation()).openInv(p);
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	private void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if (placedCrates.containsKey(b.getLocation())) {
			if (p.hasPermission(permission)) {
				b.setType(Material.AIR);
				placedCrates.remove(b.getLocation());
			} else {
				p.sendMessage(WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
				e.setCancelled(true);
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
