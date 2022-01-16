package de.paul.weaponsystem.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.crates.Crate;

public class CommandPlaceCrate implements TabCompleter, CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("placecrate"))) {
				if (args.length >= 1) {
					Crate c = Crate.getCrateByName(args[0]);
					if (c != null) {
						c.place(p.getLocation());
					} else {
						p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nocrate"));
					}
				} else {
					p.sendMessage("�cNutze�8: �8/�cplaceCrate <CrateName>");
				}
			} else {
				p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lsbel, String[] args) {
		return Crate.getAllNames();
	}

}
