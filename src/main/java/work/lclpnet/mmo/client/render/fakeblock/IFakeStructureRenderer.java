package work.lclpnet.mmo.client.render.fakeblock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import work.lclpnet.mmo.block.fake.FakeStructure;

@Environment(EnvType.CLIENT)
public interface IFakeStructureRenderer {

    void render(FakeStructure structure, Vec3d cameraPos, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers);

    void setWorld(World world);
}
