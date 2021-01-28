package de.paul.weaponsystem.weapon;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import de.paul.weaponsystem.weapon.Weapon.WeaponType;

public class WeaponItem extends ItemStack implements Listener {
	
	private static HashMap<Integer, WeaponItem> items = new HashMap<>();
	
	private Weapon weapon;

	public WeaponItem() {}
	
	public WeaponItem(Weapon weapon) {
		super(weapon.getItemID());
		this.weapon = weapon;
		
		int id = (int) (Math.random()*1000);
		
		ItemMeta m = getItemMeta();
		m.setDisplayName(weapon.getItemName());
		m.setLore(weapon.getItemLore());
		m.setUnbreakable(true);
		m.setLocalizedName(weapon.getName()+"_"+id);
		setItemMeta(m);
		
		items.put(id, this);
	}
	
	public Weapon getWeapon() {
		return weapon;
	}
	
	private void gunReleod(Player p) {
		p.sendMessage("Gun Reload Test");
	}
	
	private void gunShot(Player p) {
		p.playSound(p.getLocation(), "minecraft:weapon.blast1", 50, (float) (1f+(Math.random()/10f)));
	}
	
	@EventHandler
	private void onShot(PlayerInteractEvent e) {
		ItemStack item = e.getItem();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (item != null) {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasLocalizedName()) {
						int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
						if (items.containsKey(id)) {
							WeaponItem itemWeapon = items.get(id);
							if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
								itemWeapon.gunShot(e.getPlayer());
								e.setCancelled(true);
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
				if (items.containsKey(id)) {
					WeaponItem itemWeapon = items.get(id);
					if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
						itemWeapon.gunReleod(e.getPlayer());
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
				if (items.containsKey(id)) {
					WeaponItem itemWeapon = items.get(id);
					if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
						itemWeapon.gunReleod(e.getPlayer());
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
						if (items.containsKey(id)) {
							WeaponItem itemWeapon = items.get(id);
							if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
								e.setDamage(0);
							} else {
								if (damager.getCooldown(item.getType()) == 0) {
									e.setDamage(itemWeapon.getWeapon().getMeleeDamage());
									damager.setCooldown(item.getType(), itemWeapon.getWeapon().getCooldown()*20);
								}
							}
						}
					}
				}
			}
		}
	}
}
