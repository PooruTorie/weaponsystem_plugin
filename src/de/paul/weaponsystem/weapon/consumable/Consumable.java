package de.paul.weaponsystem.weapon.consumable;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.weapon.muni.Muni;
import de.paul.weaponsystem.weapon.muni.MuniItem;

public abstract class Consumable extends MuniItem {
	
	protected static HashMap<Integer, Consumable> items = new HashMap<>();
	
	public static void save() {
		Config weapons = WeaponSystem.loadConfig("data");
		JSONArray ws = (JSONArray) weapons.get("consumable");
		for (int i : items.keySet()) {
			Config c = new Config(new JSONObject());
			Consumable item = items.get(i);
			c.set("id", i);
			c.set("weaponName", item.getMuni().getName());
			ws.add(c.toJSON());
		}
		weapons.set("consumable", ws);
	}
	
	public static void load() {
		Config weapons = WeaponSystem.loadConfig("data");
		JSONArray ws = (JSONArray) weapons.get("consumable");
		for (Object o : ws) {
			Config c = new Config((JSONObject) o);
			int i = ((int) c.get("id"));
			String name = (String) c.get("weaponName");
			Muni muni = Muni.getMuniByName(name);
			items.put(i, muni.getConsumable());
		}
		ws.clear();
		weapons.set("consumable", ws);
	}
	
	protected int id;
	
	public Consumable(Muni muni) {
		super(muni);
		
		this.id = muni.getId();
		
		items.put(id, this);
	}
	
	public Consumable(Muni muni, int costs) {
		super(muni, costs);
		
		this.id = muni.getId();
		
		items.put(id, this);
	}

	public static void register() {
		Config config = WeaponSystem.loadConfig("config", "consumable");
		
		String nacrotic = (String) config.get("narcotics");
		Muni.register(new Muni(-100, "narcotics", "§6Narcotics", Integer.parseInt(nacrotic.split("[:]")[0]), Integer.parseInt(nacrotic.split("[:]")[1]), -1,  Narcotics.class));
		Muni.register(new Muni(-101, "antinarcotics", "§aAnti §6Narcotics", Integer.parseInt(nacrotic.split("[:]")[0]), Integer.parseInt(nacrotic.split("[:]")[1]), -1,  AntiNarcotics.class));
		
		Bukkit.getPluginManager().registerEvents(new ConsumableListener(), WeaponSystem.plugin);
	}

	abstract void Use(Player p, Player p2);
	
}
