package de.paul.weaponsystem.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.dyroxplays.revieve.objects.DeathPlayer;
import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.storages.Dealer;

public class CommandDealWeapon implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("dealweapon"))) {
				if (!DeathPlayer.isDead(p)) {
					if (args.length > 0) {
						if (args[0].equalsIgnoreCase("accept")) {
							Dealer.accept(p);
						} else {
							try {
								Player other = null;
								int costs = Integer.parseInt(args[0]);
								
								for (Entity e : p.getWorld().getNearbyEntities(p.getLocation(), 1, 1, 1)) {
									if (e instanceof Player) {
										if ((Player) e != p) {
											if (!DeathPlayer.isDead((Player) e)) {
												if (other == null) {
													other = (Player) e;
												} else if (distance(p, other) > distance(p, (Player) e)) {
													other = (Player) e;
												}
											}
										}
									}
								}
								
								if (other != null) {
									Dealer.startDeal(p, other, costs);
								}
							} catch (Exception e) {
								e.printStackTrace();
								p.sendMessage("§cUsage: /dealWeapon <Preis>");
							}
						}
					} else {
						p.sendMessage("§cUsage: /dealWeapon <Preis> oder /dealWeapon accept");
					}
				}
			} else {
				p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
			}
		}
		return false;
	}

	private float distance(Player p, Player o) {
		return (float) p.getEyeLocation().distanceSquared(o.getLocation());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> tab = new ArrayList<>();
		if (args.length == 1) {
			tab.add("accept");
			tab.add("10000");
		}
		return tab;
	}

}
