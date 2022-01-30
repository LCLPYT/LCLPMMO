package work.lclpnet.mmo.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import work.lclpnet.mmo.module.BoletusModule;
import work.lclpnet.mmo.sound.MMOSounds;
import work.lclpnet.mmo.util.MMOAnimations;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class BoletusEntity extends HostileEntity implements Angerable, IAnimatable, IEntitySyncable {

    private static final UUID entityUUID = UUID.fromString("6CC930D2-9F85-11EB-A8B3-0242AC130003");
    private static final IntRange angerTimeRange = Durations.betweenSeconds(20, 39);
    private static final IntRange angerAlliesRange = Durations.betweenSeconds(4, 6);
    private static final IntRange angerSoundRange = Durations.betweenSeconds(0, 1);
    private static final IntRange puffRange = Durations.betweenSeconds(10, 20);
    private static final EntityAttributeModifier attackingSpeedBoost = new EntityAttributeModifier(entityUUID, "Attacking speed boost", 0.175D, EntityAttributeModifier.Operation.ADDITION);
    private static final short ANIMATION_PUFF = 0, ANIMATION_ATTACK = 1;

    protected int angerTime;
    protected int angerNearbyAlliesTimer;
    protected int angerSoundTimer;
    protected int puffTimer;
    protected int puffDelayTimer;
    protected int stepSoundTimer;
    protected UUID angerTarget;

    @Environment(EnvType.CLIENT)
    protected AnimationFactory factory;

    public BoletusEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);

        this.ignoreCameraFrustum = true;

        if (world.isClient) factory = new AnimationFactory(this);
        this.puffTimer = puffRange.choose(this.random);
    }

    public BoletusEntity(World world) {
        this(BoletusModule.boletusEntityType, world);
    }

    @Override
    public int getAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angerTarget;
    }

    @Override
    public void setAngryAt(@Nullable UUID uuid) {
        this.angerTarget = uuid;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(angerTimeRange.choose(this.random));
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));

        this.goalSelector.add(3, new BoletusMeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge(BoletusEntity.class));
        this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new FollowTargetGoal<>(this, PixieEntity.class, 10, false, false, le -> !((PixieEntity) le).isTamed()));
        this.targetSelector.add(4, new FollowTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new UniversalAngerGoal<>(this, true));
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.5D)
                .add(EntityAttributes.GENERIC_ARMOR, 1.5D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.world.isClient) {
            if (stepSoundTimer > 0) stepSoundTimer--;

            if (puffDelayTimer > 0 && --puffDelayTimer <= 0) {
                ServerWorld sw = (ServerWorld) this.world;
                double x = this.getX();
                double y = this.getEyeY() + 0.2D;
                double z = this.getZ();
                sw.spawnParticles(BoletusModule.sporesParticleType, x, y, z, 30, 0.5D, 0.1D, 0.5D, 0.035D);
                sw.playSound(null, x, y, z, MMOSounds.ENTITY_BOLETUS_SPORES, SoundCategory.HOSTILE, this.getSoundVolume(), this.getSoundPitch());

                double distance = 6D;
                Vec3d min = new Vec3d(x - distance, y - distance, z - distance);
                Vec3d max = new Vec3d(x + distance, y + distance, z + distance);
                List<PlayerEntity> entities = this.world.getEntitiesByClass(PlayerEntity.class, new Box(min, max), en -> en.squaredDistanceTo(x, y, z) <= distance * distance);
                entities.forEach(p -> p.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 250, 1)));
            }

            if (!this.isAiDisabled() && puffTimer-- <= 0) {
                puffTimer = puffRange.choose(this.random);
                MMOAnimations.syncEntityAnimation(this, ANIMATION_PUFF);
                puffDelayTimer = 9;
            }
        }
    }

    @Override
    protected void mobTick() {
        EntityAttributeInstance attributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (attributeInstance == null) return;

        if (this.hasAngerTime()) {
            if (!attributeInstance.hasModifier(attackingSpeedBoost))
                attributeInstance.addTemporaryModifier(attackingSpeedBoost);

            this.tickAngerSound();
        } else if (attributeInstance.hasModifier(attackingSpeedBoost)) {
            attributeInstance.removeModifier(attackingSpeedBoost);
        }

        this.tickAngerLogic((ServerWorld) this.world, true);
        if (this.getTarget() != null) this.tickAngerNearby();

        if (this.hasAngerTime()) this.playerHitTimer = this.age;

        super.mobTick();
    }

    private void tickAngerSound() {
        if (this.angerSoundTimer > 0) {
            --this.angerSoundTimer;
            if (this.angerSoundTimer == 0) {
                this.playAngerSound();
            }
        }
    }

    private void tickAngerNearby() {
        if (this.angerNearbyAlliesTimer > 0) {
            --this.angerNearbyAlliesTimer;
        } else {
            if (this.getTarget() != null && this.getVisibilityCache().canSee(this.getTarget())) {
                this.angerNearbyAllies();
            }

            this.angerNearbyAlliesTimer = angerAlliesRange.choose(this.random);
        }
    }

    private void angerNearbyAllies() {
        double range = this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        Box box = Box.method_29968(this.getPos()).expand(range, 10.0D, range);

        LivingEntity attackTarget = this.getTarget();
        if (attackTarget == null) return;

        this.world.getEntitiesIncludingUngeneratedChunks(BoletusEntity.class, box).stream()
                .filter(other -> other != this)
                .filter(other -> other.getTarget() == null)
                .filter(other -> !other.isTeammate(attackTarget))
                .forEach(other -> other.setTarget(attackTarget));
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (this.getTarget() == null && target != null) {
            this.angerSoundTimer = angerSoundRange.choose(this.random);
            this.angerNearbyAlliesTimer = angerAlliesRange.choose(this.random);
        }

        if (target instanceof PlayerEntity)
            this.setAttacking((PlayerEntity) target);

        super.setTarget(target);
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 100;
    }

    @Override
    public boolean tryAttack(Entity target) {
        MMOAnimations.syncEntityAnimation(this, ANIMATION_ATTACK);
        return super.tryAttack(target);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return MMOSounds.ENTITY_BOLETUS_IDLE;
    }

    protected SoundEvent getStepSound() {
        return MMOSounds.ENTITY_BOLETUS_STEP;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (stepSoundTimer <= 0) {
            this.playSound(this.getStepSound(), 0.15F, 1.0F);
            stepSoundTimer = 100;
        }
    }

    private void playAngerSound() {
        this.playSound(MMOSounds.ENTITY_BOLETUS_ANGRY, this.getSoundVolume(), this.getSoundPitch() * 1.3F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return MMOSounds.ENTITY_BOLETUS_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MMOSounds.ENTITY_BOLETUS_DEATH;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        this.angerToTag(tag);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.angerFromTag((ServerWorld) this.world, tag);
    }

    @Override
    public boolean isAngryAt(PlayerEntity player) {
        return this.shouldAngerAt(player);
    }

    private <E extends IAnimatable> PlayState idlePredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.boletus.idle", true));
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState puffPredicate(AnimationEvent<E> event) {
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState attackPredicate(AnimationEvent<E> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "idle", 0, this::idlePredicate));
        // for these animations, specify a transition tick, otherwise they will not be playing
        animationData.addAnimationController(new AnimationController<>(this, "puff", 1, this::puffPredicate));
        animationData.addAnimationController(new AnimationController<>(this, "attack", 1, this::attackPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void onEntityAnimationSync(int state) {
        if (state == ANIMATION_PUFF) {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, getUuid().hashCode(), "puff");
            if (controller.getAnimationState() == AnimationState.Stopped) {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.boletus.puff", false));
            }
        }
        else if (state == ANIMATION_ATTACK) {
            final AnimationController<?> controller = GeckoLibUtil.getControllerForID(this.factory, getUuid().hashCode(), "attack");
            if (controller.getAnimationState() == AnimationState.Stopped) {
                controller.markNeedsReload();
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.boletus.attack", false));
            }
        }
    }

    public static class BoletusMeleeAttackGoal extends Goal {

        protected final PathAwareEntity attacker;
        private final double speedTowardsTarget;
        private final boolean longMemory;
        private Path path;
        private double targetX;
        private double targetY;
        private double targetZ;
        private int delayCounter;
        private int swingCooldown;
        private long lastCheckTime;
        private int failedPathFindingPenalty = 0;
        private final boolean canPenalize = false;

        public BoletusMeleeAttackGoal(PathAwareEntity creature, double speedIn, boolean useLongMemory) {
            this.attacker = creature;
            this.speedTowardsTarget = speedIn;
            this.longMemory = useLongMemory;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean canStart() {
            long i = this.attacker.world.getTime();
            if (i - this.lastCheckTime < 20L) {
                return false;
            } else {
                this.lastCheckTime = i;
                LivingEntity livingentity = this.attacker.getTarget();
                if (livingentity == null) {
                    return false;
                } else if (!livingentity.isAlive()) {
                    return false;
                } else {
                    if (canPenalize) {
                        if (--this.delayCounter <= 0) {
                            this.path = this.attacker.getNavigation().findPathTo(livingentity, 0);
                            this.delayCounter = 4 + this.attacker.getRandom().nextInt(7);
                            return this.path != null;
                        } else {
                            return true;
                        }
                    }
                    this.path = this.attacker.getNavigation().findPathTo(livingentity, 0);
                    if (this.path != null) {
                        return true;
                    } else {
                        return this.getAttackReachSqr(livingentity) >= this.attacker.squaredDistanceTo(livingentity);
                    }
                }
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        @Override
        public boolean shouldContinue() {
            LivingEntity target = this.attacker.getTarget();
            if (target == null) {
                return false;
            } else if (!target.isAlive()) {
                return false;
            } else if (!this.longMemory) {
                return !this.attacker.getNavigation().isIdle();
            } else if (!this.attacker.isInWalkTargetRange(target.getBlockPos())) {
                return false;
            } else {
                return !(target instanceof PlayerEntity) || !target.isSpectator() && !((PlayerEntity)target).isCreative();
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        @Override
        public void start() {
            this.attacker.getNavigation().startMovingAlong(this.path, this.speedTowardsTarget);
            this.attacker.setAttacking(true);
            this.delayCounter = 0;
            this.swingCooldown = 0;
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        @Override
        public void stop() {
            LivingEntity target = this.attacker.getTarget();
            if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(target)) {
                this.attacker.setTarget(null);
            }

            this.attacker.setAttacking(false);
            this.attacker.getNavigation().stop();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        @Override
        public void tick() {
            LivingEntity target = this.attacker.getTarget();
            if(target == null) return;

            this.attacker.getLookControl().lookAt(target, 30.0F, 30.0F);
            double d0 = this.attacker.squaredDistanceTo(target);
            this.delayCounter = Math.max(this.delayCounter - 1, 0);
            if ((this.longMemory || this.attacker.getVisibilityCache().canSee(target))
                    && this.delayCounter <= 0 && (
                            this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D
                                    || target.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0D
                                    || this.attacker.getRandom().nextFloat() < 0.05F
            )) {
                this.targetX = target.getX();
                this.targetY = target.getY();
                this.targetZ = target.getZ();
                this.delayCounter = 4 + this.attacker.getRandom().nextInt(7);
                if (this.canPenalize) {
                    this.delayCounter += failedPathFindingPenalty;
                    if (this.attacker.getNavigation().getCurrentPath() != null) {
                        PathNode end = this.attacker.getNavigation().getCurrentPath().getEnd();
                        if (end != null && target.squaredDistanceTo(end.x, end.y, end.z) < 1) {
                            failedPathFindingPenalty = 0;
                        } else {
                            failedPathFindingPenalty += 10;
                        }
                    } else {
                        failedPathFindingPenalty += 10;
                    }
                }
                if (d0 > 1024.0D) {
                    this.delayCounter += 10;
                } else if (d0 > 256.0D) {
                    this.delayCounter += 5;
                }

                if (!this.attacker.getNavigation().startMovingTo(target, this.speedTowardsTarget)) {
                    this.delayCounter += 15;
                }
            }

            this.swingCooldown = Math.max(this.swingCooldown - 1, 0);
            this.checkAndPerformAttack(target, d0);
        }

        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.swingCooldown <= 0) {
                this.resetSwingCooldown();
                this.attacker.swingHand(Hand.MAIN_HAND);
                this.attacker.tryAttack(enemy);
            }
        }

        protected void resetSwingCooldown() {
            this.swingCooldown = 20;
        }

        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return this.attacker.getWidth() * 2.0F * this.attacker.getWidth() * 2.0F + attackTarget.getWidth();
        }
    }
}
