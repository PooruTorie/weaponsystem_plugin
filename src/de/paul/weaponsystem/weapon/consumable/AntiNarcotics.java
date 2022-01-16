package de.paul.weaponsystem.weapon.consumable;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.weapon.muni.Muni;

public class AntiNarcotics extends Consumable {

	public AntiNarcotics(Muni muni) {
		super(muni);
	}

	@Override
	void Use(Player p, Player clicked) {
		if (clicked.hasPotionEffect(PotionEffectType.SLOW) && clicked.hasPotionEffect(PotionEffectType.BLINDNESS)) {
			clicked.removePotionEffect(PotionEffectType.SLOW);
			clicked.removePotionEffect(PotionEffectType.BLINDNESS);
			
			clicked.sendMessage(WeaponSystem.prefix+"§5Deine Betäubung ist vorbei");
			
			getMuni().removeItem(p.getInventory(), id);
		}
	}

}
