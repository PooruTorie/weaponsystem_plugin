package de.paul.weaponsystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.muni.Muni;

public class CommandGetList implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			p.sendMessage("§cAktuelle Waffen§8:");
			for (Weapon w : Weapon.getAll()) {
				p.sendMessage(w.getItemName());
			}
			p.sendMessage("§cAktuelle Munitionen§8:");
			for (Muni m : Muni.getAll()) {
				p.sendMessage(m.getItemName());
			}
		}
		return false;
	}

}
