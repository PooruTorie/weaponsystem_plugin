package de.paul.weaponsystem.weapon.throwable;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.weapon.muni.Muni;

public class SmokeGrenade extends Throwable implements Listener {

	public SmokeGrenade(Muni muni) {
		super(muni);
		
		Bukkit.getPluginManager().registerEvents(this, WeaponSystem.plugin);
	}
	
	public SmokeGrenade(Muni muni, int costs) {
		super(muni, costs);
	}
	
	protected void Throw(Player p) {
		ItemStack e = this.clone();
		Bukkit.getScheduler().runTask(WeaponSystem.plugin, new Runnable() {
			
			int task;
			
			@Override
			public void run() {
				e.setAmount(1);
				Item i = p.getWorld().dropItem(p.getLocation(), e);
				i.setVelocity(p.getEyeLocation().getDirection().add(p.getVelocity()).add(new Vector(0, 1, 0)));
				i.setPickupDelay(9999999);
				
				task = Bukkit.getScheduler().runTaskTimer(WeaponSystem.plugin, new Runnable() {
					
					int j = 0;
					
					@Override
					public void run() {
						i.getWorld().spawnParticle(Particle.SMOKE_NORMAL, i.getLocation(), 10, 0.1, 0.1, 0.1, 0.05);
						j++;
						if (j >= 5*20 && j%10 == 0) {
							i.getWorld().spawnParticle(Particle.SMOKE_LARGE, i.getLocation(), 1000, 1, 1, 1, 0.15);
							for (Entity ent : i.getWorld().getNearbyEntities(i.getLocation(), 6, 6, 6)) {
								if (ent instanceof Player) {
									Player p = (Player) ent;
									p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2*20, 3, false, false), true);
								}
							}
							i.remove();
							WeaponSystem.playSound(i.getLocation(), "minecraft:weapon.explosion", 1, 1);
						}
						
						if (j == 20*18) {
							Bukkit.getScheduler().cancelTask(task);
						}
					}
				}, 0, 1).getTaskId();
				
				getMuni().removeItem(p.getInventory(), id);
			}
		});
	}
}
