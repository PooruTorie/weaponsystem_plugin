package de.paul.weaponsystem.weapon;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.paul.weaponsystem.weapon.Weapon.WeaponType;

public class WeaponEventListener implements Listener {
	
	@EventHandler
	private void onHit(ProjectileHitEvent e) {
		Projectile p = e.getEntity();
		if (p instanceof Snowball) {
			String name = p.getCustomName();
			if (name != null) {
				if (name.contains("_")) {
					int damage = Integer.parseInt(name.split("[_]")[1]);
					if (e.getHitEntity() instanceof LivingEntity) {
						((LivingEntity) e.getHitEntity()).damage(damage);
					}
				}
			}
		}
	}
	
	@EventHandler
	private void onShot(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getHand() == EquipmentSlot.HAND) {
				if (item != null) {
					if (item.hasItemMeta()) {
						if (item.getItemMeta().hasLocalizedName()) {
							int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
							if (WeaponItem.items.containsKey(id)) {
								WeaponItem itemWeapon = WeaponItem.items.get(id);
								if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
									if (p.getCooldown(item.getType()) == 0) {
										p.setCooldown(item.getType(), (int) (itemWeapon.getWeapon().getCooldown()*20f));
										itemWeapon.gunShot(p);
									}
									e.setCancelled(true);
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	private void onDrop(PlayerDropItemEvent e) {
		ItemStack item = e.getItemDrop().getItemStack();
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasLocalizedName()) {
				int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
				if (WeaponItem.items.containsKey(id)) {
					WeaponItem itemWeapon = WeaponItem.items.get(id);
					if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
						itemWeapon.gunReleod(item, e.getPlayer());
					}
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	private void onHandSwitch(PlayerSwapHandItemsEvent e) {
		ItemStack item = e.getOffHandItem();
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasLocalizedName()) {
				int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
				if (WeaponItem.items.containsKey(id)) {
					WeaponItem itemWeapon = WeaponItem.items.get(id);
					if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
						itemWeapon.gunReleod(item, e.getPlayer());
					}
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	private void onHit(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player damager = (Player) e.getDamager();
			ItemStack item = damager.getItemInHand();
			if (item != null) {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasLocalizedName()) {
						int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
						if (WeaponItem.items.containsKey(id)) {
							WeaponItem itemWeapon = WeaponItem.items.get(id);
							if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
								e.setDamage(0);
							} else {
								if (damager.getCooldown(item.getType()) == 0) {
									e.setDamage(itemWeapon.getWeapon().getMeleeDamage());
									damager.setCooldown(item.getType(), (int) (itemWeapon.getWeapon().getCooldown()*20));
								}
							}
						}
					}
				}
			}
		}
	}
	
}
