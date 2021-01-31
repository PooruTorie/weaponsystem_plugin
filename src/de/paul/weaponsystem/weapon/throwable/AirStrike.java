package de.paul.weaponsystem.weapon.throwable;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.weapon.muni.Muni;
import de.paul.weaponsystem.weapon.rocketLauncher.Rocket;

public class AirStrike extends Throwable implements Listener {

	public AirStrike(Muni muni) {
		super(muni);
		
		Bukkit.getPluginManager().registerEvents(this, WeaponSystem.plugin);
	}
	
	public AirStrike(Muni muni, int costs) {
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
				i.setVelocity(p.getEyeLocation().getDirection());
				i.setPickupDelay(9999999);
				
				task = Bukkit.getScheduler().runTaskTimer(WeaponSystem.plugin, new Runnable() {
					
					int j = 0;
					
					@Override
					public void run() {
						i.getWorld().spawnParticle(Particle.REDSTONE, i.getLocation(), (int) ((8f*20f)/100f*j), (1f/(8f*20f))*j, (4f/(8f*20f))*j, (1f/(8f*20f))*j, 0);
						j++;
						if (j == 8*20) {
							i.getWorld().spawnParticle(Particle.FLAME, i.getLocation(), 100, 0, 0, 0, 0.05);
							
							Location l = i.getLocation().getBlock().getLocation();
							l.setPitch(90);
							Random r = new Random();
							for (int j = 0; j < 20; j++) {
								Bukkit.getScheduler().runTaskLater(WeaponSystem.plugin, new Runnable() {
									
									@Override
									public void run() {
										Rocket.spawn(l.clone().add(r.nextInt(20)-10, 60, r.nextInt(20)-10));
									}
								}, 4*j);
							}
							
							i.remove();
							Bukkit.getScheduler().cancelTask(task);
						}
					}
				}, 0, 1).getTaskId();
				
				getMuni().removeItem(p.getInventory(), id);
				
				int a = getMuni().getMuniItems(p.getInventory());
				if (a == 0) {
					items.remove(id);
				}
			}
		});
	}
}
