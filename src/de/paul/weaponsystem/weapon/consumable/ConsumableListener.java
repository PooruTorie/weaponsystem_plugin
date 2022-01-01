package de.paul.weaponsystem.weapon.consumable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.storages.PlayerWeapons;

public class ConsumableListener implements Listener {

	@EventHandler
	private void onClick(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getEquipment().getItemInMainHand();
		if (e.getHand() == EquipmentSlot.HAND) {
			if (e.getRightClicked() != null) {
				if (e.getRightClicked() instanceof Player) {
					Player clicked = (Player) e.getRightClicked();
					if (item != null) {
						if (item.hasItemMeta()) {
							if (item.getItemMeta().hasLocalizedName()) {
								if (item.getItemMeta().getLocalizedName().contains("_")) {
									int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
									if (Consumable.items.containsKey(id)) {
										if (!PlayerWeapons.getForPlayer(p).isBlocked()) {
											Consumable.items.get(id).Use(p, clicked);
										} else {
											p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("block"));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
}
