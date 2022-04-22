package work.lclpnet.mmo.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public record RenderContext(MatrixStack matrices, float tickDelta, Vec3d cameraPos, VertexConsumerProvider vertexConsumers) {

}
