package de.paul.weaponsystem.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.muni.Muni;

public class CommandGetWeapon implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("getweapon"))) {
				if (args.length >= 1) {
					Weapon w = Weapon.getWeaponByName(args[0]);
					if (w != null) {
						w.give(p);
					} else {
						p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("noweapon"));
					}
				} else {
					p.sendMessage("§cNutze§8: §8/§cgetWeapon <WeaponName>");
				}
			} else {
				p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lsbel, String[] args) {
		return Weapon.getAllNames();
	}

}
