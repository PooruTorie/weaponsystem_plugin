package de.paul.weaponsystem.weapon.throwable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.storages.PlayerWeapons;

public class ThrowableEventListener implements Listener {
	
	@EventHandler
	private void onThrow(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getHand() == EquipmentSlot.HAND) {
				if (item != null) {
					if (item.hasItemMeta()) {
						if (item.getItemMeta().hasLocalizedName()) {
							int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
							if (Throwable.items.containsKey(id)) {
								if (!PlayerWeapons.getForPlayer(p).isBlocked()) {
									Throwable.items.get(id).Throw(p);
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
