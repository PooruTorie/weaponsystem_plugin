package de.paul.weaponsystem;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockBreakAnimation;

public class BlockCrack {
	
	public static HashMap<Location, Integer> damage = new HashMap<>();
	public static HashMap<Location, Integer> ids = new HashMap<>();
	
	public static void crack(Block b) {
		if (!damage.containsKey(b.getLocation())) {
			damage.put(b.getLocation(), 0);
		}
		if (!ids.containsKey(b.getLocation())) {
			ids.put(b.getLocation(), new Random().nextInt());
		}
		damage.put(b.getLocation(), Math.min(damage.get(b.getLocation())+1, 9));
		PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(ids.get(b.getLocation()), new BlockPosition(b.getX(), b.getY(), b.getZ()), damage.get(b.getLocation()));
		for (Player p : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static MaterialData getParticle(Block b) {
		return b.getType().getNewData(b.getData());
	}
	
}
