package de.paul.weaponsystem.weapon.consumable;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.weapon.muni.Muni;

public class Narcotics extends Consumable {

	public Narcotics(Muni muni) {
		super(muni);
	}

	@Override
	void Use(Player p, Player clicked) {
		clicked.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*60*10, 2, false, false), true);
		clicked.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*60*10, 2, false, false), true);
		
		clicked.sendMessage(WeaponSystem.prefix+"§5Du wurdest betäubt für 10 Minuten");
		
		getMuni().removeItem(p.getInventory(), id);
	}

}
