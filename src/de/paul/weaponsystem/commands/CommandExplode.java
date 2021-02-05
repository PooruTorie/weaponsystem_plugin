package de.paul.weaponsystem.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.paul.weaponsystem.WeaponSystem;

public class CommandExplode implements TabCompleter, CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("explode"))) {
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("info")) {
						p.sendMessage(WeaponSystem.prefix+"§4Eine Sprengung kostet §e5000$");
					} else {
						String dest = args[0];
						double balance = WeaponSystem.economy.getBalance(p);
						if (balance >= 5000) {
							Creeper c = (Creeper) p.getWorld().spawnEntity(p.getLocation(), EntityType.CREEPER);
							c.setExplosionRadius(14);
							c.setAI(false);
							c.setCustomName("grenade");
							c.setMaxFuseTicks(0);
							WeaponSystem.economy.depositPlayer(p, 5000);
							
							for (Player op : Bukkit.getOnlinePlayers()) {
								op.sendMessage("§7Es gab einen Terror anschlag an "+dest);
							}
						} else {
							p.sendMessage(WeaponSystem.prefix+WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nomoney").replace("%money%", "§e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(5000-balance)+"$"));
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
			tab.add("info");
		}
		return tab;
	}

}
