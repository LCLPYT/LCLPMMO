package work.lclpnet.mmo.entity;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.INPC;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtWithoutMovingGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import work.lclpnet.mmo.audio.MMOSoundEvents;

public class PixieEntity extends TameableEntity implements INPC, IFlyingAnimal, ILimitTracking {

	public static final DataParameter<Boolean> TUTORIAL = EntityDataManager.createKey(PixieEntity.class, DataSerializers.BOOLEAN);
	private PanicGoal panicGoal;
	private WanderGoal wanderGoal;
	private SwimGoal swimGoal;
	private FollowOwnerGoal followOwnerGoal;
	private LookAtWithoutMovingGoal tutorialLookGoal;
	private Vector3d target = null;

	public PixieEntity(World worldIn) {
		super(MMOEntities.PIXIE, worldIn);
		this.moveController = new FlyingMovementController(this, 20, true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
		return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(99, new WanderToGoal());
		registerDynamicPixieGoals();
	}

	private void registerDynamicPixieGoals() {
		if(panicGoal == null) this.goalSelector.addGoal(0, panicGoal = new PanicGoal(this, 1.25D));
		if(wanderGoal == null) this.goalSelector.addGoal(8, wanderGoal = new WanderGoal());
		if(swimGoal == null) this.goalSelector.addGoal(9, swimGoal = new SwimGoal(this));
		if(followOwnerGoal == null) this.goalSelector.addGoal(8, followOwnerGoal = new FollowOwnerGoal(this, 1F, 10F, 2F, true));
	}
	
	private void registerTutorialGoals() {
		if(tutorialLookGoal == null) this.goalSelector.addGoal(5, tutorialLookGoal = new LookAtWithoutMovingGoal(this, PlayerEntity.class, 10.0F, 1.0F));
	}
	
	private void unregisterPixieGoals() {
		if(panicGoal != null) this.goalSelector.removeGoal(panicGoal);
		if(wanderGoal != null) this.goalSelector.removeGoal(wanderGoal);
		if(swimGoal != null) this.goalSelector.removeGoal(swimGoal);
		if(followOwnerGoal != null) this.goalSelector.removeGoal(followOwnerGoal);
		
		panicGoal = null;
		wanderGoal = null;
		swimGoal = null;
		followOwnerGoal = null;
	}
	
	private void unregisterTutorialGoals() {
		if(tutorialLookGoal != null) this.goalSelector.removeGoal(tutorialLookGoal);
		
		tutorialLookGoal = null;
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(TUTORIAL, false);
	}
	
	protected PathNavigator createNavigator(World worldIn) {
		FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn) {

			@SuppressWarnings("deprecation")
			@Override
			public boolean canEntityStandOnPos(BlockPos pos) {
				return !this.world.getBlockState(pos.down()).isAir();
			}

		};
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanSwim(false);
		flyingpathnavigator.setCanEnterDoors(true);
		return flyingpathnavigator;
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
	public SoundCategory getSoundCategory() {
		return SoundCategory.NEUTRAL;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}
	
	@Override
	public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
		return null;
	}
	
	@Override
	public boolean canMateWith(AnimalEntity otherAnimal) {
		return false;
	}
	
	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isChild() {
		return false;
	}
	
