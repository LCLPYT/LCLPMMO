package work.lclpnet.mmo.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SporeParticle extends SpriteTexturedParticle {

    private final IAnimatedSprite spriteSetWithAge;

    protected SporeParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteSetWithAge) {
        super(world, x, y, z, motionX, motionY, motionZ);

        this.spriteSetWithAge = spriteSetWithAge;

        this.motionX *= 0.1D;
        this.motionY *= 0.1D;
        this.motionZ *= 0.1D;
        this.motionX += motionX;
        this.motionY += motionY;
        this.motionZ += motionZ;

        float f = 1.0F - (float) (Math.random() * (double) 0.3F);
        this.particleRed = f;
        this.particleGreen = f;
        this.particleBlue = f;

        this.particleScale *= 0.75F;

        int i = (int) (8.0D / (Math.random() * 0.8D + 0.3D));
        this.maxAge = (int) Math.max((float) i * 2.5F, 1.0F);
        this.canCollide = true;

        this.selectSpriteWithAge(spriteSetWithAge);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.selectSpriteWithAge(this.spriteSetWithAge);
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.96F;
            this.motionY *= 0.96F;
            this.motionZ *= 0.96F;

            if (this.onGround) {
                this.motionX *= 0.7D;
                this.motionZ *= 0.7D;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {

        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SporeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

}
