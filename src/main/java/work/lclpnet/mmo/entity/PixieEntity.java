package work.lclpnet.mmo.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Npc;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import work.lclpnet.mmo.module.PixieModule;
import work.lclpnet.mmo.network.MMODataSerializers;
import work.lclpnet.mmo.sound.MMOSounds;
import work.lclpnet.mmo.util.EntityHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class PixieEntity extends TameableEntity implements Npc, Flutterer, IAnimatable {

    public static final TrackedData<Vec3d> TARGET = DataTracker.registerData(PixieEntity.class, MMODataSerializers.VEC3D);
    public static final TrackedData<Boolean> STRICT_TARGET = DataTracker.registerData(PixieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Float> BASE_SPEED = DataTracker.registerData(PixieEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private EscapeDangerGoal panicGoal;
    private WanderGoal wanderGoal;
    private SwimGoal swimGoal;
    private FollowOwnerGoal followOwnerGoal;

    @Environment(EnvType.CLIENT)
    protected AnimationFactory factory;

    public PixieEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 20, true);

        if (world.isClient) this.factory = new AnimationFactory(this);
    }

    public PixieEntity(World world) {
        this(PixieModule.pixieEntityType, world);
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return world.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(99, new WanderToGoal());
        registerDefaultPixieGoals();
    }

    private void registerDefaultPixieGoals() {
        if (panicGoal == null)
            this.goalSelector.add(0, panicGoal = new EscapeDangerGoal(this, 1.25D));
        if (wanderGoal == null)
            this.goalSelector.add(8, wanderGoal = new WanderGoal());
        if (swimGoal == null)
            this.goalSelector.add(9, swimGoal = new SwimGoal(this));
        if (followOwnerGoal == null)
            this.goalSelector.add(8, followOwnerGoal = new FollowOwnerGoal(this, 1F, 10F, 2F, true));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TARGET, null);
        this.dataTracker.startTracking(STRICT_TARGET, false);
        this.dataTracker.startTracking(BASE_SPEED, 0.6F);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation navigation = new BirdNavigation(this, world) {

            @Override
            public boolean isValidPosition(BlockPos pos) {
                return !this.world.getBlockState(pos.down()).isAir();
            }
        };

        navigation.setCanPathThroughDoors(false);
        navigation.setCanSwim(false);
        navigation.setCanEnterOpenDoors(true);

        return navigation;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return MMOSounds.ENTITY_PIXIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(@Nullable DamageSource damageSourceIn) {
        return MMOSounds.ENTITY_PIXIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MMOSounds.ENTITY_PIXIE_DEATH;
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
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        // NO-OP
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean isInAir() {
        return !this.onGround;
    }

    @Override
    public void tick() {
        super.tick();
        boolean client = this.world.isClient;

        if (!client && this.isStrictTarget()) {
            double sqDis = squaredDistanceToTarget();
            if (sqDis >= 0.06D && sqDis <= 1D) {
                this.navigation.stop();
                Vec3d target = this.getPixieTarget();
                EntityHelper.teleport(this, (ServerWorld) this.world, target.x, target.y, target.z, this.getYaw(), this.getPitch());
            }
        }

        if (client && this.random.nextFloat() < 0.1F) {
            for (int i = 0; i < this.random.nextInt(2) + 1; ++i) {
                this.addParticle(this.world, this.getX() - (double) 0.3F, this.getX() + (double) 0.3F, this.getZ() - (double) 0.3F, this.getZ() + (double) 0.3F, this.getBodyY(0.5D), new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, Blocks.DIAMOND_BLOCK.getDefaultState()));
            }
        }
    }

    private void addParticle(World worldIn, double xMin, double xMax, double zMin, double zMax, double y, ParticleEffect particleData) {
        worldIn.addParticle(particleData, MathHelper.lerp(worldIn.random.nextDouble(), xMin, xMax), y, MathHelper.lerp(worldIn.random.nextDouble(), zMin, zMax), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public boolean cannotDespawn() {
        return super.cannotDespawn() || this.getOwnerUuid() != null;
    }

    @Override
    protected int getXpToDrop(PlayerEntity player) {
        return 1 + this.world.random.nextInt(2);
    }

    public void setPixieTarget(Vec3d target) {
        this.dataTracker.set(TARGET, target);
    }

    public Vec3d getPixieTarget() {
        return this.dataTracker.get(TARGET);
    }

    public void setPixieSpeed(float speed) {
        this.dataTracker.set(BASE_SPEED, speed);
    }

    public float getPixieSpeed() {
        return this.dataTracker.get(BASE_SPEED);
    }

    public void setStrictTarget(boolean strictTarget) {
        this.dataTracker.set(STRICT_TARGET, strictTarget);
    }

    public boolean isStrictTarget() {
        return this.dataTracker.get(STRICT_TARGET);
    }

    public double squaredDistanceToTarget() {
        return this.squaredDistanceTo(this.getPixieTarget());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);

        Vec3d target = this.getPixieTarget();
        if (target != null) {
            tag.putDouble("TargetX", target.x);
            tag.putDouble("TargetY", target.y);
            tag.putDouble("TargetZ", target.z);
        }
        tag.putBoolean("StrictTarget", this.isStrictTarget());
        tag.putFloat("BaseSpeed", this.getPixieSpeed());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);

        if (tag.contains("TargetX") && tag.contains("TargetY") && tag.contains("TargetZ")) {
            double tx = tag.getDouble("TargetX");
            double ty = tag.getDouble("TargetY");
            double tz = tag.getDouble("TargetZ");
            setPixieTarget(new Vec3d(tx, ty, tz));
        }

        setStrictTarget(tag.contains("StrictTarget") && tag.getBoolean("StrictTarget"));
        if (tag.contains("BaseSpeed")) setPixieSpeed(tag.getFloat("BaseSpeed"));
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 8D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.7D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48D);
    }

    private <E extends IAnimatable> PlayState flutterPredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.pixie.flutter", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "flutter", 0, this::flutterPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    class WanderToGoal extends Goal {

        WanderToGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return PixieEntity.this.getTarget() != null && squaredDistanceToTarget() > 0.75D * 0.75D;
        }

        private double calcYDistanceSq() {
            double dy = PixieEntity.this.getPixieTarget().y - PixieEntity.this.getPos().y;
            return dy * dy;
        }

        private double calcXZDistanceSq() {
            Vec3d v = PixieEntity.this.getPos();

            Vec3d pixieTarget = PixieEntity.this.getPixieTarget();
            double dx = pixieTarget.x - v.x,
                    dz = pixieTarget.z - v.z;

            return dx * dx + dz * dz;
        }

        @Override
        public boolean shouldContinue() {
            return PixieEntity.this.getTarget() != null && PixieEntity.this.navigation.isFollowingPath();
        }

        @Override
        public void tick() {
            PixieEntity pixie = PixieEntity.this;

            LivingEntity owner = pixie.getOwner();
            if (owner != null) {
                if (owner.squaredDistanceTo(pixie) > 225D) {
                    pixie.navigation.stop();
                    pixie.lookControl.lookAt(owner.getPos());
                } else if (!pixie.navigation.isFollowingPath()) startExecuting();
            }
        }

        public void startExecuting() {
            Vec3d target = PixieEntity.this.getPixieTarget();
            if (target != null) {
                double speed = calcYDistanceSq() < 16D && calcXZDistanceSq() < 4D ? 0.3D : getPixieSpeed(); // go slower when near to the target
                PixieEntity.this.navigation.startMovingAlong(PixieEntity.this.navigation.findPathTo(target.x, target.y, target.z, 0), speed);
            }
        }
    }

    class WanderGoal extends Goal {
        WanderGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canStart() {
            return PixieEntity.this.navigation.isIdle() && PixieEntity.this.random.nextInt(10) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinue() {
            return PixieEntity.this.navigation.isIdle();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            Vec3d vec3d = this.getTargetLocation();
            if (vec3d != null)
                PixieEntity.this.navigation.startMovingAlong(PixieEntity.this.navigation.findPathTo(new BlockPos(vec3d), 0), getPixieSpeed());
        }

        @Nullable
        protected Vec3d getTargetLocation() {
            Vec3d rotation = PixieEntity.this.getRotationVec(0.0F);
            Vec3d randomPos = AboveGroundTargeting.find(PixieEntity.this, 8, 7, rotation.x, rotation.z, (float) Math.PI / 2F, 4, 1);

            if (randomPos == null)
                randomPos = NoPenaltySolidTargeting.find(PixieEntity.this, 8, 4, -2, rotation.x, rotation.z, Math.PI / 2D);

            return randomPos;
        }
    }
}
