package work.lclpnet.mmo.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.entity.MMOEntities;
import work.lclpnet.mmo.util.MMONames;

@EventBusSubscriber(bus = Bus.MOD, modid = LCLPMMO.MODID)
public class MMOItems {

	private static final List<Item> ITEMS = new ArrayList<>();

	public static final Item PIXIE_SPAWN_EGG = register(MMONames.Item.PIXIE_SPAWN_EGG, new SpawnEggItem(MMOEntities.PIXIE, 0x7dafff, 0xdeebff, new Item.Properties().group(ItemGroup.MISC)));
	
	private static Item register(String name, Item item) {
		item.setRegistryName(name);
		ITEMS.add(item);
		return item;
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		ITEMS.forEach(event.getRegistry()::register);
		ITEMS.clear();
	}

}
