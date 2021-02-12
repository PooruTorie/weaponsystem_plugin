package de.paul.weaponsystem.weapon;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.dyroxplays.revieve.objects.DeathPlayer;
import de.paul.weaponsystem.BlockCrack;
import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.armor.BulletVest;
import de.paul.weaponsystem.storages.PlayerWeapons;
import de.paul.weaponsystem.storages.Storage.StorageType;
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
						if (e.getHitEntity() instanceof Player) {
							Player hit = (Player) e.getHitEntity();
							if (!BulletVest.isOn.contains(hit.getUniqueId())) {
								hit.damage(damage);
								hit.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation(), 40, 0.1, 0.1, 0.1, 0, Material.REDSTONE_BLOCK.getNewData((byte) 0x00));
							} else {
								boolean is = BulletVest.isLastBlocked.get(hit.getUniqueId());
								if (is == true) {
									hit.damage(damage);
									hit.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation(), 40, 0.1, 0.1, 0.1, 0, Material.REDSTONE_BLOCK.getNewData((byte) 0x00));
								}
								BulletVest.isLastBlocked.put(hit.getUniqueId(), !is);
							}
						} else {
							((LivingEntity) e.getHitEntity()).damage(damage);
							p.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation(), 40, 0.1, 0.1, 0.1, 0, Material.REDSTONE_BLOCK.getNewData((byte) 0x00));
						}
					}
					if (e.getHitBlock() != null) {
						BlockCrack.crack(e.getHitBlock());
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation(), 40, 0.1, 0.1, 0.1, 0, BlockCrack.getParticle(e.getHitBlock()));
					}
				}
			}
		}
	}
	
	@EventHandler
	private void onShot(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		if (!DeathPlayer.isDead(p)) {
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
			} else {
				if (e.getHand() == EquipmentSlot.HAND) {
					if (item != null) {
						if (item.hasItemMeta()) {
							if (item.getItemMeta().hasLocalizedName()) {
								int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
								if (WeaponItem.items.containsKey(id)) {
									WeaponItem itemWeapon = WeaponItem.items.get(id);
									if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
										itemWeapon.showHelp(p);
										e.setCancelled(true);
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
	private void onZoom(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand();
		if (item != null) {
			if (item.hasItemMeta()) {
				if (item.getItemMeta().hasLocalizedName()) {
					int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
					if (WeaponItem.items.containsKey(id)) {
						WeaponItem itemWeapon = WeaponItem.items.get(id);
						if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
							if (itemWeapon.getWeapon().isGunZoom()) {
								if (e.isSneaking()) {
									p.setWalkSpeed(-1f);
								} else {
									p.setWalkSpeed(0.2f);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	private void onScroll(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItem(e.getNewSlot());
		if (item != null) {
			if (item.hasItemMeta()) {
				if (item.getItemMeta().hasLocalizedName()) {
					int id = Integer.parseInt(item.getItemMeta().getLocalizedName().split("[_]")[1]);
					if (WeaponItem.items.containsKey(id)) {
						WeaponItem itemWeapon = WeaponItem.items.get(id);
						if (itemWeapon.getWeapon().getType() == WeaponType.gun) {
							if (itemWeapon.getWeapon().isGunZoom()) {
								if (p.isSneaking()) {
									p.setWalkSpeed(-1f);
								}
								return;
							}
						}
					}
				}
			}
		}
		p.setWalkSpeed(0.2f);
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
								if (!PlayerWeapons.getForPlayer(damager).isBlocked()) {
									if (damager.getCooldown(item.getType()) == 0) {
										e.setDamage(itemWeapon.getWeapon().getMeleeDamage());
										damager.setCooldown(item.getType(), (int) (itemWeapon.getWeapon().getCooldown()*20));
									}
								} else {
									damager.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
									e.setCancelled(true);
								}
							}
						}
					}
				}
			}
		}
	}
	
}
