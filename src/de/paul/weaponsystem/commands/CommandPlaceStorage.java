package de.paul.weaponsystem.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.crates.Crate;
import de.paul.weaponsystem.shop.ShopKeeper;
import de.paul.weaponsystem.shop.ShopKeeper.ShopType;
import de.paul.weaponsystem.storages.Storage;
import de.paul.weaponsystem.storages.Storage.StorageType;

public class CommandPlaceStorage implements TabCompleter, CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("placestorage"))) {
				if (args.length >= 1) {
					try {
						StorageType t = StorageType.valueOf(args[0]);
						t.getStorage().place(p.getLocation());
					} catch (Exception e) {
						p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nostoragetype"));
					}
				} else {
					p.sendMessage("§cUsage: /placeStorage <StorageType>");
				}
			} else {
				p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lsbel, String[] args) {
		return Storage.StorageType.names();
	}

}
