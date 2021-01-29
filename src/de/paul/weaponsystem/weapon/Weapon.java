package de.paul.weaponsystem.weapon;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import de.paul.weaponsystem.config.WeaponConfig;
import de.paul.weaponsystem.weapon.Weapon.WeaponType;

public class Weapon {
	
	private WeaponType type;
	private String name;
	private String itemName;
	private int itemID;
	private ArrayList<String> itemLore = new ArrayList<>();
	private int meleeDamage;
	private int gunDamage;
	private float cooldown;
	private int gunMuniCapacity;
	private int gunBullets;
	private int gunAcuracy;
	private int gunMuniId;
	private int gunReloadTime;
	
	private WeaponConfig config;
	
	public WeaponConfig getConfig() {
		return config;
	}
	
	public Weapon(WeaponConfig config) {
		this.config = config;
		type = config.getType();
		name = config.getName();
		itemName = config.getItemName();
		itemID = config.getItemID();
		itemLore = config.getItemLore();
		meleeDamage = config.getMeleeDamage();
		gunDamage = config.getGunDamage();
		cooldown = config.getCooldown();
		gunMuniCapacity = config.getGunMuniCapacity();
		gunMuniId = config.getGunMuniId();
		gunReloadTime = config.getGunReloadTime();
		gunBullets = config.getGunBullets();
		gunAcuracy = config.getGunAcuracy();
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
	
	public void give(Player p) {
		WeaponItem item = new WeaponItem(this);
		p.getInventory().addItem(item);
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
}
