package de.paul.weaponsystem.shop;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ShopKeeperEventListener implements Listener {
	
	@EventHandler
	private void onClick(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		Entity ent = e.getRightClicked();
		if (ent != null) {
			if (ShopKeeper.shopKepperEnitys.containsKey(ent)) {
				if (e.getHand() == EquipmentSlot.HAND) {
					ShopKeeper.shopKepperEnitys.get(ent).openInv(p);
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	private void onDamage(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		if (ShopKeeper.shopKepperEnitys.containsKey(ent)) {
			if (e.getCause() != DamageCause.VOID) {
				e.setCancelled(true);
			}
		}
	}
	
}
