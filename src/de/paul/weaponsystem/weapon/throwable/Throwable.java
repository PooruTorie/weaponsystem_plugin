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
		Config weapons = WeaponSystem.loadConfig("playerWeapons");
		JSONArray ws = (JSONArray) weapons.get("throwable");
		for (int i : items.keySet()) {
			Config c = new Config(new JSONObject());
			Throwable item = items.get(i);
			c.set("id", (long) i);
			c.set("weaponName", item.getMuni().getName());
			ws.add(c.toJSON());
		}
		weapons.set("throwable", ws);
	}
	
	public static void load() {
		Config weapons = WeaponSystem.loadConfig("playerWeapons");
		JSONArray ws = (JSONArray) weapons.get("throwable");
		for (Object o : ws) {
			Config c = new Config((JSONObject) o);
			int i = ((Long) c.get("id")).intValue();
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

	public static void register() {
		Muni.register(new Muni(-1, "grenade", "§7Gre§6na§7de", 351, 8, Lists.newArrayList("§6Rightclick to launch"), Grenade.class));
		Muni.register(new Muni(-2, "flashbang", "§fFlashbang", 341, 0, Lists.newArrayList("§6Rightclick to launch"), FlashBang.class));
		
		Bukkit.getPluginManager().registerEvents(new ThrowableEventListener(), WeaponSystem.plugin);
	}

	abstract void Throw(Player p);
}
