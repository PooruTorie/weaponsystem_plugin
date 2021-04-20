package de.paul.weaponsystem.weapon;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import de.paul.weaponsystem.crates.Crate;
import de.paul.weaponsystem.storages.PlayerWeapons;
import de.paul.weaponsystem.weapon.Weapon.WeaponType;
import de.paul.weaponsystem.weapon.muni.Muni;
import de.paul.weaponsystem.weapon.throwable.Throwable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class WeaponItem extends ItemStack {
	
	public static HashMap<Integer, WeaponItem> items = new HashMap<>();
	
	public static void save() {
		Config weapons = WeaponSystem.loadConfig("data");
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
		Config weapons = WeaponSystem.loadConfig("data");
		JSONArray ws = (JSONArray) weapons.get("weapons");
		for (Object o : ws) {
			Config c = new Config((JSONObject) o);
			int i = ((Long) c.get("id")).intValue();
			int magazin = ((Long) c.get("magazin")).intValue();
			String name = (String) c.get("weaponName");
			Weapon weapon = Weapon.getWeaponByName(name);
			items.put(i, (WeaponItem) weapon.loadItem(i, magazin));
		}
		ws.clear();
		weapons.set("weapons", ws);
		
		Bukkit.getPluginManager().registerEvents(new WeaponEventListener(), WeaponSystem.plugin);
	}
	
	protected Weapon weapon;
	protected int magazin = 0;
	protected int id;
	
	public WeaponItem(Weapon weapon) {
		super(weapon.getItemID(), 1, (short) weapon.getItemDamage());
		this.weapon = weapon;
		
		id = (int) (Math.random()*1000)-1000;
		
		ItemMeta m = getItemMeta();
		m.setDisplayName(weapon.getItemName());
		m.setUnbreakable(true);
		m.setLocalizedName(weapon.getName()+"_"+id);
		setItemMeta(m);
		
		items.put(id, this);
	}
	
	public WeaponItem(Weapon weapon, int id, int magazin) {
		super(weapon.getItemID(), 1, (short) weapon.getItemDamage());
		this.weapon = weapon;
		
		this.id = id;
		
		ItemMeta m = getItemMeta();
		m.setDisplayName(weapon.getItemName());
		m.setUnbreakable(true);
		m.setLocalizedName(weapon.getName()+"_"+id);
		setItemMeta(m);
		
		this.magazin = magazin;
	}
	
	public WeaponItem(Weapon weapon, Crate crate) {
		this(weapon);
		ItemMeta m = getItemMeta();
		m.setLocalizedName(weapon.getName()+"_"+id+"_"+crate.getName());
		setItemMeta(m);
	}

	public WeaponItem(Weapon weapon, int id, int magazin, int costs) {
		this(weapon, id, magazin);
		ItemMeta m = getItemMeta();
		List<String> l = new ArrayList<>();
		l.add("§3"+weapon.getLizenz().name().replace('_', ' '));
		l.add("§ePreis:");
		l.add("§8➥ §e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(costs)+"$");
		m.setLocalizedName(weapon.getName()+"_"+id+"_"+costs);
		m.setLore(l);
		setItemMeta(m);
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
	
	public void gunReleod(ItemStack item, Player p) {
		Bukkit.getScheduler().runTaskLater(WeaponSystem.plugin, new Runnable() {
			
			private int task;

			@Override
			public void run() {
				if (magazin < weapon.getGunMuniCapacity()) {
					Muni muni = Muni.getMuniById(weapon.getGunMuniId());
					int i = muni.getMuniItems(p.getInventory());
					if (i > 0) {
						WeaponSystem.playSound(p.getLocation(), "minecraft:weapon.reload", 5, 1);
						task = Bukkit.getScheduler().runTaskTimer(WeaponSystem.plugin, new Runnable() {
							int i = 0;
							
							@Override
							public void run() {
								String text = "§a"+i+"%";
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
								i++;
								if (i == 101) {
									Bukkit.getScheduler().cancelTask(task);
								}
							}
						}, 0, (weapon.getGunReloadTime()*20)/100).getTaskId();
						Bukkit.getScheduler().runTaskLater(WeaponSystem.plugin, new Runnable() {
							
							@Override
							public void run() {
								muni.removeItem(p.getInventory());
								magazin = weapon.getGunMuniCapacity();
							}
						}, weapon.getGunReloadTime()*20);
					} else {
						p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("hasnomuni"));
					}
					showAmmo(p);
				} else {
					p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("munifull"));
				}
			}
		}, 1);
	}
	
	public void remove(Player p) {
		setType(Material.AIR);
		items.remove(id);
	}
	
	public void gunShot(Player p) {
		if (!PlayerWeapons.getForPlayer(p).isBlocked()) {
			if (magazin > 0) {
				float a = ((weapon.getGunAcuracy()-100f)*-1f)/100f;
				Random r = new Random();
				if (weapon.getGunShotSound().isEmpty()) {
					WeaponSystem.playSound(p.getLocation(), "minecraft:weapon.blast1", 30, 1);
				} else {
					WeaponSystem.playSound(p.getLocation(), "minecraft:"+weapon.getGunShotSound(), 30, 1);
				}
				for (int i = 0; i < weapon.getGunBullets(); i++) {
					Snowball bullet = p.launchProjectile(Snowball.class);
					bullet.setVelocity(bullet.getVelocity().multiply(2f).add(new Vector(((r.nextFloat()*2)-1)*a, ((r.nextFloat()*2)-1)*a, ((r.nextFloat()*2)-1)*a)));
					bullet.setCustomName(weapon.getName()+"_"+weapon.getGunDamage());
					bullet.setGravity(false);
				}
				p.spawnParticle(Particle.SMOKE_NORMAL, p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(0.45)), 2, 0.01, 0.01, 0.01, 0.03);
				magazin--;
			} else {
				WeaponSystem.playSound(p.getLocation(), "minecraft:weapon.empty", 5, 1);
			}
			showAmmo(p);
		} else {
			p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("block"));
		}
	}

	public static WeaponItem getWeaponByItem(ItemStack item) {
		if (item != null) {
			if (item.hasItemMeta()) {
				if (item.getItemMeta().hasLocalizedName()) {
					int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
					if (items.containsKey(id)) {
						return items.get(id);
					}
				}
			}
		}
		return null;
	}
}
