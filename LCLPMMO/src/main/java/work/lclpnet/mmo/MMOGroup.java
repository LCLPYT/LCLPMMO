package work.lclpnet.mmo;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class MMOGroup extends ItemGroup{

	public MMOGroup(String label) {
		super(label);
	}
	
	@Override
	public ItemStack createIcon() {
		return new ItemStack(Items.TOTEM_OF_UNDYING);
	}
	
}
