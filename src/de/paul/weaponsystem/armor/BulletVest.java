package de.paul.weaponsystem.armor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.dyroxplays.revieve.lizenz.Lizenz.LizenzType;
import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.crates.Crate;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.WeaponItem;
import de.paul.weaponsystem.weapon.Weapon.WeaponType;

public class BulletVest extends WeaponItem {
	
	public static HashMap<UUID, Boolean> isLastBlocked = new HashMap<>();
	public static ArrayList<UUID> isOn = new ArrayList<>();
	
	public BulletVest(Weapon weapon) {
		super(weapon);
	}
	
	public BulletVest(Weapon weapon, Crate c) {
		super(weapon, c);
	}
	
	public BulletVest(Weapon weapon, int id, int magazin) {
		super(weapon, id, magazin);
	}
	
	public BulletVest(Weapon weapon, int id, int magazin, int costs) {
		super(weapon, id, magazin, costs);
	}
	
	@Override
	public void gunReleod(ItemStack item, Player p) {
	}
	
	@Override
	public void gunShot(Player p) {
		p.setItemInHand(null);
		p.getEquipment().setLeggings(this);
		
		isOn.add(p.getUniqueId());
		isLastBlocked.put(p.getUniqueId(), false);
	}
	
	@Override
	public void remove(Player p) {
		super.remove(p);
		
		isOn.remove(p.getUniqueId());
		isLastBlocked.remove(p.getUniqueId());
	}
	
	public static void register() {
		Weapon.register(new Weapon(WeaponType.gun, "bulletvest", "§9Schutzweste", 304, 0, 0, 0, 1500, false, false, LizenzType.Leichter_Waffenschein, BulletVest.class));
		
		Bukkit.getPluginManager().registerEvents(new BulletVestListener(), WeaponSystem.plugin);
	}
}
