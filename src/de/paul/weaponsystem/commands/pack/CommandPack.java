package de.paul.weaponsystem.commands.pack;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.weapon.muni.Muni;

public class CommandPack implements TabCompleter, CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("packen"))) {
				if (args.length >= 1) {
					Player other = Bukkit.getPlayer(args[0]);
					if (other != null) {
						if (other.getWorld().equals(p.getWorld())) {
							if (other.getLocation().distanceSquared(p.getLocation()) <= 2) {
								PackListener.packedPlayer.put(p, other);
								p.performCommand("me packt "+other.getName());
								return true;
							}
						}
						p.sendMessage(WeaponSystem.prefix+"§cDer Spieler ist zu weit weg.");
					} else {
						p.sendMessage(WeaponSystem.prefix+"§cDer Spieler "+args[0]+" ist nicht Online.");
					}
				} else {
					p.sendMessage("§cNutze§8: §8/§cpacken <Spieler>");
				}
			} else {
				p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lsbel, String[] args) {
		List<String> tab = new ArrayList<String>();
		if (sender instanceof Player) {
			Player p = (Player) sender;
			for (Entity e : p.getNearbyEntities(2, 2, 2)) {
				if (e.getType() == EntityType.PLAYER) {
					tab.add(e.getName());
				}
			}
		}
		return tab;
	}
}
