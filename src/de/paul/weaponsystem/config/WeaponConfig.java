package de.paul.weaponsystem.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.Weapon.WeaponType;

public class WeaponConfig extends Config {

	private WeaponType type;
	private String name;
	private String itemName;
	private int itemID;
	private ArrayList<String> itemLore = new ArrayList<>();
	private int meleeDamage;
	private int gunDamage;
	private int gunCooldown;
	private int gunMuniCapacity;
	private int gunMuniId;
	private int gunReloadTime;

	public WeaponConfig(File f) throws IOException, ParseException {
		super(f);
		
		load();
	}
	
	private void load() {
		type = Weapon.WeaponType.valueOf(((String) get("type")).toLowerCase());
		name = (String) get("name");
		itemName = getChatColorString("item_name");
		itemID = ((Long) get("item_id")).intValue();
		((JSONArray) get("item_lore")).forEach(new Consumer<Object>() {
			@Override
			public void accept(Object t) {
				itemLore.add((String) t);
			}});
		switch (type) {
		case gun:
			gunDamage = ((Long) get("damage")).intValue();
			gunCooldown = ((Long) get("cooldown")).intValue();
			gunMuniCapacity = ((Long) get("muni")).intValue();
			gunMuniId = ((Long) get("muni_id")).intValue();
			gunReloadTime = ((Long) get("reload_time")).intValue();
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
	public ArrayList<String> getItemLore() {
		return itemLore;
	}
	
	public int getMeleeDamage() {
		return meleeDamage;
	}
	
	public int getGunDamage() {
		return gunDamage;
	}
	
	public int getGunCooldown() {
		return gunCooldown;
	}
	
	public int getGunMuniCapacity() {
		return gunMuniCapacity;
	}
	
	public int getGunMuniId() {
		return gunMuniId;
	}
	
	public int getGunReloadTime() {
		return gunReloadTime;
	}
}
