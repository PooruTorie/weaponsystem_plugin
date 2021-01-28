package de.paul.weaponsystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.weapon.Weapon;

public class CommandGetWeapon implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("getweapon"))) {
				if (args.length >= 1) {
					Weapon w = Weapon.getWeaponByName(args[0]);
					if (w != null) {
						
					} else {
						p.sendMessage(WeaponSystem.loadConfig("config", "messages").getChatColorString("noweapon"));
					}
				} else {
					p.sendMessage("cUsage: /getWeapon <WeaponName>");
				}
			} else {
				p.sendMessage(WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
			}
		}
		return false;
	}

}
