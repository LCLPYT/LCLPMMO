package work.lclpnet.mmo.entity;

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
import net.minecraft.entity.ai.goal.*;
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
import work.lclpnet.corebase.util.EntityHelper;
import work.lclpnet.mmo.audio.MMOSoundEvents;
import work.lclpnet.mmo.util.MMODataSerializers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.EnumSet;

public class PixieEntity extends TameableEntity implements INPC, IFlyingAnimal, ILimitTracking {

    public static final DataParameter<Boolean> TUTORIAL = EntityDataManager.createKey(PixieEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Vector3d> TARGET = EntityDataManager.createKey(PixieEntity.class, MMODataSerializers.VECTOR_3D);
    public static final DataParameter<Boolean> STRICT_TARGET = EntityDataManager.createKey(PixieEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Float> BASE_SPEED = EntityDataManager.createKey(PixieEntity.class, DataSerializers.FLOAT);

    private PanicGoal panicGoal;
    private WanderGoal wanderGoal;
    private SwimGoal swimGoal;
    private FollowOwnerGoal followOwnerGoal;
    private LookAtWithoutMovingGoal tutorialLookGoal;

    public PixieEntity(World worldIn) {
        super(MMOEntities.PIXIE, worldIn);
        this.moveController = new FlyingMovementController(this, 20, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getBlockPathWeight(@Nonnull BlockPos pos, IWorldReader worldIn) {
        return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(99, new WanderToGoal());
        registerDynamicPixieGoals();
    }

    private void registerDynamicPixieGoals() {
        if (panicGoal == null) this.goalSelector.addGoal(0, panicGoal = new PanicGoal(this, 1.25D));
        if (wanderGoal == null) this.goalSelector.addGoal(8, wanderGoal = new WanderGoal());
        if (swimGoal == null) this.goalSelector.addGoal(9, swimGoal = new SwimGoal(this));
        if (followOwnerGoal == null)
            this.goalSelector.addGoal(8, followOwnerGoal = new FollowOwnerGoal(this, 1F, 10F, 2F, true));
    }

    private void registerTutorialGoals() {
        if (tutorialLookGoal == null)
            this.goalSelector.addGoal(5, tutorialLookGoal = new LookAtWithoutMovingGoal(this, PlayerEntity.class, 10.0F, 1.0F));
    }

    private void unregisterPixieGoals() {
        if (panicGoal != null) this.goalSelector.removeGoal(panicGoal);
        if (wanderGoal != null) this.goalSelector.removeGoal(wanderGoal);
        if (swimGoal != null) this.goalSelector.removeGoal(swimGoal);
        if (followOwnerGoal != null) this.goalSelector.removeGoal(followOwnerGoal);

        panicGoal = null;
        wanderGoal = null;
        swimGoal = null;
        followOwnerGoal = null;
    }

    private void unregisterTutorialGoals() {
        if (tutorialLookGoal != null) this.goalSelector.removeGoal(tutorialLookGoal);

        tutorialLookGoal = null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(TUTORIAL, false);
        this.dataManager.register(TARGET, null);
        this.dataManager.register(STRICT_TARGET, false);
        this.dataManager.register(BASE_SPEED, 0.6F);
    }

    @Nonnull
    protected PathNavigator createNavigator(@Nonnull World worldIn) {
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
    protected SoundEvent getHurtSound(@Nullable DamageSource damageSourceIn) {
        return MMOSoundEvents.ENTITY_PIXIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MMOSoundEvents.ENTITY_PIXIE_DEATH;
    }

    @Override
    @Nonnull
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
    @ParametersAreNullableByDefault
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    @Nullable
    @ParametersAreNullableByDefault
    public AgeableEntity createChild(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    }

    @Override
    @ParametersAreNullableByDefault
    public boolean canMateWith(AnimalEntity otherAnimal) {
        return false;
    }

    @Override
    @ParametersAreNullableByDefault
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @Override
    public boolean shouldBeTrackedBy(ServerPlayerEntity player) {
        return !this.isTamed() || !this.isTutorialPixie() || this.isOwner(player);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote && this.isStrictTarget()) {
            double sqDis = squareDistanceToTarget();
            if (sqDis >= 0.06D && sqDis <= 1D) {
                this.navigator.setPath(null, 0D);
                Vector3d tar = this.getTarget();
                EntityHelper.teleport(this, (ServerWorld) this.world, tar.x, tar.y, tar.z, this.rotationYaw, this.rotationPitch);
            }
        }
        if (this.rand.nextFloat() < 0.1F) {
            for (int i = 0; i < this.rand.nextInt(2) + 1; ++i) {
                this.addParticle(this.world, this.getPosX() - (double) 0.3F, this.getPosX() + (double) 0.3F, this.getPosZ() - (double) 0.3F, this.getPosZ() + (double) 0.3F, this.getPosYHeight(0.5D), new BlockParticleData(ParticleTypes.FALLING_DUST, Blocks.DIAMOND_BLOCK.getDefaultState()));
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
    public boolean canBePushed() {
        return super.canBePushed() && !this.isTutorialPixie();
    }

    @Override
    @ParametersAreNullableByDefault
    protected int getExperiencePoints(PlayerEntity player) {
        return 1 + this.world.rand.nextInt(2);
    }

    public boolean isTutorialPixie() {
        return this.dataManager.get(TUTORIAL);
    }

    public void setTutorialPixie(boolean tutorialPixie) {
        this.dataManager.set(TUTORIAL, tutorialPixie);
        if (tutorialPixie) {
            unregisterPixieGoals();
            registerTutorialGoals();
        } else {
            unregisterTutorialGoals();
            registerDynamicPixieGoals();
        }
    }

    public void setTarget(Vector3d target) {
        this.dataManager.set(TARGET, target);
    }

    public Vector3d getTarget() {
        return this.dataManager.get(TARGET);
    }

    public void setBaseSpeed(float baseSpeed) {
        this.dataManager.set(BASE_SPEED, baseSpeed);
    }

    public float getBaseSpeed() {
        return this.dataManager.get(BASE_SPEED);
    }

    public void setStrictTarget(boolean strictTarget) {
        this.dataManager.set(STRICT_TARGET, strictTarget);
    }

    public boolean isStrictTarget() {
        return this.dataManager.get(STRICT_TARGET);
    }

    public double squareDistanceToTarget() {
        return this.getTarget().squareDistanceTo(this.getPositionVec());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Tutorial", this.isTutorialPixie());

        Vector3d tar = this.getTarget();
        compound.putBoolean("HasTarget", tar != null);
        if (tar != null) {
            compound.putDouble("TargetX", tar.x);
            compound.putDouble("TargetY", tar.y);
            compound.putDouble("TargetZ", tar.z);
        }
        compound.putBoolean("StrictTarget", this.isStrictTarget());
        compound.putFloat("BaseSpeed", this.getBaseSpeed());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        setTutorialPixie(compound.getBoolean("Tutorial"));

        if (compound.getBoolean("HasTarget")) {
            double tx = compound.getDouble("TargetX");
            double ty = compound.getDouble("TargetY");
            double tz = compound.getDouble("TargetZ");
            setTarget(new Vector3d(tx, ty, tz));
        }

        setStrictTarget(compound.getBoolean("StrictTarget"));
        if (compound.contains("BaseSpeed")) setBaseSpeed(compound.getFloat("BaseSpeed"));
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
            return PixieEntity.this.getTarget() != null && squareDistanceToTarget() > 0.75D * 0.75D;
        }

        private double ySquareDistanceToTarget() {
            double dy = PixieEntity.this.getTarget().y - PixieEntity.this.getPositionVec().y;
            return dy * dy;
        }

        private double xzSquareDistanceToTarget() {
            Vector3d v = PixieEntity.this.getPositionVec();
            double dx = PixieEntity.this.getTarget().x - v.x,
                    dz = PixieEntity.this.getTarget().z - v.z;
            return dx * dx + dz * dz;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return PixieEntity.this.getTarget() != null && PixieEntity.this.navigator.hasPath();
        }

        @Override
        public void tick() {
            PixieEntity pixie = PixieEntity.this;

            LivingEntity owner = pixie.getOwner();
            if (owner != null) {
                if (owner.getPositionVec().squareDistanceTo(pixie.getPositionVec()) > 225D) {
                    pixie.navigator.setPath(null, 0D);
                    pixie.lookController.setLookPosition(owner.getPositionVec());
                } else if (!pixie.navigator.hasPath())
                    startExecuting();
            }
        }

        public void startExecuting() {
            Vector3d vector3d = PixieEntity.this.getTarget();
            if (vector3d != null) {
                double speed = ySquareDistanceToTarget() < 16D && xzSquareDistanceToTarget() < 4D ? 0.3D : getBaseSpeed(); // go slower when near to the target
                PixieEntity.this.navigator.setPath(PixieEntity.this.navigator.getPathToPos(new BlockPos(vector3d), 0), speed);
            }
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
                PixieEntity.this.navigator.setPath(PixieEntity.this.navigator.getPathToPos(new BlockPos(vector3d), 0), getBaseSpeed());
        }

        @Nullable
        protected Vector3d getTargetLocation() {
            Vector3d vector3d = PixieEntity.this.getLook(0.0F);
            Vector3d vector3d2 = RandomPositionGenerator.findAirTarget(PixieEntity.this, 8, 7, vector3d, (float) Math.PI / 2F, 2, 1);
            return vector3d2 != null ? vector3d2 : RandomPositionGenerator.findGroundTarget(PixieEntity.this, 8, 4, -2, vector3d, Math.PI / 2D);
        }
    }
}
