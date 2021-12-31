package de.paul.weaponsystem.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.crates.Crate;
import de.paul.weaponsystem.shop.ShopKeeper;
import de.paul.weaponsystem.shop.ShopKeeper.ShopType;

public class CommandPlaceShopNPC implements TabCompleter, CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("placeshopnpc"))) {
				if (args.length >= 1) {
					try {
						ShopType t = ShopType.valueOf(args[0]);
						new ShopKeeper(p.getLocation(), t);
					} catch (Exception e) {
						p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("noshoptype"));
					}
				} else {
					p.sendMessage("§cNutze§8: §8/§cplaceShopNPC <ShopType>");
				}
			} else {
				p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lsbel, String[] args) {
		return ShopKeeper.ShopType.names();
	}

}
