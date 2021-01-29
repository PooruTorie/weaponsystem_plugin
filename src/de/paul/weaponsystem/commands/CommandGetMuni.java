package de.paul.weaponsystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.muni.Muni;

public class CommandGetMuni implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("getmuni"))) {
				if (args.length >= 1) {
					Muni m = Muni.getWeaponByName(args[0]);
					if (m != null) {
						m.give(p);
					} else {
						p.sendMessage(WeaponSystem.loadConfig("config", "messages").getChatColorString("nomuni"));
					}
				} else {
					p.sendMessage("§cUsage: /getMuni <MuniName>");
				}
			} else {
				p.sendMessage(WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
			}
		}
		return false;
	}

}
