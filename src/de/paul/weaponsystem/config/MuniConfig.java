package de.paul.weaponsystem.config;

import java.io.File;
import java.io.IOException;

import org.json.simple.parser.ParseException;

import de.paul.weaponsystem.weapon.muni.Muni;

public class MuniConfig extends Config {
	
	private int id;
	private String name;
	private String itemName;
	private int itemID;
	private int itemDamage;
	private int costs;

	public MuniConfig(File f) throws IOException, ParseException {
		super(f);
		
		load();
	}

	private void load() {
		id = ((int) get("id"));
		name = (String) get("name");
		itemName = getChatColorString("item_name");
		itemID = ((int) get("item_id"));
		itemDamage = ((int) get("item_damage"));
		costs = ((int) get("costs"));
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
	
	public int getCosts() {
		return costs;
	}

	public Muni toMuni() {
		return new Muni(this);
	}
}
