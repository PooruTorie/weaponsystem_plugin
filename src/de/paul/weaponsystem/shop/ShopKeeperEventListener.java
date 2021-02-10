package de.paul.weaponsystem.shop;

import java.text.DecimalFormat;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import de.dyroxplays.revieve.RevieveAPI;
import de.dyroxplays.revieve.lizenz.Lizenz;
import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.storages.PlayerWeapons;
import de.paul.weaponsystem.storages.Storage;
import de.paul.weaponsystem.storages.Storage.StorageType;
import de.paul.weaponsystem.weapon.Weapon;
import de.paul.weaponsystem.weapon.muni.Muni;
import net.minecraft.server.v1_12_R1.ICommandHandler;

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
	
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getClickedInventory() != null) {
			if (ShopKeeper.invs.containsKey(p.getUniqueId())) {
				if (ShopKeeper.invs.get(p.getUniqueId()).equals(e.getClickedInventory())) {
					if (e.getCurrentItem() != null) {
						if (e.getCurrentItem().equals(ShopKeeper.none)) {
							e.setCancelled(true);
						} else {
							e.setCancelled(true);
							ItemStack item = e.getCurrentItem();
							
							if (item.hasItemMeta()) {
								int costs = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[2]);
								double balance = WeaponSystem.economy.getBalance(p);
							
								Weapon w = Weapon.getWeaponByName(item.getItemMeta().getLocalizedName().split("[_]")[0]);
								if (w != null) {
									if (!PlayerWeapons.getForPlayer(p).hasWeapon(w)) {
										if (w.hasLicense(Lizenz.getLicenses(p))) {
											if (balance >= costs) {
												if (!w.getName().equals("bulletvest")) {
													PlayerWeapons.getForPlayer(p).buy(w);
												} else {
													w.give(p, Storage.StorageType.weapon.getStorage());
												}
												
												WeaponSystem.economy.depositPlayer(p, costs*-1);
												Inventory i = e.getClickedInventory();
												Inventory n = Bukkit.createInventory(p, i.getSize(), i.getName().split("[|]")[0]+"| §e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(WeaponSystem.economy.getBalance(p))+"$");
												n.setContents(i.getContents());
												p.openInventory(n);
												ShopKeeper.invs.put(p.getUniqueId(), n);
											} else {
												p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nomoney").replace("%money%", "§e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(costs-balance)+"$"));
											}
										} else {
											p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nolicense"));
										}
									} else {
										p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("hasweapon"));
									}
								} else {
									if (balance >= costs) {
										Muni m = Muni.getMuniByName(item.getItemMeta().getLocalizedName().split("[_]")[0]);
										m.give(p);
										
										WeaponSystem.economy.depositPlayer(p, costs*-1);
										Inventory i = e.getClickedInventory();
										Inventory n = Bukkit.createInventory(p, i.getSize(), i.getName().split("[|]")[0]+"| §e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(WeaponSystem.economy.getBalance(p))+"$");
										n.setContents(i.getContents());
										p.openInventory(n);
										ShopKeeper.invs.put(p.getUniqueId(), n);
									} else {
										p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nomoney").replace("%money%", "§e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(costs-balance)+"$"));
									}
								}
							}
						}
					}
				}
				if (ShopKeeper.invs.get(p.getUniqueId()).equals(e.getInventory())) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	private void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (ShopKeeper.invs.containsKey(p.getUniqueId())) {
			ShopKeeper.invs.remove(p.getUniqueId());
		}
	}
}
