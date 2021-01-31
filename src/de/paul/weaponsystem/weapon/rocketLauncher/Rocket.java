package de.paul.weaponsystem.weapon.rocketLauncher;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import de.paul.weaponsystem.WeaponSystem;

public class Rocket implements Listener {

	public static void shot(Player p) {
		Fireball ball = p.launchProjectile(Fireball.class);
		ball.setCustomName("rocket");
		ball.setYield(10);
		Wolf rocket = (Wolf) ball.getLocation().getWorld().spawnEntity(ball.getLocation(), EntityType.WOLF);
		rocket.setAdult();
		rocket.setAI(false);
		rocket.setAgeLock(true);
		rocket.setInvulnerable(true);
		rocket.setSilent(true);
		ball.setPassenger(rocket);
	}
	
	public static void spawn(Location l) {
		double pitch = ((l.getPitch() + 90) * Math.PI) / 180;
        double yaw = ((l.getYaw() + 90) * Math.PI) / 180;
		
		double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        Vector vector = new Vector(x/2, z/2, y/2);
		Fireball ball = l.getWorld().spawn(l, Fireball.class);
		ball.setCustomName("rocket");
		ball.setYield(4);
		ball.setVelocity(vector);
	}
	
	@EventHandler
	private void onHit(ProjectileHitEvent e) {
		Projectile p = e.getEntity();
		if (p.getCustomName() != null) {
			if (p.getCustomName().equals("rocket")) {
				if (p.getPassenger() != null) {
					p.getPassenger().remove();
				}
				p.remove();
			}
		}
	}
	
	@EventHandler
	private void onExplosion(EntityExplodeEvent e) {
		if (e.getEntity().getCustomName() != null) {
			if (e.getEntity().getCustomName().equals("rocket")) {
				for (Block b : e.blockList()) {
					if (new Random().nextInt(10) == 1) {
						Location l = e.getLocation();
						Entity f = l.getWorld().spawnFallingBlock(e.getLocation(), b.getType(), b.getData());
						if (f instanceof FallingBlock) {
							f.setCustomName("rocket");
							f.setVelocity(new Vector((Math.random()*2)-1, (Math.random()*2)-1, (Math.random()*2)-1));
						} else {
							f.remove();
						}
					}
				}
				e.setCancelled(true);
				e.getLocation().getWorld().spawnParticle(Particle.CLOUD, e.getLocation(), 4000, 2, 2, 2, 0.3);
			}
		}
	}
	
	@EventHandler
	private void onLand(EntityChangeBlockEvent e) {
		if (e.getEntity().getCustomName() != null) {
			if (e.getEntity().getCustomName().equals("rocket")) {
				e.setCancelled(true);
				e.getBlock().getWorld().spawnParticle(Particle.BLOCK_CRACK, e.getBlock().getLocation(), 10, new MaterialData(((FallingBlock) e.getEntity()).getBlockId(), ((FallingBlock) e.getEntity()).getBlockData()));
				e.getEntity().remove();
			}
		}
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e){
	    List<Entity> ents = e.getEntity().getNearbyEntities(5, 5, 5);
	    for(Entity ent : ents) {
	    	if (ent.getType() == EntityType.FALLING_BLOCK) {
	        	if (ent.getCustomName() != null) {
		        	if (ent.getCustomName().equals("rocket")) {
		        		e.getEntity().getWorld().spawnParticle(Particle.BLOCK_CRACK, e.getEntity().getLocation(), 10, new MaterialData(((FallingBlock) ent).getBlockId(), ((FallingBlock) ent).getBlockData()));
		        		e.getEntity().remove();
		        	}
	        	}
	    	}
	    }
	}
}
