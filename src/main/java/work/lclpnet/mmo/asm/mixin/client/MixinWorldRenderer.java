package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.block.Blocks;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.asm.type.client.IWorldRenderer;
import work.lclpnet.mmo.client.fakeblock.FakeBlock;
import work.lclpnet.mmo.client.render.FakeBlockRenderer;
import work.lclpnet.mmo.client.render.fakeblock.IFakeBlockRenderer;

import java.util.HashSet;
import java.util.Set;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements IWorldRenderer {

    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    @Shadow private @Nullable ClientWorld world;

    @Shadow public abstract void tick();

    @Inject(method = "render", at = @At(value = "CONSTANT", args = "stringValue=blockentities", ordinal = 0))
    private void afterEntities(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        if (this.world != null)
            this.world.getProfiler().swap("fakeblocks");

        final VertexConsumerProvider.Immediate vertexConsumers = this.bufferBuilders.getEntityVertexConsumers();

        final Vec3d cameraPos = camera.getPos();
        final IFakeBlockRenderer renderer = FakeBlockRenderer.getInstance();

        synchronized (this.fakeBlocks) {
            for (FakeBlock fakeBlock : fakeBlocks) {
                matrices.push();

                final BlockPos position = fakeBlock.getPos();
                matrices.translate(position.getX() - cameraPos.x, position.getY() - cameraPos.y, position.getZ() - cameraPos.z);

                int i = world != null ? WorldRenderer.getLightmapCoordinates(world, fakeBlock.getPos()) : LightmapTextureManager.MAX_LIGHT_COORDINATE;
                renderer.render(fakeBlock, tickDelta, matrices, vertexConsumers, i, OverlayTexture.DEFAULT_UV);

                matrices.pop();
            }
        }
    }

    @Unique
    private final Set<FakeBlock> fakeBlocks = new HashSet<>();

    @Inject(
            method = "setWorld",
            at = @At("HEAD")
    )
    public void onSetWorld(ClientWorld world, CallbackInfo ci) {
        fakeBlocks.clear();
        if (world != null) {
            fakeBlocks.add(new FakeBlock(world, new BlockPos(1600, 184, 3500), Blocks.COBBLESTONE.getDefaultState()));
        }
    }

    @Override
    public Set<FakeBlock> getFakeBlocks() {
        return fakeBlocks;
    }
}
