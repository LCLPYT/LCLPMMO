package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
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
import work.lclpnet.mmo.client.render.RenderContext;
import work.lclpnet.mmo.client.render.fakeblock.FakeStructureManager;
import work.lclpnet.mmo.client.render.fakeblock.FakeStructureRenderer;
import work.lclpnet.mmo.client.render.fakeblock.IFakeStructureRenderer;
import work.lclpnet.mmo.module.DecorationsModule;

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

        if (fakeStructureRenderer != null) {
            final RenderContext renderContext = new RenderContext(matrices, tickDelta, camera.getPos(), this.bufferBuilders.getEntityVertexConsumers());

            for (var fakeStructure : fakeStructureManager.getFakeStructures()) {
                fakeStructureRenderer.render(fakeStructure.getKey(), fakeStructure.getValue(), renderContext);
            }
        }
    }

    @Unique
    private final FakeStructureManager fakeStructureManager = new FakeStructureManager();
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

        fakeStructureManager.clear();
        if (world != null) {
            // TODO remove
            fakeStructureManager.add(new FakeStructure(
                    UUID.randomUUID(),
                    new FakeGroup[]{
                            new FakeGroup(
                                    new Vec3f(0.5F, 0.5F, 0.5F),
                                    new BlockPos(0, 0, 0),
                                    new FakeBlock[]{
                                            new FakeBlock(world, new FakeBlockPos(0, 0, 0), Blocks.COBBLESTONE.getDefaultState()),
                                            new FakeBlock(world, new FakeBlockPos(0, 0, 1), Blocks.STONE.getDefaultState()),
                                            new FakeBlock(world, new FakeBlockPos(0, 1, 1), Blocks.ICE.getDefaultState()),
                                    },
                                    new FakeGroup[]{
                                            new FakeGroup(
                                                    new Vec3f(0.5F, 0.5F, 0.5F),
                                                    new BlockPos(1, 2, 0),
                                                    new FakeBlock[]{
                                                            new FakeBlock(world, new FakeBlockPos(0, 0, 0), DecorationsModule.glassBottleBlock.getDefaultState()),
                                                    },
                                                    null,
                                                    null
                                            )
                                    },
                                    new Transformation[]{
                                            new Transformation(Transformation.ROTATE, new Vec3f(0.0F, (float) Math.PI / 4F, 0.0F)),
//                                            new Transformation(Transformation.SCALE, new Vec3f(0.5F, 0.5F, 0.5F)),
                                    }
                            ),
                            new FakeGroup(
                                    new Vec3f(0.5F, 0.5F, 0.5F),
                                    new BlockPos(1, 0, 0),
                                    new FakeBlock[]{
                                            new FakeBlock(world, new FakeBlockPos(0, 0, 0), DecorationsModule.glassBottleBlock.getDefaultState()),
                                    },
                                    null,
                                    null
                            )
                    }
            ), new BlockPos(1600, 184, 3500));
        }
    }

    @Override
    public FakeStructureManager getFakeStructureManager() {
        return this.fakeStructureManager;
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
