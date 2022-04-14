package work.lclpnet.mmo.asm.mixin.iris;

import net.coderbot.iris.mixin.LevelRendererAccessor;
import net.coderbot.iris.pipeline.ShadowRenderer;
import net.coderbot.iris.uniforms.CameraUniforms;
import net.coderbot.iris.uniforms.CapturedRenderingState;
import net.coderbot.iris.vendored.joml.Vector3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.asm.type.client.IWorldRenderer;
import work.lclpnet.mmo.client.fakeblock.FakeBlock;
import work.lclpnet.mmo.client.render.FakeBlockRenderer;
import work.lclpnet.mmo.client.render.fakeblock.IFakeBlockRenderer;

import java.util.Set;

/**
 * Compat-mixin for FakeBlocks.
 */
@Mixin(ShadowRenderer.class)
public abstract class MixinShadowRenderer {

    @Shadow @Final private BufferBuilderStorage buffers;

    @Shadow @Final private float sunPathRotation;

    @Shadow @Final private float intervalSize;

    @Inject(method = "renderShadows", at = @At(value = "CONSTANT", args = "stringValue=draw entities", ordinal = 0))
    public void beforeEntities(LevelRendererAccessor levelRenderer, Camera playerCamera, CallbackInfo ci) {
        if (!(levelRenderer instanceof WorldRenderer worldRenderer)) return;

        final Set<FakeBlock> fakeBlocks = ((IWorldRenderer) worldRenderer).getFakeBlocks();
        if (fakeBlocks.isEmpty()) return;

        MinecraftClient.getInstance().getProfiler().swap("fakeblock shadows");

        final VertexConsumerProvider.Immediate vertexConsumers = this.buffers.getEntityVertexConsumers();
        final Vector3d cameraPos = CameraUniforms.getUnshiftedCameraPosition();
        final IFakeBlockRenderer renderer = FakeBlockRenderer.getInstance();
        final MatrixStack matrices = ShadowRenderer.createShadowModelView(this.sunPathRotation, this.intervalSize);
        final World world = levelRenderer.getLevel();

        // push all fake blocks to the shadow render buffer
        for (FakeBlock fakeBlock : fakeBlocks) {
            matrices.push();

            final BlockPos position = fakeBlock.getPos();
            matrices.translate(position.getX() - cameraPos.x, position.getY() - cameraPos.y, position.getZ() - cameraPos.z);

            int i = world != null ? WorldRenderer.getLightmapCoordinates(world, fakeBlock.getPos()) : LightmapTextureManager.MAX_LIGHT_COORDINATE;
            renderer.render(fakeBlock, CapturedRenderingState.INSTANCE.getTickDelta(), matrices, vertexConsumers, i, OverlayTexture.DEFAULT_UV);

            matrices.pop();
        }
    }
}
