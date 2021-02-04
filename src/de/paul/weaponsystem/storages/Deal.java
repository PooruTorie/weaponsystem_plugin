package de.paul.weaponsystem.storages;

import java.text.DecimalFormat;
import java.util.Locale;

import org.bukkit.entity.Player;

import de.paul.weaponsystem.WeaponSystem;
import de.paul.weaponsystem.storages.Storage.StorageType;
import de.paul.weaponsystem.weapon.WeaponItem;

public class Deal {

	private Player p;
	private Player other;
	private int costs;
	private WeaponItem weapon;
	private int slot;

	public Deal(Player p, Player other, int costs, WeaponItem weapon) {
		this.p = p;
		this.other = other;
		this.costs = costs;
		this.weapon = weapon;
		slot = p.getInventory().getHeldItemSlot();
	}

	public void accept() {
		if (WeaponSystem.economy.getBalance(other) >= costs) {
			WeaponSystem.economy.withdrawPlayer(p, costs);
			WeaponSystem.economy.depositPlayer(p, costs);
			
			if (PlayerWeapons.getForPlayer(p).hasWeapon(weapon.getWeapon())) {
				if (!PlayerWeapons.getForPlayer(other).hasWeapon(weapon.getWeapon())) {
					PlayerWeapons.getForPlayer(p).remove(weapon.getWeapon());
					PlayerWeapons.getForPlayer(other).buy(weapon.getWeapon());
					
					p.getInventory().setItem(slot, null);
					weapon.remove();
					weapon.getWeapon().give(other, StorageType.weapon.getStorage());
					
					p.sendMessage(WeaponSystem.prefix+"§aDu hast deine "+weapon.getWeapon().getItemName()+"§a für §e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(costs)+"$ verkauft.");
					other.sendMessage(WeaponSystem.prefix+"§aDu hast eine "+weapon.getWeapon().getItemName()+"§a für §e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(costs)+"$ gekauft.");
				} else {
					other.sendMessage(WeaponSystem.prefix+"§cDu hast diese Waffe schon in deinem Lager.");
				}
			}
		} else {
			other.sendMessage(WeaponSystem.prefix+WeaponSystem.prefix+WeaponSystem.loadConfig("config", "messages").getChatColorString("nomoney").replace("%money%", "§e"+DecimalFormat.getIntegerInstance(Locale.GERMAN).format(costs-WeaponSystem.economy.getBalance(other))+"$"));
		}
	}

}
