package work.lclpnet.mmo.module;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.block.GlassBottleBlock;
import work.lclpnet.mmo.blockentity.GlassBottleBlockEntity;
import work.lclpnet.mmocontent.block.MMOBlockRegistrar;

public class DecorationsModule implements IModule {

    public static BlockEntityType<GlassBottleBlockEntity> glassBottleBlockEntity;

    public static GlassBottleBlock glassBottleBlock;

    @Override
    public void register() {
        glassBottleBlock = new GlassBottleBlock();
        new MMOBlockRegistrar(glassBottleBlock)
                .register(LCLPMMO.identifier("glass_bottle"), LCLPMMO.ITEM_GROUP);

        glassBottleBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, LCLPMMO.identifier("glass_bottle"),
                BlockEntityType.Builder.create(GlassBottleBlockEntity::new, glassBottleBlock).build(null));
    }
}
