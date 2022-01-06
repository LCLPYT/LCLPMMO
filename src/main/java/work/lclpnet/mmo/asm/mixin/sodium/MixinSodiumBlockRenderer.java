package work.lclpnet.mmo.asm.mixin.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuffers;
import me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import work.lclpnet.mmo.block.GlassBottleBlock;

@Mixin(BlockRenderer.class)
public class MixinSodiumBlockRenderer {

    @Inject(
            method = "renderModel(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/render/model/BakedModel;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuffers;ZJ)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onRenderModel(BlockRenderView world, BlockState state, BlockPos pos, BakedModel model, ChunkModelBuffers buffers, boolean cull, long seed, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof GlassBottleBlock)  // cancel rendering of GlassBottleBlock, because this is done completely by the GlassBottleBlockEntityRenderer
            cir.setReturnValue(true); // cancel and pretend it was rendered
    }
}
