package de.paul.weaponsystem.storages;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;

import org.bukkit.entity.Player;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.weapon.WeaponItem;

public class Dealer {

	private static HashMap<Player, Deal> waitAccept = new HashMap<>();

	public static void startDeal(Player p, Player other, int costs) {
		WeaponItem weapon = WeaponItem.getWeaponByItem(p.getItemInHand());
		
		if (weapon != null) {
			p.sendMessage(WeaponSystem.prefix+"§aDu startest einen Deal mit "+other.getName());
			other.sendMessage(WeaponSystem.prefix+"§a"+p.getName()+" möchte die Waffe "+weapon.getWeapon().getItemName()+"§a für §e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(costs)+"$§a mit dir Dealen.");
			other.sendMessage(WeaponSystem.prefix+"§a§lSchreibe /dealweapon accept §aum die Anfrage anzunehmen.");
			
			waitAccept.put(other, new Deal(p, other, costs, weapon));
		} else {
			p.sendMessage(WeaponSystem.prefix+"§cDu hast keine Waffe in deiner Hand");
		}
	}

	public static void accept(Player other) {
		if (waitAccept.containsKey(other)) {
			Deal deal = waitAccept.get(other);
			waitAccept.remove(other);
			
			deal.accept();
		}
	}

}
