package de.paul.weaponsystem.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.muni.Muni;

public class MuniConfig extends Config {
	
	private int id;
	private String name;
	private String itemName;
	private int itemID;
	private int itemDamage;
	private ArrayList<String> itemLore = new ArrayList<>();
	private int costs;

	public MuniConfig(File f) throws IOException, ParseException {
		super(f);
		
		load();
	}

	private void load() {
		id = ((Long) get("id")).intValue();
		name = (String) get("name");
		itemName = getChatColorString("item_name");
		itemID = ((Long) get("item_id")).intValue();
		((JSONArray) get("item_lore")).forEach(new Consumer<Object>() {
			@Override
			public void accept(Object t) {
				itemLore.add(ChatColor.translateAlternateColorCodes('&', (String) t));
			}});
		itemDamage = ((Long) get("item_damage")).intValue();
		costs = ((Long) get("costs")).intValue();
	}
	
	public int getId() {
		return id;
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
	
	public int getCosts() {
		return costs;
	}

	public Muni toMuni() {
		return new Muni(this);
	}
}
