package work.lclpnet.mmo.module;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import work.lclpnet.mmo.block.BigChainBlock;
import work.lclpnet.mmo.block.BigChainCornerBlock;
import work.lclpnet.mmo.block.GlassBottleBlock;
import work.lclpnet.mmo.blockentity.GlassBottleBlockEntity;
import work.lclpnet.mmocontent.block.MMOBlockRegistrar;

import static work.lclpnet.mmo.LCLPMMO.ITEM_GROUP;
import static work.lclpnet.mmo.LCLPMMO.identifier;

public class DecorationsModule implements IModule {

    public static BlockEntityType<GlassBottleBlockEntity> glassBottleBlockEntity;

    public static GlassBottleBlock glassBottleBlock;

    @Override
    public void register() {
        glassBottleBlock = new GlassBottleBlock();
        new MMOBlockRegistrar(glassBottleBlock)
                .register(identifier("glass_bottle"), ITEM_GROUP);

        glassBottleBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier("glass_bottle"),
                BlockEntityType.Builder.create(GlassBottleBlockEntity::new, glassBottleBlock).build(null));

        new MMOBlockRegistrar(new BigChainBlock())
                .register(identifier("chain_block"), ITEM_GROUP);

        new MMOBlockRegistrar(new BigChainCornerBlock())
                .register(identifier("chain_corner_block"), ITEM_GROUP);
    }
}
