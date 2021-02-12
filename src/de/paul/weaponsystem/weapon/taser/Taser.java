package de.paul.weaponsystem.weapon.taser;

import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import de.dyroxplays.revieve.lizenz.Lizenz.LizenzType;
import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.crates.Crate;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.WeaponItem;
import de.paul.weaponsystem.weapon.Weapon.WeaponType;
import de.paul.weaponsystem.weapon.rocketLauncher.RPG;
import de.paul.weaponsystem.weapon.rocketLauncher.Rocket;

public class Taser extends WeaponItem {
	
	public Taser(Weapon weapon) {
		super(weapon);
	}
	
	public Taser(Weapon weapon, Crate c) {
		super(weapon, c);
	}
	
	public Taser(Weapon weapon, int id, int magazin) {
		super(weapon, id, magazin);
	}
	
	public Taser(Weapon weapon, int id, int magazin, int costs) {
		super(weapon, id, magazin, costs);
	}
	
	@Override
	public void gunReleod(ItemStack item, Player p) {
	}
	
	@Override
	public void gunShot(Player p) {
		if (p.getCooldown(getType()) <= 0) {
			WeaponSystem.playSound(p.getLocation(), "minecraft:taser.taser", 6, 1);
			p.setCooldown(getType(), 20*10);
			
			Player hit = drawLine(p, p.getEyeLocation(), p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(10)), 0.1);
			if (hit != null) {
				hit.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*10, 10, false, false), true);
				hit.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*10, 200, false, false), true);
				hit.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*10, 10, false, false), true);
			}
		}
		
	}
	
	public Player drawLine(Player shooter, Location point1, Location point2, double space) {
	    World world = point1.getWorld();
	    Validate.isTrue(point2.getWorld().equals(world), "Lines cannot be in different worlds!");
	    double distance = point1.distance(point2);
	    Vector p1 = point1.toVector();
	    Vector p2 = point2.toVector();
	    Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
	    double length = 0;
	    for (; length < distance; p1.add(vector)) {
	        world.spawnParticle(Particle.CRIT_MAGIC, p1.getX(), p1.getY(), p1.getZ(), 0);
	        length += space;
	        for (Entity ent : world.getNearbyEntities(p1.toLocation(world), 0.2, 0.3, 0.2)) {
				if (ent instanceof Player) {
					if (ent != shooter) {
						return (Player) ent;
					}
				}
			}
	    }
		return null;
	}
	
	public static void register() {
		Weapon.register(new Weapon(WeaponType.gun, "taser", "§3Taser", 369, 0, 0, 0, 0, false, LizenzType.Leichter_Waffenschein, Taser.class));
	}
}
