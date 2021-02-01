package de.paul.weaponsystem.storages;

import org.bukkit.Bukkit;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.muni.Muni;

public class StorageEventListener implements Listener {
	
	@EventHandler
	private void onClick(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		Entity ent = e.getRightClicked();
		if (ent != null) {
			if (Storage.storagesEnitys.containsKey(ent)) {
				if (e.getHand() == EquipmentSlot.HAND) {
					Storage.storagesEnitys.get(ent).openInv(p);
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	private void onDamage(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		if (Storage.storagesEnitys.containsKey(ent)) {
			if (e.getCause() != DamageCause.VOID) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getClickedInventory() != null) {
			if (Storage.invs.containsKey(p.getUniqueId())) {
				if (Storage.invs.get(p.getUniqueId()).equals(e.getClickedInventory())) {
					if (e.getCurrentItem() != null) {
						if (e.getCurrentItem().equals(Storage.none)) {
							e.setCancelled(true);
						} else {
							e.setCancelled(true);
							ItemStack item = e.getCurrentItem();
							if (item != null) {
								Storage storage = Storage.invStorages.get(p.getUniqueId());
								Weapon w = Weapon.getWeaponByName(item.getItemMeta().getLocalizedName().split("[_]")[0]);
								if (w != null) {
									if (!storage.playerHasWeapon(p, w)) {
										w.give(p, storage);
									}
								} else {
									Muni m = Muni.getMuniByName(item.getItemMeta().getLocalizedName().split("[_]")[0]);
									m.give(p);
								}
								Inventory i = e.getClickedInventory();
								Inventory n = Bukkit.createInventory(p, i.getSize(), i.getName().split("[|]")[0]);
								n.setContents(i.getContents());
								p.openInventory(n);
								Storage.invs.put(p.getUniqueId(), n);
								Storage.invStorages.put(p.getUniqueId(), storage);
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
		if (Storage.invs.containsKey(p.getUniqueId())) {
			Storage.invs.remove(p.getUniqueId());
			Storage.invStorages.remove(p.getUniqueId());
		}
	}
}
