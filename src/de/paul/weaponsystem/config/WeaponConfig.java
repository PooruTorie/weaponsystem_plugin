package de.paul.weaponsystem.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.Weapon.WeaponType;

public class WeaponConfig extends Config {

	private WeaponType type;
	private String name;
	private String itemName;
	private int itemID;
	private int itemDamage;
	private double cooldown;
	private int meleeDamage;
	private int gunDamage;
	private int gunBullets;
	private int gunAcuracy;
	private int gunMuniCapacity;
	private int gunMuniId;
	private int gunReloadTime;
	private boolean gunZoom = false;
	private String gunShotSound = "";
	private int costs;

	public WeaponConfig(File f) throws IOException, ParseException {
		super(f);
		
		load();
	}
	
	private void load() {
		type = Weapon.WeaponType.valueOf(((String) get("type")).toLowerCase());
		name = (String) get("name");
		itemName = getChatColorString("item_name");
		itemID = ((Long) get("item_id")).intValue();
		itemDamage = ((Long) get("item_damage")).intValue();
		cooldown = (double) get("cooldown");
		costs = ((Long) get("costs")).intValue();
		switch (type) {
		case gun:
			gunDamage = ((Long) get("damage_per_bullet")).intValue();
			gunMuniCapacity = ((Long) get("muni")).intValue();
			gunMuniId = ((Long) get("muni_id")).intValue();
			gunReloadTime = ((Long) get("reload_time")).intValue();
			gunBullets = ((Long) get("bullets")).intValue();
			gunAcuracy = ((Long) get("acuracy")).intValue();
			if (contains("shootsound")) {
				gunShotSound = (String) get("shootsound");
			}
			if (contains("zoom")) {
				gunZoom = (boolean) get("zoom");
			}
			break;
		case melee:
			meleeDamage = ((Long) get("damage")).intValue();
			break;
		}
	}
	
	public Weapon toWeapon() {
		return new Weapon(this);
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
	
	public int getMeleeDamage() {
		return meleeDamage;
	}
	
	public int getGunDamage() {
		return gunDamage;
	}
	
	public float getCooldown() {
		return (float) cooldown;
	}
	
	public int getGunMuniCapacity() {
		return gunMuniCapacity;
	}
	
	public int getGunAcuracy() {
		return gunAcuracy;
	}
	
	public int getGunBullets() {
		return gunBullets;
	}
	
	public int getGunMuniId() {
		return gunMuniId;
	}
	
	public int getGunReloadTime() {
		return gunReloadTime;
	}
	
	public int getCosts() {
		return costs;
	}
	
	public boolean isGunZoom() {
		return gunZoom;
	}
	
	public String getGunShotSound() {
		return gunShotSound;
	}
}
