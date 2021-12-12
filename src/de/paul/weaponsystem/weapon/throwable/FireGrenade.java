package de.paul.weaponsystem.weapon.throwable;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
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

public class FireGrenade extends Throwable implements Listener {

	public FireGrenade(Muni muni) {
		super(muni);
		
		Bukkit.getPluginManager().registerEvents(this, WeaponSystem.plugin);
	}
	
	public FireGrenade(Muni muni, int costs) {
		super(muni, costs);
	}
	
	protected void Throw(Player p) {
		ItemStack e = this.clone();
		e.setAmount(1);
		Item i = p.getWorld().dropItem(p.getLocation(), e);
		i.setVelocity(p.getEyeLocation().getDirection().add(p.getVelocity()).add(new Vector(0, 1, 0)));
		i.setPickupDelay(9999999);
		
		Bukkit.getScheduler().runTaskLater(WeaponSystem.plugin, new Runnable() {
			
			@Override
			public void run() {
				i.getWorld().spawnParticle(Particle.FLAME, i.getLocation(), 1000, 0.4, 0.4, 0.4, 0.08);
				for (Entity ent : i.getWorld().getNearbyEntities(i.getLocation(), 8, 8, 8)) {
					ent.setFireTicks(20*20);
				}
				i.remove();
			}
		}, 20*2);
		
		getMuni().removeItem(p.getInventory(), id);
		
		int a = getMuni().getMuniItems(p.getInventory());
		if (a == 0) {
			items.remove(id);
		}
	}
}
