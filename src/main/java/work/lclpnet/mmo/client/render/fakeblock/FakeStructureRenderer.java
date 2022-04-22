package work.lclpnet.mmo.client.render.fakeblock;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import work.lclpnet.mmo.block.fake.*;
import work.lclpnet.mmo.client.render.RenderContext;

public class FakeStructureRenderer implements IFakeStructureRenderer {

    protected World world = null;

    // hold transformStack as field, so that it doesn't need to be allocated every tick.
    // ensure the stack is properly reset after each render cycle
    // !!this is not thread safe!!
    protected final MatrixStack transformStack = new MatrixStack();

    @Override
    public void render(FakeStructure structure, BlockPos position, final RenderContext ctx) {
        final MatrixStack matrices = ctx.matrices();
        final Vec3d cameraPos = ctx.cameraPos();

        matrices.push();
        matrices.translate(position.getX() - cameraPos.x, position.getY() - cameraPos.y, position.getZ() - cameraPos.z);

        for (FakeGroup group : structure.groups) {
            renderGroup(group, ctx);
        }

        matrices.pop();
    }

    // make sure, transformStack is empty after full iteration
    protected void renderGroup(FakeGroup group, RenderContext ctx) {
        final IFakeBlockRenderer renderer = FakeBlockRenderer.getInstance();

        final boolean groupTransform = group.transform != null && group.transform.length > 0;
        if (groupTransform) {
            transformStack.push();

            for (Transformation transformation : group.transform) {
                transformation.apply(transformStack, group.pivot);
            }
        }

        if (group.children != null) {
            for (FakeGroup child : group.children)
                this.renderGroup(child, ctx);
        }

        final MatrixStack matrices = ctx.matrices();
        matrices.push();

        if (!transformStack.isEmpty()) {
            // manually apply cumulated transformations
            MatrixStack.Entry last = matrices.peek();
            MatrixStack.Entry lastTransform = transformStack.peek();
            last.getPositionMatrix().multiply(lastTransform.getPositionMatrix());
            last.getNormalMatrix().multiply(lastTransform.getNormalMatrix());
        }

        // TESTING ---------------------------------------- TODO remove
        long periodMs = 3000L;
        float rot = (System.currentTimeMillis() % periodMs) / (float) periodMs * 360F;

        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.wrapDegrees(rot)));
        matrices.translate(-0.5F, -0.5F, -0.5F);
        // END TESTING ------------------------------------

        matrices.translate(group.origin.getX(), group.origin.getY(), group.origin.getZ());

        final float tickDelta = ctx.tickDelta();
        final VertexConsumerProvider vertexConsumers = ctx.vertexConsumers();

        for (FakeBlock block : group.blocks) {
            matrices.push();

            final FakeBlockPos position = block.getPos();
            matrices.translate(position.getX(), position.getY(), position.getZ());

            final BlockPos pos = position.toBlockPos(group.origin);
            int i = world != null ? WorldRenderer.getLightmapCoordinates(world, pos) : LightmapTextureManager.MAX_LIGHT_COORDINATE;
            renderer.render(block, pos, tickDelta, matrices, vertexConsumers, i, OverlayTexture.DEFAULT_UV);

            matrices.pop();
        }

        matrices.pop();

        if (groupTransform) {
            transformStack.pop();
        }
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }
}
