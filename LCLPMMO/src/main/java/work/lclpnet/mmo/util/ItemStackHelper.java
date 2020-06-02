package work.lclpnet.mmo.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;

public class ItemStackHelper {

	public static boolean isAir(ItemStack is) {
		return isItem(is, Items.AIR);
	}
	
	public static boolean isItem(ItemStack is, Item i) {
		return i.equals(is.getItem());
	}
	
	public static boolean isPotion(ItemStack is) {
		return isClassType(is, PotionItem.class);
	}
	
	public static <T extends Item> boolean isClassType(ItemStack is, Class<T> clazz) {
		return clazz.isInstance(is.getItem());
	}
	
}
