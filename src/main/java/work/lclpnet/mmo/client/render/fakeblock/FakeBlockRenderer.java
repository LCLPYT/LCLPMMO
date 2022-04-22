package work.lclpnet.mmo.client.render.fakeblock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import work.lclpnet.mmo.block.fake.FakeBlock;

import java.util.Objects;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class FakeBlockRenderer implements IFakeBlockRenderer {

    private static IFakeBlockRenderer instance = null;
    private final Random random;

    public static void setInstance(IFakeBlockRenderer renderer) {
        instance = Objects.requireNonNull(renderer);
    }

    public static IFakeBlockRenderer getInstance() {
        return instance;
    }

    private final MinecraftClient client;

    public FakeBlockRenderer(MinecraftClient client) {
        this.client = client;
        this.random = new Random();
    }

    @Override
    public void render(FakeBlock fakeBlock, BlockPos pos, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockRenderManager blockRenderManager = client.getBlockRenderManager();

        final BlockState state = fakeBlock.getState();
        final RenderLayer layer = RenderLayers.getBlockLayer(state);
        blockRenderManager.renderBlock(state, pos, fakeBlock.getWorld(), matrices, vertexConsumers.getBuffer(layer), true, random);
    }
}
