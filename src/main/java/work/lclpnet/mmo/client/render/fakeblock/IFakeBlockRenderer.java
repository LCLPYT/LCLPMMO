package work.lclpnet.mmo.client.render.fakeblock;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import work.lclpnet.mmo.client.fakeblock.FakeBlock;

public interface IFakeBlockRenderer {

    void render(FakeBlock fakeBlock, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);
}
