package work.lclpnet.mmo.client.render.fakeblock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import work.lclpnet.mmo.block.fake.FakeBlock;

@Environment(EnvType.CLIENT)
public interface IFakeBlockRenderer {

    void render(FakeBlock fakeBlock, BlockPos pos, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);
}
