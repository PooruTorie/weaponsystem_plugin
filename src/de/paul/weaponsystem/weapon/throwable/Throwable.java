package de.paul.weaponsystem.weapon.throwable;



import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.Lists;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.weapon.muni.Muni;
import de.paul.weaponsystem.weapon.muni.MuniItem;

public abstract class Throwable extends MuniItem {

	protected static HashMap<Integer, Throwable> items = new HashMap<>();
	
	public static void save() {
		Config weapons = WeaponSystem.loadConfig("data");
		JSONArray ws = (JSONArray) weapons.get("throwable");
		for (int i : items.keySet()) {
			Config c = new Config(new JSONObject());
			Throwable item = items.get(i);
			c.set("id", i);
			c.set("weaponName", item.getMuni().getName());
			ws.add(c.toJSON());
		}
		weapons.set("throwable", ws);
	}
	
	public static void load() {
		Config weapons = WeaponSystem.loadConfig("data");
		JSONArray ws = (JSONArray) weapons.get("throwable");
		for (Object o : ws) {
			Config c = new Config((JSONObject) o);
			int i = ((int) c.get("id"));
			String name = (String) c.get("weaponName");
			Muni muni = Muni.getMuniByName(name);
			items.put(i, muni.getThrowable());
		}
		ws.clear();
		weapons.set("throwable", ws);
	}
	
	protected int id;
	
	public Throwable(Muni muni) {
		super(muni);
		
		this.id = muni.getId();
		
		items.put(id, this);
	}
	
	public Throwable(Muni muni, int costs) {
		super(muni, costs);
		
		this.id = muni.getId();
		
		items.put(id, this);
	}

	public static void register() {
		Config config = WeaponSystem.loadConfig("config", "throwable");
		String grenade = (String) config.get("grenade");
		Muni.register(new Muni(-1, "grenade", "§7Gre§6na§7de", Integer.parseInt(grenade.split("[:]")[0]), Integer.parseInt(grenade.split("[:]")[1]), 1000, ExplosiveGrenade.class));
		String flashbang = (String) config.get("flashbang");
		Muni.register(new Muni(-2, "flashbang", "§fFlashbang", Integer.parseInt(flashbang.split("[:]")[0]), Integer.parseInt(flashbang.split("[:]")[1]), 1000, FlashBang.class));
		String airstrike = (String) config.get("airstrike");
		Muni.register(new Muni(-3, "airstrike", "§cAir Strike", Integer.parseInt(airstrike.split("[:]")[0]), Integer.parseInt(airstrike.split("[:]")[1]), 10000, AirStrike.class));
		String smoke = (String) config.get("smoke");
		Muni.register(new Muni(-4, "smoke", "§8Smoke Grenade", Integer.parseInt(smoke.split("[:]")[0]), Integer.parseInt(smoke.split("[:]")[1]), 500, SmokeGrenade.class));
		String fire = (String) config.get("fire");
		Muni.register(new Muni(-5, "fire", "§6Fire Grenade", Integer.parseInt(fire.split("[:]")[0]), Integer.parseInt(fire.split("[:]")[1]), 700, FireGrenade.class));
		String molotov = (String) config.get("molotov");
		Muni.register(new Muni(-6, "molotov", "§cMolotov §6Cocktail", Integer.parseInt(molotov.split("[:]")[0]), Integer.parseInt(molotov.split("[:]")[1]), 600, Molotov.class));
		
		Bukkit.getPluginManager().registerEvents(new ThrowableEventListener(), WeaponSystem.plugin);
	}

	abstract void Throw(Player p);
}
