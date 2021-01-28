package de.paul.weaponsystem;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.ParseException;

import de.paul.weaponsystem.assets.Assets;
import de.paul.weaponsystem.config.Config;

public class WeaponSystem extends JavaPlugin {
	
	public static Plugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.playSound(p.getLocation(), "minecraft:weapon.blast1", 50, 1);
		}
	}
	
	public static Config loadConfig(String name) {
		File configFile = new File(plugin.getDataFolder(), name+".json");
		try {
			return new Config(configFile);
		} catch (IOException e) {
			plugin.getDataFolder().mkdirs();
			
			Assets.copyFile(configFile, name+".json");
			
			return loadConfig(name);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
