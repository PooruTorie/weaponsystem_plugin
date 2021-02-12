package de.paul.weaponsystem.commands.admin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.dyroxplays.revieve.RevieveAPI;
import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.storages.PlayerWeapons;
import de.paul.weaponsystem.storages.Storage.StorageType;
import de.paul.weaponsystem.weapon.Weapon;

public class CommandBlock implements TabCompleter, CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("block"))) {
				if (args.length >= 2) {
					Player other = Bukkit.getPlayer(args[0]);
					if (other != null) {
						try {
							float f = Float.parseFloat(args[1]);
							PlayerWeapons.getForPlayer(other).block(f);
						} catch (Exception e) {
							p.sendMessage("§cUsage: /waffensperre <Spieler> <Zeit in Stunden>");
						}
					} else {
						p.sendMessage(WeaponSystem.prefix+"§cDer Spieler §a"+args[0]+"§c ist nicht online.");
					}
				} else {
					p.sendMessage("§cUsage: /waffensperre <Spieler> <Zeit in Stunden>");
				}
			} else {
				p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> tab = new ArrayList<>();
		if (args.length == 1) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				tab.add(p.getName());
			}
		}
		if (args.length == 2) {
			tab.add("1");
		}
		return tab;
	}

}
