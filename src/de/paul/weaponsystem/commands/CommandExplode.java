package de.paul.weaponsystem.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.dyroxplays.revieve.objects.DeathPlayer;
import de.paul.weaponsystem.WeaponSystem;

public class CommandExplode implements TabCompleter, CommandExecutor, Listener {
	
	private static List<Player> explosion = new ArrayList<Player>();
	private static HashMap<UUID, Integer> delay = new HashMap<>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission((String) WeaponSystem.loadConfig("config", "permissions").get("explode"))) {
				if (!DeathPlayer.isDead(p)) {
					if (args.length > 0) {
						if (args[0].equalsIgnoreCase("info")) {
							p.sendMessage(WeaponSystem.prefix+"§7Eine Sprengung kostet §e5000$");
						} else {
							if (!delay.containsKey(p.getUniqueId())) {
								String dest = args[0];
								double balance = WeaponSystem.economy.getBalance(p);
								if (balance >= 5000) {
									Creeper c = (Creeper) p.getWorld().spawnEntity(p.getLocation(), EntityType.CREEPER);
									c.setExplosionRadius(20);
									c.setAI(false);
									c.setCustomName("bomb");
									c.setMaxFuseTicks(0);
									WeaponSystem.economy.withdrawPlayer(p, 5000);
									
									explosion.add(p);
									
									for (Entity e : p.getNearbyEntities(30, 30, 30)) {
										if (e.getType() == EntityType.PLAYER) {
											explosion.add((Player) e);
										}
									}
									
									delay.put(p.getUniqueId(), 60*15);
									Bukkit.getScheduler().runTaskTimer(WeaponSystem.plugin, new BukkitRunnable() {
										
										@Override
										public void run() {
											delay.put(p.getUniqueId(), delay.get(p.getUniqueId())-1);
											if (delay.get(p.getUniqueId()) <= 0) {
												delay.remove(p.getUniqueId());
												explosion.clear();
												cancel();
											}
										}
									}, 0, 20);
									
									for (Player op : Bukkit.getOnlinePlayers()) {
										op.sendMessage(WeaponSystem.prefix+"§7Es gab einen Terror anschlag §8(§c"+dest+"§8)");
									}
								} else {
									p.sendMessage(WeaponSystem.prefix+WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nomoney").replace("%money%", "§e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(5000-balance)+"$"));
								}
							} else {
								int[] time = splitToComponentTimes(delay.get(p.getUniqueId()));
								if (time[0] == 0) {
									if (time[1] == 0) {
										p.sendMessage(WeaponSystem.prefix+"§7Du hast noch einen Cooldown: §c"+time[0]+" Stunden "+time[1]+" Minuten "+time[2]+" Sekunden");
									} else {
										p.sendMessage(WeaponSystem.prefix+"§7Du hast noch einen Cooldown: §c"+time[1]+" Minuten "+time[2]+" Sekunden");
									}
								} else {
									p.sendMessage(WeaponSystem.prefix+"§7Du hast noch einen Cooldown: §c"+time[2]+" Sekunden");
								}
							}
						}
					} else {
						p.sendMessage("§cNutze§8: §8/§csp <Ort>");
					}
				}
			} else {
				p.sendMessage(WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nopermission"));
			}
		}
		return false;
	}
	
	public static int[] splitToComponentTimes(int time) {
	    int hours = time / 3600;
	    int remainder = time - hours * 3600;
	    int mins = remainder / 60;
	    remainder = remainder - mins * 60;
	    int secs = remainder;

	    int[] ints = {hours , mins , secs};
	    return ints;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		ArrayList<String> tab = new ArrayList<>();
		if (args.length == 1) {
			tab.add("info");
		}
		return tab;
	}
	
	@EventHandler
	private void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		EntityDamageEvent last = p.getLastDamageCause();
		if (last.getCause() == DamageCause.ENTITY_EXPLOSION) {
			if (explosion.contains(p)) {
				Bukkit.getScheduler().runTaskLater(WeaponSystem.plugin, new Runnable() {
					
					@Override
					public void run() {
						DeathPlayer.getDeathPlayer(p).remove(false);
					}
				}, 10);
				explosion.remove(p);
			}
		}
	}
	
	@EventHandler
	private void onExplosion(EntityExplodeEvent e) {
		if (e.getEntity().getCustomName() != null) {
			if (e.getEntity().getCustomName().equals("bomb")) {
				for (Block b : e.blockList()) {
					if (new Random().nextInt(10) == 1) {
						Location l = e.getLocation();
						Entity f = l.getWorld().spawnFallingBlock(e.getLocation(), b.getType(), b.getData());
						if (f instanceof FallingBlock) {
							f.setCustomName("bomb");
							f.setVelocity(new Vector((Math.random()*2)-1, (Math.random()*2)-1, (Math.random()*2)-1));
						} else {
							f.remove();
						}
					}
				}
				e.setCancelled(true);
				e.getLocation().getWorld().spawnParticle(Particle.CLOUD, e.getLocation(), 1000, 1, 1, 1, 0.1);
			}
		}
	}
	
	@EventHandler
	private void onLand(EntityChangeBlockEvent e) {
		if (e.getEntity() != null) {
			if (e.getEntity().getCustomName() != null) {
				if (e.getEntity().getCustomName().equals("bomb")) {
					e.setCancelled(true);
					e.getBlock().getWorld().spawnParticle(Particle.BLOCK_CRACK, e.getBlock().getLocation(), 10, new MaterialData(((FallingBlock) e.getEntity()).getBlockId(), ((FallingBlock) e.getEntity()).getBlockData()));
					e.getEntity().remove();
				}
			}
		}
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e){
	    List<Entity> ents = e.getEntity().getNearbyEntities(5, 5, 5);
	    for(Entity ent : ents) {
	    	if (ent.getType() == EntityType.FALLING_BLOCK) {
	        	if (ent.getCustomName() != null) {
		        	if (ent.getCustomName().equals("bomb")) {
		        		e.getEntity().getWorld().spawnParticle(Particle.BLOCK_CRACK, e.getEntity().getLocation(), 10, new MaterialData(((FallingBlock) ent).getBlockId(), ((FallingBlock) ent).getBlockData()));
		        		e.getEntity().remove();
		        	}
	        	}
	    	}
	    }
	}
}
