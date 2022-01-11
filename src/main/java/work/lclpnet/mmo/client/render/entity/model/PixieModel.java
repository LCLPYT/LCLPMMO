package work.lclpnet.mmo.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.util.math.MathHelper;
import work.lclpnet.mmo.entity.PixieEntity;

public class PixieModel extends CompositeEntityModel<PixieEntity> {

    public final ModelPart body;
    public final ModelPart leftTopWing;
    public final ModelPart rightTopWing;
    public final ModelPart rightBottomWing;
    public final ModelPart leftBottomWing;

    public PixieModel() {
        textureWidth = 16;
        textureHeight = 16;

        final float pi = (float) Math.PI, y0 = 22.5F;

        body = new ModelPart(this, 0, 0);
        body.setPivot(-1F, y0, -1F);
        body.addCuboid(0F, -2F, 0F, 2, 2, 2);
        setRotation(body, 0F, 0F, 0F);

        leftTopWing = new ModelPart(this, 10, 0);
        leftTopWing.setPivot(-1F, y0 - 2F, 1F);
        leftTopWing.addCuboid(0F, 0F, 0F, 0F, 1F, 3F, 0.001F);
        setRotation(leftTopWing, pi * 0.19F, pi * 1.59F, pi * 0F);

        rightTopWing = new ModelPart(this, 10, 4);
        rightTopWing.setPivot(1F, y0 - 2F, 1F);
        rightTopWing.addCuboid(0F, 0F, 0F, 0F, 1F, 3F, 0.001F);
        setRotation(rightTopWing, pi * 0.19F, pi * 0.41F, pi * 0F);

        rightBottomWing = new ModelPart(this, 10, 8);
        rightBottomWing.setPivot(1F, y0, 1F);
        rightBottomWing.addCuboid(0F, 0F, 0F, 0F, 1F, 2F, 0.001F);
        setRotation(rightBottomWing, pi * -0.09F, pi * 0.41F, pi * 0F);

        leftBottomWing = new ModelPart(this, 10, 11);
        leftBottomWing.setPivot(-1F, y0, 1F);
        leftBottomWing.addCuboid(0F, 0F, 0F, 0F, 1F, 2F, 0.001F);
        setRotation(leftBottomWing, pi * -0.09F, pi * 1.59F, pi * 0F);
    }

    private void setRotation(ModelPart model, float x, float y, float z) {
        model.pitch = x;
        model.yaw = y;
        model.roll = z;
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(body, leftTopWing, rightTopWing, rightBottomWing, leftBottomWing);
    }

    @Override
    public void setAngles(PixieEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        boolean onGroundMotionless = entity.isOnGround() && entity.getVelocity().lengthSquared() < 1.0E-2D;
        if (onGroundMotionless) return;

        float pi = (float) Math.PI;
        float rot = MathHelper.cos(animationProgress) * pi;

        this.leftTopWing.yaw = pi * 1.49F + rot * 0.25F;
        this.leftTopWing.pitch = pi * 0.19F + rot * 0.05F;

        this.rightTopWing.yaw = pi * 0.51F - rot * 0.25F;
        this.rightTopWing.pitch = pi * 0.19F + rot * 0.05F;

        this.rightBottomWing.yaw = pi * 0.51F - rot * 0.25F;
        this.rightBottomWing.pitch = pi * -0.09F - rot * 0.025F;

        this.leftBottomWing.yaw = pi * 1.49F + rot * 0.25F;
        this.leftBottomWing.pitch = pi * -0.09F - rot * 0.025F;
    }
}
