package de.paul.weaponsystem;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.sqlite.SQLiteConfig.SynchronousMode;

import de.paul.weaponsystem.assets.Assets;
import de.paul.weaponsystem.commands.CommandGetWeapon;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.config.MuniConfig;
import de.paul.weaponsystem.config.WeaponConfig;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.WeaponItem;
import de.paul.weaponsystem.weapon.muni.Muni;

public class WeaponSystem extends JavaPlugin {
	
	public static Plugin plugin;
	
	private static File weaponFolder;
	private static File muniFolder;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		try {
			weaponFolder = new File(getDataFolder(), "weapons");
			if (!weaponFolder.exists()) {
				weaponFolder.mkdirs();
				Assets.loadFolder("weapons", weaponFolder);
			}
			
			muniFolder = new File(getDataFolder(), "muni");
			if (!muniFolder.exists()) {
				muniFolder.mkdirs();
				Assets.loadFolder("muni", muniFolder);
			}
		
		
			loadWeapons();
			loadMuni();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		getCommand("getWeapon").setExecutor(new CommandGetWeapon());
		
		Bukkit.getPluginManager().registerEvents(new WeaponItem(), WeaponSystem.plugin);
	}
	
	private void loadMuni() throws IOException, ParseException {
		for (File muniFile : muniFolder.listFiles()) {
			MuniConfig muniConfig = new MuniConfig(muniFile);
			Muni.register(muniConfig.toMuni());
		}
	}

	private void loadWeapons() throws IOException, ParseException {
		for (File weaponFile : weaponFolder.listFiles()) {
			WeaponConfig weaponConfig = new WeaponConfig(weaponFile);
			Weapon.register(weaponConfig.toWeapon());
		}
	}

	public static Config loadConfig(String name, String... subConfig) {
		File configFile = new File(plugin.getDataFolder(), name+".json");
		try {
			Config c = new Config(configFile);
			if (subConfig.length == 0) {
				return c;
			} else {
				for (String subName : subConfig) {
					c = new Config((JSONObject) c.get(subName));
				}
				return c;
			}
		} catch (IOException e) {
			plugin.getDataFolder().mkdirs();
			
			Assets.copyFile(configFile, name+".json");
			
			return loadConfig(name, subConfig);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
