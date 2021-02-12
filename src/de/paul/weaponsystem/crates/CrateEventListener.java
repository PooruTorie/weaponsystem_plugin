package de.paul.weaponsystem.crates;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.paul.weaponsystem.WeaponSystem;
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
	private void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getHand() == EquipmentSlot.HAND) {
				if (e.getClickedBlock() != null) {
					Block b = e.getClickedBlock();
					if (Crate.placedCrates.containsKey(b.getLocation())) {
						Crate.placedCrates.get(b.getLocation()).openInv(p);
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	private void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if (Crate.placedCrates.containsKey(b.getLocation())) {
			Crate c = Crate.placedCrates.get(b.getLocation());
			if (p.hasPermission(c.getPermission())) {
				b.setType(Material.AIR);
				Crate.placedCrates.remove(b.getLocation());
			} else {
				p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
				e.setCancelled(true);
			}
		}
	}
	
}
