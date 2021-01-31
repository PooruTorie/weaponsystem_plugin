package de.paul.weaponsystem.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import de.paul.weaponsystem.config.CrateConfig.CratePos.ItemType;
import de.paul.weaponsystem.crates.Crate;

public class CrateConfig extends Config {
	
	private String name;
	private String permission;
	private String invName;
	private int blockMat;
	private ArrayList<CratePos> items = new ArrayList<>();

	public CrateConfig(File f) throws IOException, ParseException {
		super(f);
		
		load();
	}

	@SuppressWarnings("unchecked")
	private void load() {
		name = (String) get("name");
		invName = getChatColorString("inv_name");
		permission = (String) get("permission");
		blockMat = ((Long) get("blockid")).intValue();
		((JSONArray) get("items")).forEach(new Consumer<Object>() {

			@Override
			public void accept(Object o) {
				Config c = new Config((JSONObject) o);
				ItemType t = ItemType.valueOf((String) c.get("type"));
				String n = (String) c.get("name");
				int s = ((Long) c.get("slot")).intValue();
				items.add(new CratePos(t, n, s));
			}
		});
	}
	
	public String getName() {
		return name;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public String getInvName() {
		return invName;
	}
	
	public int getBlockMat() {
		return blockMat;
	}
	
	public ArrayList<CratePos> getItems() {
		return items;
	}
	
	public Crate toCrate() {
		return new Crate(this);
	}
	
	public static class CratePos {
		
		private ItemType itemType;
		private String itemName;
		private int slot;
		
		public CratePos(ItemType itemType, String itemName, int slot) {
			this.itemType = itemType;
			this.itemName = itemName;
			this.slot = slot;
		}
		
		public ItemType getItemType() {
			return itemType;
		}
		
		public String getItemName() {
			return itemName;
		}
		
		public int getSlot() {
			return slot;
		}
		
		public static enum ItemType {
			weapon, muni;
		}
	}
}