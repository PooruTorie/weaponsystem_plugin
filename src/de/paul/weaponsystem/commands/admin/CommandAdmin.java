package de.paul.weaponsystem.commands.admin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.storages.PlayerWeapons;
import de.paul.weaponsystem.weapon.Weapon;

public class CommandAdmin implements TabCompleter, CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("admin"))) {
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("list")) {
						if (args.length >= 2) {
							Player other = Bukkit.getPlayer(args[1]);
							if (other != null) {
								p.sendMessage("§7Waffen von §b"+other.getName());
								for (Weapon w : PlayerWeapons.getForPlayer(other).getBuyedWeapons()) {
									p.sendMessage("   §7- "+w.getItemName());
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> tab = new ArrayList<>();
		if (args.length == 1) {
			tab.add("list");
		} else {
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("list")) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						tab.add(p.getName());
					}
				}
			}
		}
		return tab;
	}
}
