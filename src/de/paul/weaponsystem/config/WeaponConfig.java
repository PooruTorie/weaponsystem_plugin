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

	public WeaponConfig(File f) throws IOException, ParseException {
		super(f);
		
		load();
	}
	
	private void load() {
		type = Weapon.WeaponType.valueOf((String) get("type"));
		name = (String) get("name");
		itemName = getChatColorString("item_name");
		itemID = ((Long) get("item_id")).intValue();
		((JSONArray) get("item_lore")).forEach(new Consumer<Object>() {
			@Override
			public void accept(Object t) {
				itemLore.add((String) t);
			}});
		switch (type) {
		case Gun:
			gunDamage = ((Long) get("damage")).intValue();
			break;
		case Melee:
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
}
