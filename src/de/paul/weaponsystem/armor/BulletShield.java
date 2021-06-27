package de.paul.weaponsystem.armor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.dyroxplays.revieve.lizenz.Lizenz.LizenzType;
import de.paul.weaponsystem.crates.Crate;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.WeaponItem;
import de.paul.weaponsystem.weapon.Weapon.WeaponType;

public class BulletShield extends WeaponItem {
	
	public static HashMap<UUID, Boolean> isLastBlocked = new HashMap<>();
	public static ArrayList<UUID> isOn = new ArrayList<>();
	
	public BulletShield(Weapon weapon) {
		super(weapon);
	}
	
	public BulletShield(Weapon weapon, Crate c) {
		super(weapon, c);
	}
	
	public BulletShield(Weapon weapon, int id, int magazin) {
		super(weapon, id, magazin);
	}
	
	public BulletShield(Weapon weapon, int id, int magazin, int costs) {
		super(weapon, id, magazin, costs);
	}
	
	@Override
	public void gunReleod(ItemStack item, Player p) {}
	
	@Override
	public void gunShot(Player p) {}
	
	@Override
	public void remove(Player p) {}
	
	public static void register() {
		Weapon.register(new Weapon(WeaponType.gun, "bulletshield", "§8S.W.A.T Schild", 442, 0, 0, 0, 1500, false, false, LizenzType.Leichter_Waffenschein, BulletShield.class));
	}
}