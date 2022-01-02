package de.paul.weaponsystem.commands.pack;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.paul.weaponsystem.WeaponSystem;

public class CommandRelease implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("packen"))) {
				if (PackListener.packedPlayer.containsKey(p)) {
					p.performCommand("me lässt "+PackListener.packedPlayer.get(p).getName()+" los");
					PackListener.packedPlayer.remove(p);
				}
			} else {
				p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
			}
		}
		return false;
	}
}
