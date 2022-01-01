package de.paul.weaponsystem.weapon.throwable;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.weapon.muni.Muni;

public class Molotov extends Throwable implements Listener {

	public Molotov(Muni muni) {
		super(muni);
		
		Bukkit.getPluginManager().registerEvents(this, WeaponSystem.plugin);
	}
	
	public Molotov(Muni muni, int costs) {
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
					
					@Override
					public void run() {
						i.getWorld().spawnParticle(Particle.FLAME, i.getLocation(), 4, 0.1, 0.1, 0.1, 0.01);
						
						if (i.isOnGround()) {
							i.getWorld().spawnParticle(Particle.FLAME, i.getLocation(), 1000, 1, 1, 1, 0.01);
							
							for (Entity ent : i.getWorld().getNearbyEntities(i.getLocation(), 2, 2, 2)) {
								ent.setFireTicks(20*20);
							}
							
							WeaponSystem.playSound(i.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
							i.remove();
							Bukkit.getScheduler().cancelTask(task);
						}
					}
				}, 0, 1).getTaskId();
				
				getMuni().removeItem(p.getInventory(), id);
			}
		});
	}
}
