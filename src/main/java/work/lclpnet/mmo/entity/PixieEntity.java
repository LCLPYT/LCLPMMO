package work.lclpnet.mmo.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.INPC;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookAtWithoutMovingGoal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import work.lclpnet.mmo.audio.MMOSoundEvents;

public class PixieEntity extends CreatureEntity implements INPC {

	protected PixieEntity(World worldIn) {
		super(MMOEntities.PIXIE, worldIn);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 0.4D));
		this.goalSelector.addGoal(8, new MoveTowardsRestrictionGoal(this, 0.4D));
		this.goalSelector.addGoal(9, new LookAtWithoutMovingGoal(this, PlayerEntity.class, 4.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 8.0F));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return MMOSoundEvents.ENTITY_PIXIE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return MMOSoundEvents.ENTITY_PIXIE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MMOSoundEvents.ENTITY_PIXIE_DEATH;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.rand.nextFloat() < 0.1F) {
			for(int i = 0; i < this.rand.nextInt(2) + 1; ++i) {
				this.addParticle(this.world, this.getPosX() - (double)0.3F, this.getPosX() + (double)0.3F, this.getPosZ() - (double)0.3F, this.getPosZ() + (double)0.3F, this.getPosYHeight(0.5D), new BlockParticleData(ParticleTypes.FALLING_DUST, Blocks.DIAMOND_BLOCK.getDefaultState()));
			}
		}
	}

	private void addParticle(World worldIn, double p_226397_2_, double p_226397_4_, double p_226397_6_, double p_226397_8_, double posY, IParticleData particleData) {
		worldIn.addParticle(particleData, MathHelper.lerp(worldIn.rand.nextDouble(), p_226397_2_, p_226397_4_), posY, MathHelper.lerp(worldIn.rand.nextDouble(), p_226397_6_, p_226397_8_), 0.0D, 0.0D, 0.0D);
	}

	public static AttributeModifierMap.MutableAttribute prepareAttributes() {
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 8D) // MAX_HEALTH
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.7D); // MOVEMENT_SPEED
	}

}
