package de.paul.weaponsystem.config;

import java.io.File;
import java.io.IOException;

import org.json.simple.parser.ParseException;

public class WeaponConfig extends Config {

	private String name;
	private String itemName;

	public WeaponConfig(File f) throws IOException, ParseException {
		super(f);
		
		load();
	}
	
	private void load() {
		name = (String) get("name");
		itemName = getChatColorString("item_name");
	}
	
	public String getName() {
		return name;
	}
	
	public String getItemName() {
		return itemName;
	}
}
