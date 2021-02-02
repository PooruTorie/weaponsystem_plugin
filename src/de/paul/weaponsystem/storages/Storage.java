package de.paul.weaponsystem.storages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Entity.Spigot;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.dyroxplays.revieve.objects.DeathPlayer;
import de.dyroxplays.revieve.objects.FlyingItems;
import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.config.CrateConfig.CratePos;
import de.paul.weaponsystem.config.CrateConfig.CratePos.ItemType;
import de.paul.weaponsystem.crates.Crate;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.WeaponItem;

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
			storages.put(loc, StorageType.valueOf(name).getStorage().place(loc));
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
	
	public Storage(StorageType type) {
		super("storage"+type.name);
		this.type = type;
		
		type.setStorage(this);
	}
	
	@Override
	public void removeWeaopons(Player p) {
		for (Weapon weapon :  PlayerWeapons.getForPlayer(p).getBuyedWeapons()) {
			if (playerHasWeapon(p, weapon)) {
				int i = 0;
				for (ItemStack item : p.getInventory()) {
					if (item != null) {
						if (item.hasItemMeta()) {
							if (item.getItemMeta().hasLocalizedName()) {
								if (item.getItemMeta().getLocalizedName().contains(weapon.getName())) {
									if (item.getItemMeta().getLocalizedName().contains(getName())) {
										int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
										WeaponItem.items.get(id).remove();
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
	
	@Override
	public Storage place(Location loc) {
		v = (Witch) loc.getWorld().spawnEntity(loc, EntityType.WITCH);
		v.setAI(false);
		v.setCollidable(false);
		v.setCanPickupItems(false);
		v.setSilent(true);
		v.setCustomName(type.getName());
		
		storagesEnitys.put(v, this);
		storages.put(loc, this);
		
		return this;
	}
	
	public Entity getEntity() {
		return v;
	}
	
	public StorageType getType() {
		return type;
	}
	
	public static HashMap<UUID, Inventory> invs = new HashMap<>();
	
	public void openInv(Player p) {
		if (!DeathPlayer.isDead(p)) {
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
					if (i == 36) {
						i+=2;
					}
				}
			} else if (type == StorageType.muni) {
				for (ItemStack item : p.getEnderChest()) {
					if (item != null) {
						inv.setItem(i, item);
						i++;
						if (i == 16) {
							i+=2;
						}
						if (i == 36) {
							i+=2;
						}
						if (i == 40) {
							i++;
						}
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
				if (type == StorageType.muni) {
					if (i == 40) {
						i++;
					}
				}
			}
			
			p.openInventory(inv);
			invs.put(p.getUniqueId(), inv);
		}
	}
	
	public enum StorageType {
		weapon("§6Waffen Schrank"), muni("§6Munitions Schrank");

		private String name;
		private Storage storage;

		private StorageType(String name) {
			this.name = name;
			new Storage(this);
		}
		
		public void setStorage(Storage storage) {
			this.storage = storage;
		}
		
		public String getName() {
			return name;
		}
		
		public Storage getStorage() {
			return storage;
		}

		public static List<String> names() {
			ArrayList<String> l = new ArrayList<>();
			for (StorageType t : values()) {
				l.add(t.name());
			}
			return l;
		}
	}

	public static ItemStack[] toEnderChest(ItemStack[] contents) {
		ItemStack[] out = new ItemStack[9*3];
		int n = 0;
		for (int i = 11; i < contents.length;) {
			if (n < 27) {
				out[n] = contents[i];
				n++;
			}
			i++;
			if (i == 16) {
				i+=2;
			}
			if (i == 36) {
				i+=2;
			}
			if (i == 40) {
				i++;
			}
		}
		return out;
	}
	
}
