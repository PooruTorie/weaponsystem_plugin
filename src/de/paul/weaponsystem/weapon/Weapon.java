package de.paul.weaponsystem.weapon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.paul.weaponsystem.config.WeaponConfig;
import de.paul.weaponsystem.crates.Crate;

public class Weapon {
	
	private WeaponType type;
	private String name;
	private String itemName;
	private int itemID;
	private int itemDamage;
	private ArrayList<String> itemLore = new ArrayList<>();
	private int meleeDamage;
	private int gunDamage;
	private float cooldown;
	private int gunMuniCapacity;
	private int gunBullets;
	private int gunAcuracy;
	private int gunMuniId;
	private int gunReloadTime;
	private String gunShotSound;
	private int costs;
	
	private Class<? extends WeaponItem> weaponClass = null;
	
	public Weapon(WeaponConfig config) {
		type = config.getType();
		name = config.getName();
		itemName = config.getItemName();
		itemID = config.getItemID();
		itemDamage = config.getItemDamage();
		itemLore = config.getItemLore();
		meleeDamage = config.getMeleeDamage();
		gunDamage = config.getGunDamage();
		cooldown = config.getCooldown();
		gunMuniCapacity = config.getGunMuniCapacity();
		gunMuniId = config.getGunMuniId();
		gunReloadTime = config.getGunReloadTime();
		gunBullets = config.getGunBullets();
		gunAcuracy = config.getGunAcuracy();
		gunShotSound = config.getGunShotSound();
		costs = config.getCosts();
	}
	
	public Weapon(WeaponType type, String name, String itemName, int itemID, ArrayList<String> itemLore, int gunMuniCapacity, int gunMuniId, int gunReloadTime, int costs, Class<? extends WeaponItem> weaponClass) {
		this.type = type;
		this.name = name;
		this.itemName = itemName;
		this.itemID = itemID;
		this.itemLore = itemLore;
		this.meleeDamage = 0;
		this.gunDamage = 0;
		this.cooldown = 0;
		this.gunMuniCapacity = gunMuniCapacity;
		this.gunMuniId = gunMuniId;
		this.gunReloadTime = gunReloadTime;
		this.gunBullets = 0;
		this.gunAcuracy = 0;
		this.gunShotSound = "";
		this.weaponClass = weaponClass;
		this.costs = costs;
	}
	
	public WeaponType getType() {
		return type;
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
	
	public ArrayList<String> getItemLore() {
		return itemLore;
	}
	
	public int getMeleeDamage() {
		return meleeDamage;
	}
	
	public int getGunDamage() {
		return gunDamage;
	}
	
	public float getCooldown() {
		return cooldown;
	}
	
	public int getGunMuniCapacity() {
		return gunMuniCapacity;
	}
	
	public int getGunMuniId() {
		return gunMuniId;
	}
	
	public int getGunBullets() {
		return gunBullets;
	}
	
	public int getGunAcuracy() {
		return gunAcuracy;
	}
	
	public int getGunReloadTime() {
		return gunReloadTime;
	}
	
	public String getGunShotSound() {
		return gunShotSound;
	}
	
	public boolean hasWeaponClass() {
		return weaponClass != null;
	}
	
	public int getCosts() {
		return costs;
	}
	
	public Class<? extends WeaponItem> getWeaponClass() {
		return weaponClass;
	}
	
	public void give(Player p) {
		if (hasWeaponClass()) {
			try {
				Object item = weaponClass.getConstructor(Weapon.class).newInstance(this);
				p.getInventory().addItem((ItemStack) item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			WeaponItem item = new WeaponItem(this);
			p.getInventory().addItem(item);
		}
	}
	
	public void give(Player p, Crate crate) {
		if (hasWeaponClass()) {
			try {
				Object item = weaponClass.getConstructor(Weapon.class, Crate.class).newInstance(this, crate);
				p.getInventory().addItem((ItemStack) item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			WeaponItem item = new WeaponItem(this, crate);
			p.getInventory().addItem(item);
		}
	}

	public enum WeaponType {
		gun, melee;
	}
	
	private static HashMap<Integer, Weapon> weapons = new HashMap<>();
	private static int index = 0;

	public static void register(Weapon weapon) {
		weapons.put(index, weapon);
		index++;
	}
	
	public static Weapon getWeaponByIndex(int index) {
		return weapons.get(index);
	}
	
	public static Weapon getWeaponByName(String searchName) {
		for (Weapon weapon : weapons.values()) {
			if (weapon.getName().equalsIgnoreCase(searchName)) {
				return weapon;
			}
		}
		return null;
	}
	
	public static ArrayList<String> getAllNames() {
		ArrayList<String> a = new ArrayList<>();
		for (Weapon weapon : weapons.values()) {
			a.add(weapon.getName());
		}
		return a;
	}
	
	public static List<Weapon> getAll() {
		return new ArrayList(weapons.values());
	}

	public Object loadItem(int i, int magazin) {
		if (hasWeaponClass()) {
			try {
				Object item = weaponClass.getConstructor(Weapon.class, int.class, int.class).newInstance(this, i, magazin);
				return item;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			WeaponItem item = new WeaponItem(this, i, magazin);
			return item;
		}
	}

	public ItemStack toItemStack(boolean costs) {
		if (costs) {
			if (hasWeaponClass()) {
				try {
					Object item = weaponClass.getConstructor(Weapon.class, int.class, int.class, int.class).newInstance(this, 0, 0, getCosts());
					return (ItemStack) item;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} else {
				WeaponItem item = new WeaponItem(this, 0, 0, getCosts());
				return item;
			}
		} else {
			if (hasWeaponClass()) {
				try {
					Object item = weaponClass.getConstructor(Weapon.class, int.class, int.class).newInstance(this, 0, 0);
					return (ItemStack) item;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} else {
				WeaponItem item = new WeaponItem(this, 0, 0);
				return item;
			}
		}
	}
}
