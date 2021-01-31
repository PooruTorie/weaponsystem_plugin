package de.paul.weaponsystem.weapon.muni;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
		Config weapons = WeaponSystem.loadConfig("data");
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
		Config weapons = WeaponSystem.loadConfig("data");
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
		m.setUnbreakable(true);
		m.setLocalizedName(muni.getName()+"_"+muni.getId());
		setItemMeta(m);
		
		items.put(muni.getId(), this);
	}

	public MuniItem(Muni muni, int costs) {
		this(muni);
		ItemMeta m = getItemMeta();
		List<String> l = new ArrayList<>();
		l.add("§ePreis:");
		l.add("§8➥ §e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(costs)+"$");
		m.setLore(l);
		m.setLocalizedName(muni.getName()+"_"+muni.getId()+"_"+costs);
		setItemMeta(m);
	}

	public Muni getMuni() {
		return muni;
	}
	
	public static HashMap<Integer, MuniItem> getItems() {
		return items;
	}
}
