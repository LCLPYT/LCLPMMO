package work.lclpnet.mmo.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;

public class ItemStackUtils {

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

    public static CompoundNBT saveAllItemsIncludeEmpty(CompoundNBT tag, NonNullList<ItemStack> list) {
        return saveAllItemsIncludeEmpty(tag, list, true);
    }

    public static CompoundNBT saveAllItemsIncludeEmpty(CompoundNBT tag, NonNullList<ItemStack> list, boolean saveEmpty) {
        ListNBT listnbt = new ListNBT();

        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putByte("Slot", (byte) i);
            itemstack.write(compoundnbt);
            listnbt.add(compoundnbt);
        }

        if (!listnbt.isEmpty() || saveEmpty) {
            tag.put("Items", listnbt);
        }

        return tag;
    }

}
