package de.paul.weaponsystem.storages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.weapon.Weapon;

public class PlayerWeapons {
	
	private static HashMap<UUID, PlayerWeapons> data = new HashMap<>();
	
	private ArrayList<Weapon> buyedWeapons = new ArrayList<>();
	private JSONObject d;
	
	public static void save() {
		Config playerData = WeaponSystem.loadConfig("playerdata");
		for (UUID id : data.keySet()) {
			PlayerWeapons w = data.get(id);
			playerData.set(id.toString(), w.getData());
		}
	}
	
	public PlayerWeapons(UUID id) {
		Config playerData = WeaponSystem.loadConfig("playerdata");
		d = new JSONObject();
		if (playerData.contains(id.toString())) {
			d = (JSONObject) playerData.get(id.toString());
		} else {
			d.put("buyed", new JSONArray());
		}
		
		JSONArray buyed = (JSONArray) d.get("buyed");
		for (Object o : buyed) {
			String name = (String) o;
			buyedWeapons.add(Weapon.getWeaponByName(name));
		}
		
		data.put(id, this);
	}

	public boolean hasWeapon(Weapon w) {
		return buyedWeapons.contains(w);
	}
	
	public JSONObject getData() {
		return d;
	}
	
	public List<Weapon> getBuyedWeapons() {
		return buyedWeapons;
	}
	
	public static PlayerWeapons getForPlayer(OfflinePlayer p) {
		if (data.containsKey(p.getUniqueId())) {
			return data.get(p.getUniqueId());
		}
		return new PlayerWeapons(p.getUniqueId());
	}

	public void buy(Weapon w) {
		buyedWeapons.add(w);
		
		JSONArray a = new JSONArray();
		for (Weapon we : buyedWeapons) {
			a.add(we.getName());
		}
		d.put("buyed", a);
	}

	public void remove(Weapon w) {
		buyedWeapons.remove(w);
		
		JSONArray a = new JSONArray();
		for (Weapon we : buyedWeapons) {
			a.add(we.getName());
		}
		d.put("buyed", a);
	}
	
}
