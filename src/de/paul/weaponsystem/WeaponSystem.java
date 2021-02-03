package de.paul.weaponsystem;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import de.dyroxplays.revieve.RevieveAPI;
import de.dyroxplays.revieve.objects.DeathPlayer;
import de.dyroxplays.revieve.objects.FlyingItems;
import de.dyroxplays.revieve.objects.PlayerRealDeathEvent;
import de.paul.weaponsystem.assets.Assets;
import de.paul.weaponsystem.commands.CommandGetMuni;
import de.paul.weaponsystem.commands.CommandGetWeapon;
import de.paul.weaponsystem.commands.CommandPlaceCrate;
import de.paul.weaponsystem.commands.CommandPlaceShopNPC;
import de.paul.weaponsystem.commands.CommandPlaceStorage;
import de.paul.weaponsystem.config.Config;
import de.paul.weaponsystem.config.CrateConfig;
import de.paul.weaponsystem.config.MuniConfig;
import de.paul.weaponsystem.config.WeaponConfig;
import de.paul.weaponsystem.crates.Crate;
import de.paul.weaponsystem.shop.ShopKeeper;
import de.paul.weaponsystem.storages.PlayerWeapons;
import de.paul.weaponsystem.storages.Storage;
import de.paul.weaponsystem.storages.Storage.StorageType;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.WeaponItem;
import de.paul.weaponsystem.weapon.muni.Muni;
import de.paul.weaponsystem.weapon.muni.MuniItem;
import de.paul.weaponsystem.weapon.rocketLauncher.RPG;
import de.paul.weaponsystem.weapon.throwable.Throwable;
import net.milkbowl.vault.economy.Economy;

public class WeaponSystem extends JavaPlugin implements Listener {
	
	public static Plugin plugin;
	
	public static Economy economy;
	
	public static final String prefix = "§8• §eWaffenSystem §8┃ ";
	
	private static File cratesFolder;
	private static File weaponFolder;
	private static File muniFolder;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		try {
			cratesFolder = new File(getDataFolder(), "crates");
			if (!cratesFolder.exists()) {
				cratesFolder.mkdirs();
				Assets.loadFolder("crates", cratesFolder);
			}
			
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
		
			loadCrates();
			loadWeapons();
			loadMuni();
			
			RPG.register();
			Throwable.register();
			
			WeaponItem.load();
			MuniItem.load();
			Throwable.load();
			Crate.load();
			ShopKeeper.load();
			Storage.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		getCommand("getWeapon").setTabCompleter(new CommandGetWeapon());
		getCommand("getWeapon").setExecutor(new CommandGetWeapon());
		getCommand("getMuni").setTabCompleter(new CommandGetMuni());
		getCommand("getMuni").setExecutor(new CommandGetMuni());
		getCommand("placeCrate").setTabCompleter(new CommandPlaceCrate());
		getCommand("placeCrate").setExecutor(new CommandPlaceCrate());
		getCommand("placeShopNPC").setTabCompleter(new CommandPlaceShopNPC());
		getCommand("placeShopNPC").setExecutor(new CommandPlaceShopNPC());
		getCommand("placeStorage").setTabCompleter(new CommandPlaceStorage());
		getCommand("placeStorage").setExecutor(new CommandPlaceStorage());
		
		if (!setupEconomy()) {
			getLogger().log(Level.WARNING, "Can't find Economy Plugin");
		}
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			System.out.println(p.getName()+" - "+RevieveAPI.hasWeaponLicense(p));
			DeathPlayer dp = DeathPlayer.getDeathPlayer(p);
			FlyingItems i = new FlyingItems();
			i.setHeight(0.5);
			i.setItemStack(new ItemStack(Material.REDSTONE_BLOCK));
			i.setLocation(p.getLocation());
			i.setText("Hi");
			i.spawn();
		}
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	@EventHandler
	private void realDeath(PlayerRealDeathEvent e) {
		Player p = e.getDeathPlayer().getPlayer();
		for (Crate c : Crate.crates.values()) {
			c.removeWeaopons(p);
		}
		for (StorageType t : StorageType.values()) {
			t.getStorage().removeWeaopons(p);
		}
	}
	
	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Bukkit.getPluginManager().callEvent(new PlayerQuitEvent(p, ""));
		}
		WeaponItem.save();
		MuniItem.save();
		Throwable.save();
		Crate.save();
		ShopKeeper.save();
		Storage.save();
		PlayerWeapons.save();
	}
	
	@EventHandler
	private void onDisconect(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		for (Crate c : Crate.crates.values()) {
			c.removeWeaopons(p);
		}
	}
	
	private void loadCrates() throws IOException, ParseException {
		for (File muniFile : cratesFolder.listFiles()) {
			CrateConfig muniConfig = new CrateConfig(muniFile);
			Crate.register(muniConfig.toCrate());
		}
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

	public static void playSound(Location l, String name, float radius, float pitch) {
		for (Player p : l.getWorld().getPlayers()) {
			Location pl = p.getLocation();
			float d = (float) (radius*100f/pl.distanceSquared(l));
			p.playSound(l, name, d, pitch);
		}
	}
}
