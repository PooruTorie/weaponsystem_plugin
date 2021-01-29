package de.paul.weaponsystem.weapon.muni;

import java.util.HashMap;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MuniItem extends ItemStack {
	
	private static HashMap<Integer, MuniItem> items = new HashMap<>();
	
	private Muni muni;

	public MuniItem(Muni muni) {
		super(muni.getItemID());
		this.muni = muni;
		
		ItemMeta m = getItemMeta();
		m.setDisplayName(muni.getItemName());
		m.setLore(muni.getItemLore());
		m.setUnbreakable(true);
		m.setLocalizedName(muni.getName()+"_"+muni.getId());
		setItemMeta(m);
		
		items.put(muni.getId(), this);
	}
	
	public static HashMap<Integer, MuniItem> getItems() {
		return items;
	}
}
