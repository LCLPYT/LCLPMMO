package work.lclpnet.mmo.entity;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;

public class EquesterEntity extends WaterMobEntity implements IAnimatable {


    private static final EntityPredicate field_213810_bA = (new EntityPredicate()).setDistance(10.0D).allowFriendlyFire().allowInvulnerable().setIgnoresLineOfSight();

    /*  Client-Only Fields
    ! IMPORTANT !
    Do not to initialize them here, since an exception will be thrown on the server.
    */
    @OnlyIn(Dist.CLIENT)
    protected AnimationFactory factory;
    @OnlyIn(Dist.CLIENT)
    protected boolean attackAnimationEnabled,
            shieldAnimationEnabled;

    public EquesterEntity(World worldIn) {
        super(MMOEntities.EQUESTER, worldIn);
        this.ignoreFrustumCheck = true;

        // Initialize client-only fields here to prevent exceptions on the server.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            factory = new AnimationFactory(this);
        }
    }

    public static AttributeModifierMap.MutableAttribute prepareAttributes() {
        return MonsterEntity.func_234295_eP_()
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 2D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 9.0D);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void registerData() {
        super.registerData();
        //this.dataManager.register(HOME_LOCATION, new Vector3d(-.5, 60, -.5));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreatheAirGoal(this));
        this.goalSelector.addGoal(0, new FindWaterGoal(this));
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new EquesterEntity.SwimWithPlayerGoal(this, 4.0D));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.2F, true));
        this.goalSelector.addGoal(8, new FollowBoatGoal(this));
        this.goalSelector.addGoal(9, new AvoidEntityGoal<>(this, GuardianEntity.class, 8.0F, 1.0D, 1.0D));
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "idle", 0, this::idlePredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    private <E extends IAnimatable> PlayState idlePredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.fallen_knight.idle", true));
        return PlayState.CONTINUE;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 2.85F;
    }

    static class SwimWithPlayerGoal extends Goal {
        private final EquesterEntity equester;
        private final double speed;
        private PlayerEntity targetPlayer;

        SwimWithPlayerGoal(EquesterEntity equesterIn, double speedIn) {
            this.equester = equesterIn;
            this.speed = speedIn;
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            this.targetPlayer = this.equester.world.getClosestPlayer(EquesterEntity.field_213810_bA, this.equester);
            if (this.targetPlayer == null) {
                return false;
            } else {
                return this.targetPlayer.isSwimming() && this.equester.getAttackTarget() != this.targetPlayer;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return this.targetPlayer != null && this.targetPlayer.isSwimming() && this.equester.getDistanceSq(this.targetPlayer) < 256.0D;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.targetPlayer.addPotionEffect(new EffectInstance(Effects.DOLPHINS_GRACE, 100));
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            this.targetPlayer = null;
            this.equester.getNavigator().clearPath();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            this.equester.getLookController().setLookPositionWithEntity(this.targetPlayer, (float) (this.equester.getHorizontalFaceSpeed() + 20), (float) this.equester.getVerticalFaceSpeed());
            if (this.equester.getDistanceSq(this.targetPlayer) < 6.25D) {
                this.equester.getNavigator().clearPath();
            } else {
                this.equester.getNavigator().tryMoveToEntityLiving(this.targetPlayer, this.speed);
            }

            if (this.targetPlayer.isSwimming() && this.targetPlayer.world.rand.nextInt(6) == 0) {
                this.targetPlayer.addPotionEffect(new EffectInstance(Effects.DOLPHINS_GRACE, 100));
            }
        }
    }
}
