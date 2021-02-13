/*
 * Scheme inspired by MrCrayfish's Furniture mod.
 * https://github.com/MrCrayfish/MrCrayfishFurnitureMod
 */

package work.lclpnet.mmo.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.util.MMONames;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@EventBusSubscriber(modid = LCLPMMO.MODID, bus = Bus.MOD)
public class MMOBlocks {

	private static final List<Block> BLOCKS = new ArrayList<>();
	private static final List<Item> BLOCK_ITEMS = new ArrayList<>();

	public static final Block GLASS_BOTTLE = register(MMONames.Block.GLASS_BOTTLE, new GlassBottleBlock(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0).sound(SoundType.GLASS).notSolid().doesNotBlockMovement()));

	private static Block register(String name, Block block) {
		return register(name, block, new Item.Properties().group(LCLPMMO.GROUP));
	}

	private static Block register(String name, Block block, Item.Properties properties) {
		return register(name, block, new BlockItem(block, properties));
	}

	private static Block register(String name, Block block, BlockItem item) {
		return register(name, block, block1 -> item);
	}

	private static Block register(String name, Block block, Function<Block, BlockItem> function) {
		block.setRegistryName(name);
		BLOCKS.add(block);
		if(block.getRegistryName() != null) {
			Item item = function.apply(block);
			if(item != null) {
				item.setRegistryName(name);
				BLOCK_ITEMS.add(item);
			}
		}
		return block;
	}

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		BLOCKS.forEach(event.getRegistry()::register);
		BLOCKS.clear();
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		BLOCK_ITEMS.forEach(event.getRegistry()::register);
		BLOCK_ITEMS.clear();
	}

}
