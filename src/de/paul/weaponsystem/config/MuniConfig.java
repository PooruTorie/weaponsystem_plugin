package de.paul.weaponsystem.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.muni.Muni;

public class MuniConfig extends Config {
	
	private int id;
	private String name;
	private String itemName;
	private int itemID;
	private ArrayList<String> itemLore = new ArrayList<>();

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
				itemLore.add((String) t);
			}});
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
	
	public ArrayList<String> getItemLore() {
		return itemLore;
	}

	public Muni toMuni() {
		return new Muni(this);
	}
}
