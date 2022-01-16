package de.paul.weaponsystem.config;

import java.io.File;
import java.io.IOException;

import org.json.simple.parser.ParseException;

import de.dyroxplays.revieve.lizenz.Lizenz.LizenzType;
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
	private boolean inShop = true;
	private boolean privateShop = false;
	private boolean gunZoom = false;
	private String gunShotSound = "";
	private int costs;
	private LizenzType lizenz;

	public WeaponConfig(File f) throws IOException, ParseException {
		super(f);
		
		load();
	}
	
	private void load() {
		type = Weapon.WeaponType.valueOf(((String) get("type")).toLowerCase());
		name = (String) get("name");
		itemName = getChatColorString("item_name");
		itemID = ((int) get("item_id"));
		itemDamage = ((int) get("item_damage"));
		cooldown = (double) get("cooldown");
		costs = ((int) get("costs"));
		lizenz = LizenzType.values()[((int) get("lizenz"))];
		if (contains("inshop")) {
			inShop = (boolean) get("inshop");
		}
		if (contains("privateshop")) {
			inShop = false;
			privateShop = (boolean) get("privateshop");
		}
		switch (type) {
		case gun:
			gunDamage = ((int) get("damage_per_bullet"));
			gunMuniCapacity = ((int) get("muni"));
			gunMuniId = ((int) get("muni_id"));
			gunReloadTime = ((int) get("reload_time"));
			gunBullets = ((int) get("bullets"));
			gunAcuracy = ((int) get("acuracy"));
			if (contains("shootsound")) {
				gunShotSound = (String) get("shootsound");
			}
			if (contains("zoom")) {
				gunZoom = (boolean) get("zoom");
			}
			break;
		case melee:
			meleeDamage = ((int) get("damage"));
			break;
		}
	}
	
	public Weapon toWeapon() {
		return new Weapon(this);
	}
	
	public WeaponType getType() {
		return type;
	}
	
	public boolean isInShop() {
		return inShop;
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

	public LizenzType getLizenz() {
		return lizenz;
	}

	public boolean isPrivateShop() {
		return privateShop;
	}
}
