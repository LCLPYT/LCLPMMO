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

        registerChainVariant("");
        registerChainVariant("iron_");
    }

    private void registerChainVariant(String prefix) {
        BigChainBlock bigChainBlock = new BigChainBlock();
        new MMOBlockRegistrar(bigChainBlock)
                .register(identifier("%schain_block", prefix), ITEM_GROUP);

        BigChainCornerBlock bigChainCornerBlock = new BigChainCornerBlock();
        bigChainCornerBlock.baseBlock = bigChainBlock;
        new MMOBlockRegistrar(bigChainCornerBlock)
                .register(identifier("%schain_corner_block", prefix), ITEM_GROUP);

        bigChainBlock.cornerBlock = bigChainCornerBlock;
    }


}
