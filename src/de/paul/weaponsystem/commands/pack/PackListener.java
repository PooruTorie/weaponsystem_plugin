package de.paul.weaponsystem.commands.pack;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.HashBiMap;

import de.paul.weaponsystem.WeaponSystem;

public class PackListener implements Listener {

	public static HashBiMap<Player, Player> packedPlayer = HashBiMap.create();
	
	public PackListener() {
		Bukkit.getScheduler().runTaskTimer(WeaponSystem.plugin, new Runnable() {
			
			@Override
			public void run() {
				for (Player p : packedPlayer.values()) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, false, false), true);
				}
			}
		}, 0, 20);
	}
	
	@EventHandler
	private void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		
		if (packedPlayer.containsKey(p)) {
			Player other = packedPlayer.get(p);
			if (other.getWorld().equals(p.getWorld())) {
				if (other.getLocation().distanceSquared(e.getTo()) >= 2) {
					other.setVelocity(p.getLocation().toVector().subtract(other.getLocation().toVector()).multiply(0.5f));
				}
			}
		}
		if (packedPlayer.containsValue(p)) {
			Player master = packedPlayer.inverse().get(p);
			if (master.getWorld().equals(p.getWorld())) {
				if (master.getLocation().distanceSquared(e.getFrom()) < master.getLocation().distanceSquared(e.getTo())) {
					e.setTo(e.getFrom());
				}
			}
		}
	}
	
}
