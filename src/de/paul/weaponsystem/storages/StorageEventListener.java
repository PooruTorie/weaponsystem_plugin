package de.paul.weaponsystem.storages;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.storages.Storage.StorageType;
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
					if (e.getClickedInventory().getName().equals(StorageType.muni.getName())) {
						if (e.getCurrentItem() != null) {
							ItemStack item = e.getCurrentItem();
							e.setCancelled(true);
							if (item != null) {
								if (item.hasItemMeta()) {
									if (item.getItemMeta().hasLocalizedName()) {
										if (Muni.getMuniByName(item.getItemMeta().getLocalizedName().split("[_]")[0]) != null) {
											e.setCancelled(false);
										}
									}
								}
							}
							if (e.getCursor() != null) {
								if (e.getCursor().hasItemMeta()) {
									if (e.getCursor().getItemMeta().hasLocalizedName()) {
										if (Muni.getMuniByName(e.getCursor().getItemMeta().getLocalizedName().split("[_]")[0]) != null) {
											e.setCancelled(false);
										}
									}
								}
							}
						}
					} else if (e.getClickedInventory().getName().equals(StorageType.weapon.getName())) {
						if (e.getCurrentItem() != null) {
							ItemStack item = e.getCurrentItem();
							if (item.hasItemMeta()) {
								if (item.getItemMeta().hasLocalizedName()) {
									Weapon w = Weapon.getWeaponByName(item.getItemMeta().getLocalizedName().split("[_]")[0]);
									if (w != null) {
										if (!StorageType.weapon.getStorage().playerHasWeapon(p, w)) {
											if (!PlayerWeapons.getForPlayer(p).isBlocked()) {
												w.give(p, StorageType.weapon.getStorage());
											} else {
												p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
											}
										}
									}
								}
							}
							e.setCancelled(true);
						}
					}
				}
				if (Storage.invs.get(p.getUniqueId()).equals(e.getInventory())) {
					if (e.getInventory().getName().equals(StorageType.weapon.getName())) {
						e.setCancelled(true);
					} else {
						ItemStack item = e.getCurrentItem();
						e.setCancelled(true);
						if (item != null) {
							if (item.hasItemMeta()) {
								if (item.getItemMeta().hasLocalizedName()) {
									if (Muni.getMuniByName(item.getItemMeta().getLocalizedName().split("[_]")[0]) != null) {
										e.setCancelled(false);
									}
								}
							}
						}
						if (e.getCursor() != null) {
							if (e.getCursor().hasItemMeta()) {
								if (e.getCursor().getItemMeta().hasLocalizedName()) {
									if (Muni.getMuniByName(e.getCursor().getItemMeta().getLocalizedName().split("[_]")[0]) != null) {
										e.setCancelled(false);
									}
								}
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
			if (e.getInventory().getName().equals(StorageType.muni.getName())) {
				p.getEnderChest().setContents(Storage.toEnderChest(e.getInventory().getContents()));
			}
			Storage.invs.remove(p.getUniqueId());
		}
	}
}
