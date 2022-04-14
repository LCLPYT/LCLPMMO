package work.lclpnet.mmo.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import work.lclpnet.mmo.client.fakeblock.FakeBlock;
import work.lclpnet.mmo.client.render.fakeblock.IFakeBlockRenderer;

import java.util.Objects;
import java.util.Random;

public class FakeBlockRenderer implements IFakeBlockRenderer {

    private static IFakeBlockRenderer instance = null;

    public static void setInstance(IFakeBlockRenderer renderer) {
        instance = Objects.requireNonNull(renderer);
    }

    public static IFakeBlockRenderer getInstance() {
        return instance;
    }

    private final MinecraftClient client;

    public FakeBlockRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(FakeBlock fakeBlock, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockRenderManager blockRenderManager = client.getBlockRenderManager();
        matrices.push();
        long periodMs = 3000L;
        float rot = (System.currentTimeMillis() % periodMs) / (float) periodMs * 360F;

        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.wrapDegrees(rot)));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.wrapDegrees(rot)));
        matrices.translate(-0.5F, -0.5F, -0.5F);

        blockRenderManager.renderBlock(fakeBlock.getState(), fakeBlock.getPos(), fakeBlock.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getSolid()), true, new Random());
        matrices.pop();
    }
}
