package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.asm.type.client.IWorldRenderer;
import work.lclpnet.mmo.block.fake.*;
import work.lclpnet.mmo.client.render.fakeblock.FakeStructureRenderer;
import work.lclpnet.mmo.client.render.fakeblock.IFakeStructureRenderer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements IWorldRenderer {

    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    @Shadow private @Nullable ClientWorld world;

    @Shadow public abstract void tick();

    @Inject(method = "render", at = @At(value = "CONSTANT", args = "stringValue=blockentities", ordinal = 0))
    private void lclpmmo$afterEntities(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        if (this.world != null)
            this.world.getProfiler().swap("fakeblocks");

        final VertexConsumerProvider.Immediate vertexConsumers = this.bufferBuilders.getEntityVertexConsumers();

        if (fakeStructureRenderer != null) {
            final Vec3d cameraPos = camera.getPos();
            synchronized (this.fakeStructures) {
                for (FakeStructure fakeStructure : fakeStructures) {
                    fakeStructureRenderer.render(fakeStructure, cameraPos, tickDelta, matrices, vertexConsumers);
                }
            }
        }
    }

    @Unique
    private final Set<FakeStructure> fakeStructures = new HashSet<>();
    @Unique
    private IFakeStructureRenderer fakeStructureRenderer;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    public void lclpmmo$afterInit(MinecraftClient client, BufferBuilderStorage bufferBuilders, CallbackInfo ci) {
        this.fakeStructureRenderer = new FakeStructureRenderer();
    }

    @Inject(
            method = "setWorld",
            at = @At("HEAD")
    )
    public void lclpmmo$onSetWorld(ClientWorld world, CallbackInfo ci) {
        if (fakeStructureRenderer != null)
            fakeStructureRenderer.setWorld(world);

        fakeStructures.clear();
        if (world != null) {
            fakeStructures.add(new FakeStructure(
                    UUID.randomUUID(),
                    new FakeGroup[]{
                            new FakeGroup(
                                    new Vec3f(0.5F, 0.5F, 0.5F),
                                    new BlockPos(1600, 184, 3500),
                                    new FakeBlock[]{
                                            new FakeBlock(world, new FakeBlockPos(0, 0, 0), Blocks.COBBLESTONE.getDefaultState()),
                                            new FakeBlock(world, new FakeBlockPos(0, 0, 1), Blocks.STONE.getDefaultState()),
                                            new FakeBlock(world, new FakeBlockPos(0, 1, 1), Blocks.ICE.getDefaultState()),
                                    },
                                    null,
                                    new Transformation[]{
                                            new Transformation(Transformation.ROTATE, new Vec3f(0.0F, (float) Math.PI / 4F, 0.0F)),
//                                            new Transformation(Transformation.SCALE, new Vec3f(0.5F, 0.5F, 0.5F)),
                                    }
                            )
                    }
            ));
        }
    }

    @Override
    public Set<FakeStructure> getFakeStructures() {
        return fakeStructures;
    }

    @Override
    public IFakeStructureRenderer getFakeStructureRenderer() {
        return fakeStructureRenderer;
    }

    @Override
    public void setFakeStructureRenderer(IFakeStructureRenderer fakeStructureRenderer) {
        this.fakeStructureRenderer = fakeStructureRenderer;
    }
}
