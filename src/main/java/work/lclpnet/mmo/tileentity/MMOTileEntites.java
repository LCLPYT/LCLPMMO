package work.lclpnet.mmo.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.block.MMOBlocks;
import work.lclpnet.mmo.util.MMONames;

@EventBusSubscriber(modid = LCLPMMO.MODID, bus = Bus.MOD)
public class MMOTileEntites {

	private static final List<TileEntityType<?>> TILE_ENTITY_TYPES = new ArrayList<>();

	public static final TileEntityType<GlassBottleTileEntity> GLASS_BOTTLE = buildType(MMONames.TileEntity.GLASS_BOTTLE, TileEntityType.Builder.create(GlassBottleTileEntity::new, MMOBlocks.GLASS_BOTTLE));

	private static <T extends TileEntity> TileEntityType<T> buildType(String id, TileEntityType.Builder<T> builder) {
		TileEntityType<T> type = builder.build(null);
		type.setRegistryName(id);
		TILE_ENTITY_TYPES.add(type);
		return type;
	}

	@SubscribeEvent
	public static void registerTypes(final RegistryEvent.Register<TileEntityType<?>> event) {
		TILE_ENTITY_TYPES.forEach(type -> event.getRegistry().register(type));
		TILE_ENTITY_TYPES.clear();
	}

}
