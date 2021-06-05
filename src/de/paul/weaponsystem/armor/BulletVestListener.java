package de.paul.weaponsystem.armor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.dyroxplays.revieve.objects.PlayerRealDeathEvent;
import de.paul.weaponsystem.weapon.WeaponItem;

public class BulletVestListener implements Listener {
	
	@EventHandler
	private void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Inventory inv = e.getClickedInventory();
		if (p.getInventory().equals(inv)) {
			if (e.getSlotType() == SlotType.ARMOR) {
				if (e.getRawSlot() == 7) {
					WeaponItem w = WeaponItem.getWeaponByItem(e.getCurrentItem());
					if (w != null) {
						if (w.getWeapon().getName().equals("bulletvest")) {
							e.setCancelled(true);
							p.getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
							BulletVest.isOn.remove(p.getUniqueId());
						}
					}
					w = WeaponItem.getWeaponByItem(e.getCursor());
					if (w != null) {
						if (w.getWeapon().getName().equals("bulletvest")) {
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	private void onDeath(PlayerRealDeathEvent e) {
		Player p = e.getDeathPlayer().getPlayer();
		BulletVest.isOn.remove(p.getUniqueId());
	}
}