	@Override
	public boolean shouldBeTrackedBy(ServerPlayerEntity player) {
		return this.isTamed() && this.isTutorialPixie() ? this.isOwner(player) : true;
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

	@Override
	public boolean canDespawn(double distanceToClosestPlayer) {
		return false;
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return super.canBeCollidedWith() && !this.isTutorialPixie();
	}
	
	@Override
	public boolean canBePushed() {
		return super.canBePushed() && !this.isTutorialPixie();
	}

	@Override
	protected int getExperiencePoints(PlayerEntity player) {
		return 1 + this.world.rand.nextInt(2);
	}

	public boolean isTutorialPixie() {
		return this.dataManager.get(TUTORIAL);
	}

	public void setTutorialPixie(boolean tutorialPixie) {
		this.dataManager.set(TUTORIAL, tutorialPixie);
		if(tutorialPixie) {
			unregisterPixieGoals();
			registerTutorialGoals();
		}
		else {
			unregisterTutorialGoals();
			registerDynamicPixieGoals();
		}
	}
	
	public void setTarget(Vector3d target) {
		this.target = target;
	}
	
	public Vector3d getTarget() {
		return target;
	}
	
	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putBoolean("Tutorial", this.isTutorialPixie());
	}
	
	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		setTutorialPixie(compound.getBoolean("Tutorial"));
	}

	public static AttributeModifierMap.MutableAttribute prepareAttributes() {
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 8D)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.7D)
				.createMutableAttribute(Attributes.FLYING_SPEED, 0.6D)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 48D);
	}
	
	class WanderToGoal extends Goal {

		WanderToGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}
		
		@Override
		public boolean shouldExecute() {
			return PixieEntity.this.target != null && squareDistanceToTarget() > 0.75D * 0.75D;
		}

		private double squareDistanceToTarget() {
			return PixieEntity.this.target.squareDistanceTo(PixieEntity.this.getPositionVec());
		}
		
		private double ySquareDistanceToTarget() {
			double dy = PixieEntity.this.target.y - PixieEntity.this.getPositionVec().y;
			return dy * dy;
		}
		
		private double xzSquareDistanceToTarget() {
			Vector3d v = PixieEntity.this.getPositionVec();
			double dx = PixieEntity.this.target.x - v.x,
					dz = PixieEntity.this.target.z - v.z;
			return dx * dx + dz * dz;
		}
		
		@Override
		public boolean shouldContinueExecuting() {
			return PixieEntity.this.target != null && PixieEntity.this.navigator.hasPath();
		}
		
		@Override
		public void tick() {
			LivingEntity owner = PixieEntity.this.getOwner();
			if(owner == null) return;
			
			if(owner.getPositionVec().squareDistanceTo(PixieEntity.this.getPositionVec()) > 225D) {
				PixieEntity.this.navigator.setPath(null, 0D);
				PixieEntity.this.lookController.setLookPosition(owner.getPositionVec());
			}
			else if(!PixieEntity.this.navigator.hasPath())
				startExecuting();
		}
		
		public void startExecuting() {
			Vector3d vector3d = this.getTargetLocation();
			if (vector3d != null) {
				double speed = ySquareDistanceToTarget() < 16D && xzSquareDistanceToTarget() < 4D ? 0.3D : 0.6D; // go slower when near to the target
				PixieEntity.this.navigator.setPath(PixieEntity.this.navigator.getPathToPos(new BlockPos(vector3d), 0), speed);
			}
		}
		
		protected Vector3d getTargetLocation() {
			return PixieEntity.this.target;
		}
		
	}

	class WanderGoal extends Goal {
		WanderGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		public boolean shouldExecute() {
			return PixieEntity.this.navigator.noPath() && PixieEntity.this.rand.nextInt(10) == 0;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return PixieEntity.this.navigator.hasPath();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			Vector3d vector3d = this.getTargetLocation();
			if (vector3d != null) 
				PixieEntity.this.navigator.setPath(PixieEntity.this.navigator.getPathToPos(new BlockPos(vector3d), 0), 0.6D);
		}

		@Nullable
		protected Vector3d getTargetLocation() {
			Vector3d vector3d = PixieEntity.this.getLook(0.0F);
			Vector3d vector3d2 = RandomPositionGenerator.findAirTarget(PixieEntity.this, 8, 7, vector3d, (float) Math.PI / 2F, 2, 1);
			return vector3d2 != null ? vector3d2 : RandomPositionGenerator.findGroundTarget(PixieEntity.this, 8, 4, -2, vector3d, Math.PI / 2D);
		}
	}

}
