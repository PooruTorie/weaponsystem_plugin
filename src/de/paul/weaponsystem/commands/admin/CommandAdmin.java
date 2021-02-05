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
import de.paul.weaponsystem.storages.Storage.StorageType;
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
									p.sendMessage("   §8- "+w.getItemName());
								}
							}
						}
					}
					if (args[0].equalsIgnoreCase("remove")) {
						if (args.length >= 3) {
							Player other = Bukkit.getPlayer(args[1]);
							Weapon w = Weapon.getWeaponByName(args[2]);
							if (other != null) {
								if (w != null) {
									if (PlayerWeapons.getForPlayer(other).hasWeapon(w)) {
										PlayerWeapons.getForPlayer(other).remove(w);
										StorageType.weapon.getStorage().removeWeaopon(other, w);
									}
								}
							}
						}
					}
					if (args[0].equalsIgnoreCase("add")) {
						if (args.length >= 3) {
							Player other = Bukkit.getPlayer(args[1]);
							Weapon w = Weapon.getWeaponByName(args[2]);
							if (other != null) {
								if (w != null) {
									if (!PlayerWeapons.getForPlayer(other).hasWeapon(w)) {
										PlayerWeapons.getForPlayer(other).buy(w);
									}
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
			tab.add("remove");
			tab.add("add");
		} else {
			if (args.length == 2) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					tab.add(p.getName());
				}
			} else {
				if (args.length == 3) {
					Player p = Bukkit.getPlayer(args[1]);
					if (p != null) {
						if (args[0].equalsIgnoreCase("remove")) {
							for (Weapon w : PlayerWeapons.getForPlayer(p).getBuyedWeapons()) {
								tab.add(w.getName());
							}
						}
						if (args[0].equalsIgnoreCase("add")) {
							for (Weapon w : Weapon.getAll()) {
								if (!PlayerWeapons.getForPlayer(p).hasWeapon(w)) {
									tab.add(w.getName());
								}
							}
						}
					}
				}
			}
		}
		return tab;
	}
}
