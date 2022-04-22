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

public class FakeStructureRenderer implements IFakeStructureRenderer {

    protected World world = null;

    @Override
    public void render(FakeStructure structure, Vec3d cameraPos, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        for (FakeGroup group : structure.groups)
            renderGroup(group, cameraPos, tickDelta, matrices, vertexConsumers, new MatrixStack());
    }

    private void renderGroup(FakeGroup group, Vec3d cameraPos, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, MatrixStack transformStack) {
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
                this.renderGroup(child, cameraPos, tickDelta, matrices, vertexConsumers, transformStack);
        }

        matrices.push();
        matrices.translate(group.origin.getX() - cameraPos.x, group.origin.getY() - cameraPos.y, group.origin.getZ() - cameraPos.z);

        if (groupTransform) {
            MatrixStack.Entry last = matrices.peek();
            MatrixStack.Entry lastTransform = transformStack.peek();
            last.getPositionMatrix().multiply(lastTransform.getPositionMatrix());
            last.getNormalMatrix().multiply(lastTransform.getNormalMatrix());
        }

        long periodMs = 3000L;
        float rot = (System.currentTimeMillis() % periodMs) / (float) periodMs * 360F;

        matrices.translate(0.5F, 0.5F, 0.5F);
//        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.wrapDegrees(45F)));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.wrapDegrees(rot)));
        matrices.translate(-0.5F, -0.5F, -0.5F);

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
