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

public class FlashBang extends Throwable implements Listener {

	public FlashBang(Muni muni) {
		super(muni);
		
		Bukkit.getPluginManager().registerEvents(this, WeaponSystem.plugin);
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
				i.getWorld().spawnParticle(Particle.END_ROD, i.getLocation(), 1000, 0.4, 0.4, 0.4, 0.4);
				for (Entity ent : i.getWorld().getNearbyEntities(i.getLocation(), 14, 14, 14)) {
					if (ent instanceof Player) {
						Player p = (Player) ent;
						p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 7*20, 3, false, false), true);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 7*20, 2, false, false), true);
					}
				}
				WeaponSystem.playSound(i.getLocation(), "minecraft:weapon.explosion", 1, 1);
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
