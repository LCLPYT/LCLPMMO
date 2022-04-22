package work.lclpnet.mmo.asm.mixin.iris;

import net.coderbot.iris.mixin.LevelRendererAccessor;
import net.coderbot.iris.pipeline.ShadowRenderer;
import net.coderbot.iris.uniforms.CameraUniforms;
import net.coderbot.iris.uniforms.CapturedRenderingState;
import net.coderbot.iris.vendored.joml.Vector3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.asm.type.client.IWorldRenderer;
import work.lclpnet.mmo.client.render.RenderContext;
import work.lclpnet.mmo.client.render.fakeblock.IFakeStructureRenderer;

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

        final var fakeStructures = ((IWorldRenderer) worldRenderer).getFakeStructureManager().getFakeStructures();
        if (fakeStructures.isEmpty()) return;

        MinecraftClient.getInstance().getProfiler().swap("fakeblock shadows");

        final VertexConsumerProvider.Immediate vertexConsumers = this.buffers.getEntityVertexConsumers();
        final Vector3d jCameraPos = CameraUniforms.getUnshiftedCameraPosition();
        final Vec3d cameraPos = new Vec3d(jCameraPos.x, jCameraPos.y, jCameraPos.z);
        final IFakeStructureRenderer renderer = ((IWorldRenderer) worldRenderer).getFakeStructureRenderer();
        final MatrixStack matrices = ShadowRenderer.createShadowModelView(this.sunPathRotation, this.intervalSize);

        // render all fake blocks to the shadow render buffer
        final RenderContext ctx = new RenderContext(matrices, CapturedRenderingState.INSTANCE.getTickDelta(), cameraPos, vertexConsumers);

        for (var fakeStructure : fakeStructures) {
            renderer.render(fakeStructure.getKey(), fakeStructure.getValue(), ctx);
        }
    }
}
