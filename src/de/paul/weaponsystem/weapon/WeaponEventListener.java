package de.paul.weaponsystem.weapon;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.dyroxplays.revieve.objects.DeathPlayer;
import de.paul.weaponsystem.BlockCrack;
import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.armor.BulletVest;
import de.paul.weaponsystem.storages.PlayerWeapons;
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
							if (!hit.isBlocking()) {
								if (!BulletVest.isOn.contains(hit.getUniqueId())) {
									hit.damage(damage);
									hit.setVelocity(p.getVelocity().normalize().multiply(.4));
									hit.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation(), 40, 0.1, 0.1, 0.1, 0, Material.REDSTONE_BLOCK.getNewData((byte) 0x00));
								} else {
									boolean is = BulletVest.isLastBlocked.get(hit.getUniqueId());
									if (is == true) {
										hit.damage(damage);
										hit.setVelocity(p.getVelocity().normalize().multiply(.4));
										hit.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation(), 40, 0.1, 0.1, 0.1, 0, Material.REDSTONE_BLOCK.getNewData((byte) 0x00));
									} else {
										WeaponSystem.playSound(hit.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 4, 1);
										
										ItemStack item = hit.getEquipment().getLeggings();
										item.setDurability((short) (item.getDurability()+1));
										hit.getEquipment().setLeggings(item);
									}
									BulletVest.isLastBlocked.put(hit.getUniqueId(), !is);
								}
							} else {
								double dot = hit.getLocation().getDirection().dot(p.getVelocity().normalize());
								if (Math.abs(dot) > 0.4d && dot < 0) {
									WeaponSystem.playSound(hit.getLocation(), Sound.ITEM_SHIELD_BLOCK, 4, 1);
									
									ItemStack item = hit.getEquipment().getItemInMainHand();
									if (item.getType() == Material.SHIELD) {
										item.setDurability((short) (item.getDurability()+1));
										hit.getEquipment().setItemInMainHand(item);
									} else {
										item = hit.getEquipment().getItemInOffHand();
										if (item.getType() == Material.SHIELD) {
											item.setDurability((short) (item.getDurability()+1));
											hit.getEquipment().setItemInOffHand(item);
										}
									}
								} else {
									if (!BulletVest.isOn.contains(hit.getUniqueId())) {
										hit.damage(damage);
										hit.setVelocity(p.getVelocity().normalize().multiply(.4));
										hit.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation(), 40, 0.1, 0.1, 0.1, 0, Material.REDSTONE_BLOCK.getNewData((byte) 0x00));
									} else {
										boolean is = BulletVest.isLastBlocked.get(hit.getUniqueId());
										if (is == true) {
											hit.damage(damage);
											hit.setVelocity(p.getVelocity().normalize().multiply(.4));
											hit.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation(), 40, 0.1, 0.1, 0.1, 0, Material.REDSTONE_BLOCK.getNewData((byte) 0x00));
										} else {
											WeaponSystem.playSound(hit.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 4, 1);
											
											ItemStack item = hit.getEquipment().getLeggings();
											item.setDurability((short) (item.getDurability()+1));
											hit.getEquipment().setLeggings(item);
										}
										BulletVest.isLastBlocked.put(hit.getUniqueId(), !is);
									}
								}
							}
						} else {
							((LivingEntity) e.getHitEntity()).damage(damage);
							((LivingEntity) e.getHitEntity()).setVelocity(p.getVelocity().normalize().multiply(.4));
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
				if (e.getHand() == EquipmentSlot.HAND || e.getHand() == EquipmentSlot.OFF_HAND) {
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
										e.setCancelled(itemWeapon.getWeapon().getItemID() != 442);
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
									damager.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("block"));
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
