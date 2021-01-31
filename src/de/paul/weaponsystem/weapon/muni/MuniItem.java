package de.paul.weaponsystem.weapon.muni;

import java.util.HashMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.crates.Crate;

public class MuniItem extends ItemStack {
	
	private static HashMap<Integer, MuniItem> items = new HashMap<>();
	
	public static void save() {
		Config weapons = WeaponSystem.loadConfig("playerWeapons");
		JSONArray ws = (JSONArray) weapons.get("muni");
		for (int i : items.keySet()) {
			Config c = new Config(new JSONObject());
			MuniItem item = items.get(i);
			c.set("id", (long) i);
			c.set("weaponName", item.getMuni().getName());
			ws.add(c.toJSON());
		}
		weapons.set("muni", ws);
	}
	
	public static void load() {
		Config weapons = WeaponSystem.loadConfig("playerWeapons");
		JSONArray ws = (JSONArray) weapons.get("muni");
		for (Object o : ws) {
			Config c = new Config((JSONObject) o);
			int i = ((Long) c.get("id")).intValue();
			String name = (String) c.get("weaponName");
			Muni muni = Muni.getMuniByName(name);
			items.put(i, new MuniItem(muni));
		}
		ws.clear();
		weapons.set("muni", ws);
	}
	
	private Muni muni;

	public MuniItem(Muni muni) {
		super(muni.getItemID(), 1, (short) muni.getItemDamage());
		this.muni = muni;
		
		ItemMeta m = getItemMeta();
		m.setDisplayName(muni.getItemName());
		m.setLore(muni.getItemLore());
		m.setUnbreakable(true);
		m.setLocalizedName(muni.getName()+"_"+muni.getId());
		setItemMeta(m);
		
		items.put(muni.getId(), this);
	}

	public Muni getMuni() {
		return muni;
	}
	
	public static HashMap<Integer, MuniItem> getItems() {
		return items;
	}
}
