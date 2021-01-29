package de.paul.weaponsystem.weapon;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.weapon.Weapon.WeaponType;
import de.paul.weaponsystem.weapon.muni.Muni;

public class WeaponItem extends ItemStack implements Listener {
	
	private static HashMap<Integer, WeaponItem> items = new HashMap<>();
	
	public static void save() {
		Config weapons = WeaponSystem.loadConfig("playerWeapons");
		JSONArray ws = (JSONArray) weapons.get("weapons");
		for (int i : items.keySet()) {
			Config c = new Config(new JSONObject());
			WeaponItem item = items.get(i);
			c.set("id", (long) i);
			c.set("magazin", (long) item.getMagazin());
			c.set("weaponName", item.getWeapon().getName());
			ws.add(c.toJSON());
		}
		weapons.set("weapons", ws);
	}
	
	public static void load() {
		Config weapons = WeaponSystem.loadConfig("playerWeapons");
		JSONArray ws = (JSONArray) weapons.get("weapons");
		for (Object o : ws) {
			Config c = new Config((JSONObject) o);
			int i = ((Long) c.get("id")).intValue();
			int magazin = ((Long) c.get("magazin")).intValue();
			String name = (String) c.get("weaponName");
			Weapon weapon = Weapon.getWeaponByName(name);
			items.put(i, new WeaponItem(weapon, i, magazin));
		}
		ws.clear();
		weapons.set("weapons", ws);
	}
	
	private Weapon weapon;
	private int magazin = 0;

	public WeaponItem() {}
	
	public WeaponItem(Weapon weapon) {
		super(weapon.getItemID());
		this.weapon = weapon;
		
		int id = (int) (Math.random()*1000);
		
		ItemMeta m = getItemMeta();
		m.setDisplayName(weapon.getItemName());
		m.setLore(weapon.getItemLore());
		m.setUnbreakable(true);
		m.setLocalizedName(weapon.getName()+"_"+id);
		setItemMeta(m);
		
		if (weapon.getType() == WeaponType.gun) {
			magazin = weapon.getGunMuniCapacity();
		}
		
		items.put(id, this);
	}
	
	public WeaponItem(Weapon weapon, int id, int magazin) {
		super(weapon.getItemID());
		this.weapon = weapon;
		
		ItemMeta m = getItemMeta();
		m.setDisplayName(weapon.getItemName());
		m.setLore(weapon.getItemLore());
		m.setUnbreakable(true);
		m.setLocalizedName(weapon.getName()+"_"+id);
		setItemMeta(m);
		
		this.magazin = magazin;
	}

	public Weapon getWeapon() {
		return weapon;
	}
	
	public int getMagazin() {
		return magazin;
	}
	
	private void gunReleod(Player p) {
		if (magazin < weapon.getGunMuniCapacity()) {
			Muni muni = Muni.getWeaponById(weapon.getGunMuniId());
			int i = muni.getMuniItems(p.getInventory());
			if (i > 0) {
				for (Player all : Bukkit.getOnlinePlayers()) {
					all.playSound(p.getLocation(), "minecraft:weapon.reload", 50, (float) (1f+Math.random()));
				}
				magazin = weapon.getGunMuniCapacity();
			}
		} else {
			p.sendMessage(WeaponSystem.loadConfig("config", "messages").getChatColorString("munifull"));
		}
	}
	
	private void gunShot(Player p) {
		if (magazin > 0) {
			for (Player all : Bukkit.getOnlinePlayers()) {
				all.playSound(p.getLocation(), "minecraft:weapon.blast1", 50, (float) (1f+Math.random()));
			}
			Snowball bullet = p.launchProjectile(Snowball.class);
			bullet.setGravity(false);
			bullet.setCustomName(weapon.getName()+"_"+weapon.getGunDamage());
			magazin--;
		} else {
			p.playSound(p.getLocation(), "minecraft:weapon.empty", 50, (float) (1f+Math.random()));
		}
	}
	
	@EventHandler
	private void onShot(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (item != null) {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasLocalizedName()) {
						int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
						if (items.containsKey(id)) {
							WeaponItem itemWeapon = items.get(id);
							if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
								if (p.getCooldown(item.getType()) == 0) {
									p.setCooldown(item.getType(), itemWeapon.getWeapon().getCooldown()*20);
									itemWeapon.gunShot(p);
								}
								e.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	private void onDrop(PlayerDropItemEvent e) {
		ItemStack item = e.getItemDrop().getItemStack();
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasLocalizedName()) {
				int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
				if (items.containsKey(id)) {
					WeaponItem itemWeapon = items.get(id);
					if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
						itemWeapon.gunReleod(e.getPlayer());
					}
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	private void onHandSwitch(PlayerSwapHandItemsEvent e) {
		ItemStack item = e.getOffHandItem();
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasLocalizedName()) {
				int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
				if (items.containsKey(id)) {
					WeaponItem itemWeapon = items.get(id);
					if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
						itemWeapon.gunReleod(e.getPlayer());
					}
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	private void onHit(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player damager = (Player) e.getDamager();
			ItemStack item = damager.getItemInHand();
			if (item != null) {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasLocalizedName()) {
						int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
						if (items.containsKey(id)) {
							WeaponItem itemWeapon = items.get(id);
							if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
								e.setDamage(0);
							} else {
								if (damager.getCooldown(item.getType()) == 0) {
									e.setDamage(itemWeapon.getWeapon().getMeleeDamage());
									damager.setCooldown(item.getType(), itemWeapon.getWeapon().getCooldown()*20);
								}
							}
						}
					}
				}
			}
		}
	}
}
