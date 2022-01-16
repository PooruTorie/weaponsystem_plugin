package de.paul.weaponsystem.crates;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.paul.weaponsystem.weapon.Weapon;

public class CrateEventListener implements Listener {
	
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getClickedInventory() != null) {
			if (Crate.invs.containsKey(p.getUniqueId())) {
				if (Crate.invs.get(p.getUniqueId()).equals(e.getClickedInventory())) {
					if (e.getCurrentItem() != null) {
						e.setCancelled(true);
						if (!e.getCurrentItem().equals(Crate.none)) {
							Crate c = Crate.open.get(p.getUniqueId());
							ItemStack item = e.getCurrentItem();
							if (item != null) {
								Weapon w = Weapon.getWeaponByName(item.getItemMeta().getLocalizedName().split("[_]")[0]);
								if (w != null) {
									if (c.playerHasWeapon(p, w)) {
										return;
									} else {
										w.give(p, c);
										return;
									}
								}
								
								p.getInventory().addItem(item);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	private void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (Crate.invs.containsKey(p.getUniqueId())) {
			Crate.invs.remove(p.getUniqueId());
			Crate.open.remove(p.getUniqueId());
		}
	}
	
	@EventHandler
	private void onClick(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		Entity ent = e.getRightClicked();
		if (ent != null) {
			if (Crate.crateEnitys.containsKey(ent)) {
				if (e.getHand() == EquipmentSlot.HAND) {
					Crate.crateEnitys.get(ent).openInv(p);
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	private void onDamage(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		if (Crate.crateEnitys.containsKey(ent)) {
			if (e.getCause() != DamageCause.VOID) {
				e.setCancelled(true);
			}
		}
	}
}
