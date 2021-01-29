package de.paul.weaponsystem.weapon;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.weapon.Weapon.WeaponType;
import de.paul.weaponsystem.weapon.muni.Muni;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

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
	
	public void showAmmo(Player p) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§b"+magazin+"§7/§b"+weapon.getGunMuniCapacity()));
	}
	
	private void gunReleod(ItemStack item, Player p) {
		Bukkit.getScheduler().runTaskLater(WeaponSystem.plugin, new Runnable() {
			
			private int task;

			@Override
			public void run() {
				if (magazin < weapon.getGunMuniCapacity()) {
					Muni muni = Muni.getWeaponById(weapon.getGunMuniId());
					int i = muni.getMuniItems(p.getInventory());
					if (i > 0) {
						WeaponSystem.playSound(p.getLocation(), "minecraft:weapon.reload", 5, 1);
						p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
						p.getInventory().setItemInOffHand(item);
						task = Bukkit.getScheduler().runTaskTimer(WeaponSystem.plugin, new Runnable() {
							int i = 0;
							
							@Override
							public void run() {
								String text = "";
								for (int j = 0; j < 20; j++) {
									if (j > i) {
										text += "§7#";
									} else {
										text += "§a#";
									}
								}
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
								i++;
								if (i == 20) {
									Bukkit.getScheduler().cancelTask(task);
								}
							}
						}, 0, (weapon.getGunReloadTime()*20)/20).getTaskId();
						Bukkit.getScheduler().runTaskLater(WeaponSystem.plugin, new Runnable() {
							
							@Override
							public void run() {
								p.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
								p.getInventory().setItemInMainHand(item);
								muni.removeItem(p.getInventory());
								magazin = weapon.getGunMuniCapacity();
								WeaponSystem.playSound(p.getLocation(), "minecraft:weapon.reload", 5, 1);
							}
						}, weapon.getGunReloadTime()*20);
					}
					showAmmo(p);
				} else {
					p.sendMessage(WeaponSystem.loadConfig("config", "messages").getChatColorString("munifull"));
				}
			}
		}, 1);
	}
	
	private void gunShot(Player p) {
		if (magazin > 0) {
			WeaponSystem.playSound(p.getLocation(), "minecraft:weapon.blast1", 30, 1);
			Snowball bullet = p.launchProjectile(Snowball.class);
			bullet.setVelocity(bullet.getVelocity().multiply(2));
			bullet.setCustomName(weapon.getName()+"_"+weapon.getGunDamage());
			magazin--;
		} else {
			WeaponSystem.playSound(p.getLocation(), "minecraft:weapon.empty", 5, 1);
		}
		showAmmo(p);
	}
	
	@EventHandler
	private void onHit(ProjectileHitEvent e) {
		Projectile p = e.getEntity();
		if (p instanceof Snowball) {
			String name = p.getCustomName();
			if (name.contains("_")) {
				int damage = Integer.parseInt(name.split("[_]")[1]);
				if (e.getHitEntity() instanceof LivingEntity) {
					((LivingEntity) e.getHitEntity()).damage(damage);
				}
			}
		}
	}
	
	@EventHandler
	private void onShot(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getHand() == EquipmentSlot.HAND) {
				if (item != null) {
					if (item.hasItemMeta()) {
						if (item.getItemMeta().hasLocalizedName()) {
							int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
							if (items.containsKey(id)) {
								WeaponItem itemWeapon = items.get(id);
								if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
									if (p.getCooldown(item.getType()) == 0) {
										p.setCooldown(item.getType(), (int) (itemWeapon.getWeapon().getCooldown()*20));
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
						itemWeapon.gunReleod(item, e.getPlayer());
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
						itemWeapon.gunReleod(item, e.getPlayer());
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
									damager.setCooldown(item.getType(), (int) (itemWeapon.getWeapon().getCooldown()*20));
								}
							}
						}
					}
				}
			}
		}
	}
}
