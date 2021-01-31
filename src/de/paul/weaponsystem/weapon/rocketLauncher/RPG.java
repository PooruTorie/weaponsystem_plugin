package de.paul.weaponsystem.weapon.rocketLauncher;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.common.collect.Lists;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.crates.Crate;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.Weapon.WeaponType;
import de.paul.weaponsystem.weapon.WeaponItem;

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
	
	@Override
	public void gunShot(Player p) {
		if (magazin > 0) {
			WeaponSystem.playSound(p.getLocation(), "minecraft:weapon.blast1", 30, 1);
			Rocket.shot(p);
			magazin--;
		} else {
			WeaponSystem.playSound(p.getLocation(), "minecraft:weapon.empty", 5, 1);
		}
		showAmmo(p);
	}
	
	public static void register() {
		Weapon.register(new Weapon(WeaponType.gun, "rpg", "§8RPG", 286, Lists.newArrayList("§7RPG"), 1, 3, 4, RPG.class));
		Bukkit.getPluginManager().registerEvents(new Rocket(), WeaponSystem.plugin);
	}
}
