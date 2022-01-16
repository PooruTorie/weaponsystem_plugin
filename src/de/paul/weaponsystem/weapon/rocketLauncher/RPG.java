package de.paul.weaponsystem.weapon.rocketLauncher;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.dyroxplays.revieve.lizenz.Lizenz.LizenzType;
import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.crates.Crate;
import de.paul.weaponsystem.storages.PlayerWeapons;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.Weapon.WeaponType;
import de.paul.weaponsystem.weapon.WeaponItem;
import de.paul.weaponsystem.weapon.muni.Muni;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class RPG extends WeaponItem {
	
	public RPG(Weapon weapon) {
		super(weapon);
	}
	
	public RPG(Weapon weapon, Crate c) {
		super(weapon, c);
	}
	
	public RPG(Weapon weapon, int id, int magazin) {
		super(weapon, id, magazin);
	}
	
	public RPG(Weapon weapon, int id, int magazin, int costs) {
		super(weapon, id, magazin, costs);
	}
	
	@Override
	public void gunReleod(ItemStack item, Player p) {
		Bukkit.getScheduler().runTaskLater(WeaponSystem.plugin, new Runnable() {
			
			private int task;

			@Override
			public void run() {
				if (magazin < weapon.getGunMuniCapacity()) {
					Muni muni = Muni.getMuniById(weapon.getGunMuniId());
					int i = muni.getMuniItems(p.getInventory());
					if (i > 0) {
						WeaponSystem.playSound(p.getLocation(), "minecraft:rpg.rpgreload", 5, 1);
						task = Bukkit.getScheduler().runTaskTimer(WeaponSystem.plugin, new Runnable() {
							int i = 0;
							
							@Override
							public void run() {
								String text = "";
								for (int j = 0; j < 20; j++) {
									if (j > i) {
										text += "§7░";
									} else {
										text += "§a█";
									}
								}
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
								i++;
								if (i == 20) {
									Bukkit.getScheduler().cancelTask(task);
								}
							}
						}, 0, (weapon.getGunReloadTime()*20)/20).getTaskId();
						Bukkit.getScheduler().runTaskLater(WeaponSystem.plugin, new Runnable() {
							
							@Override
							public void run() {
								muni.removeItem(p.getInventory());
								magazin = weapon.getGunMuniCapacity();
							}
						}, weapon.getGunReloadTime()*20);
					} else {
						p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("hasnomuni"));
					}
					showAmmo(p);
				} else {
					p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("munifull"));
				}
			}
		}, 1);
	}
	
	@Override
	public void gunShot(Player p) {
		if (!PlayerWeapons.getForPlayer(p).isBlocked()) {
			if (magazin > 0) {
				WeaponSystem.playSound(p.getLocation(), "minecraft:rpg.rpg", 30, 1);
				Rocket.shot(p);
				magazin--;
			}
			showAmmo(p);
		} else {
			p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("block"));
		}
	}
	
	public static void register() {
		Weapon.register(new Weapon(WeaponType.gun, "rpg", "§8RPG", 286, 1, 3, 4, 75000, false, false, LizenzType.Schwerer_Waffenschein, RPG.class));
		Bukkit.getPluginManager().registerEvents(new Rocket(), WeaponSystem.plugin);
	}
}
