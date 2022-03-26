package work.lclpnet.mmo.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class SporesParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteSetWithAge;

    protected SporesParticle(ClientWorld clientWorld, double d, double e, double f, double motionX, double motionY, double motionZ, SpriteProvider spriteSetWithAge) {
        super(clientWorld, d, e, f, motionX, motionY, motionZ);

        this.spriteSetWithAge = spriteSetWithAge;

        this.velocityX *= 0.1D;
        this.velocityY *= 0.1D;
        this.velocityZ *= 0.1D;
        this.velocityX += motionX;
        this.velocityY += motionY;
        this.velocityZ += motionZ;

        float tint = 1.0F - (float) (Math.random() * (double) 0.3F);
        this.red = tint;
        this.green = tint;
        this.blue = tint;

        this.scale *= 0.75F;

        int i = (int) (8.0D / (Math.random() * 0.8D + 0.3D));
        this.maxAge = (int) Math.max((float) i * 2.5F, 1.0F);
        this.collidesWithWorld = true;

        this.setSpriteForAge(spriteSetWithAge);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteSetWithAge);
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            this.velocityX *= 0.96F;
            this.velocityY *= 0.96F;
            this.velocityZ *= 0.96F;

            if (this.onGround) {
                this.velocityX *= 0.7D;
                this.velocityZ *= 0.7D;
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider spriteSet;

        public Factory(SpriteProvider spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(DefaultParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SporesParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
