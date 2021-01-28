package de.paul.weaponsystem.weapon.muni;

import java.util.ArrayList;
import java.util.HashMap;

import de.paul.weaponsystem.config.MuniConfig;

public class Muni {

	private int id;
	private String name;
	private String itemName;
	private int itemID;
	private ArrayList<String> itemLore = new ArrayList<>();
	
	public Muni(MuniConfig config) {
		id = config.getId();
		name = config.getName();
		itemName = config.getItemName();
		itemID = config.getItemID();
		itemLore = config.getItemLore();
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
	
	private static HashMap<Integer, Muni> muni = new HashMap<>();

	public static void register(Muni muni) {
		Muni.muni.put(muni.getId(), muni);
	}
	
	public Muni getWeaponById(int id) {
		return muni.get(id);
	}
	
	public Muni getWeaponByName(String searchName) {
		for (Muni muni : muni.values()) {
			if (muni.getName().equals(searchName)) {
				return muni;
			}
		}
		return null;
	}

}
