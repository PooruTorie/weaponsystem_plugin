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
import org.bukkit.util.Vector;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.muni.Muni;

public class Grenade extends Throwable implements Listener {

	public Grenade(Muni muni) {
		super(muni);
		
		Bukkit.getPluginManager().registerEvents(this, WeaponSystem.plugin);
	}
	
	public Grenade(Muni muni, int costs) {
		super(muni, costs);
	}
	
	protected void Throw(Player p) {
		ItemStack e = this.clone();
		e.setAmount(1);
		Item i = p.getWorld().dropItem(p.getLocation(), e);
		i.setVelocity(p.getEyeLocation().getDirection());
		i.setPickupDelay(9999999);
		
		Bukkit.getScheduler().runTaskLater(WeaponSystem.plugin, new Runnable() {
			
			@Override
			public void run() {
				Creeper c = (Creeper) i.getLocation().getWorld().spawnEntity(i.getLocation(), EntityType.CREEPER);
				c.setExplosionRadius(4);
				c.setAI(false);
				c.setCustomName("grenade");
				c.setMaxFuseTicks(0);
				i.remove();
			}
		}, 20*4);
		
		getMuni().removeItem(p.getInventory(), id);
		
		int a = getMuni().getMuniItems(p.getInventory());
		if (a == 0) {
			items.remove(id);
		}
	}
	
	@EventHandler
	private void onExplosion(EntityExplodeEvent e) {
		if (e.getEntity().getCustomName() != null) {
			if (e.getEntity().getCustomName().equals("grenade")) {
				for (Block b : e.blockList()) {
					if (new Random().nextInt(10) == 1) {
						Location l = e.getLocation();
						Entity f = l.getWorld().spawnFallingBlock(e.getLocation(), b.getType(), b.getData());
						if (f instanceof FallingBlock) {
							f.setCustomName("grenade");
							f.setVelocity(new Vector((Math.random()*2)-1, (Math.random()*2)-1, (Math.random()*2)-1));
						} else {
							f.remove();
						}
					}
				}
				e.setCancelled(true);
				e.getLocation().getWorld().spawnParticle(Particle.CLOUD, e.getLocation(), 1000, 1, 1, 1, 0.1);
			}
		}
	}
	
	@EventHandler
	private void onLand(EntityChangeBlockEvent e) {
		if (e.getEntity() != null) {
			if (e.getEntity().getCustomName() != null) {
				if (e.getEntity().getCustomName().equals("grenade")) {
					e.setCancelled(true);
					e.getBlock().getWorld().spawnParticle(Particle.BLOCK_CRACK, e.getBlock().getLocation(), 10, new MaterialData(((FallingBlock) e.getEntity()).getBlockId(), ((FallingBlock) e.getEntity()).getBlockData()));
					e.getEntity().remove();
				}
			}
		}
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e){
	    List<Entity> ents = e.getEntity().getNearbyEntities(5, 5, 5);
	    for(Entity ent : ents) {
	    	if (ent.getType() == EntityType.FALLING_BLOCK) {
	        	if (ent.getCustomName() != null) {
		        	if (ent.getCustomName().equals("grenade")) {
		        		e.getEntity().getWorld().spawnParticle(Particle.BLOCK_CRACK, e.getEntity().getLocation(), 10, new MaterialData(((FallingBlock) ent).getBlockId(), ((FallingBlock) ent).getBlockData()));
		        		e.getEntity().remove();
		        	}
	        	}
	    	}
	    }
	}
}
