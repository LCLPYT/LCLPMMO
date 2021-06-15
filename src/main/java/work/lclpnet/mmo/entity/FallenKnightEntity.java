package work.lclpnet.mmo.entity;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.vector.Vector3d;
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
import work.lclpnet.mmo.util.MMODataSerializers;

public class FallenKnightEntity extends MonsterEntity implements IAnimatable {

    public static final DataParameter<Vector3d> HOME_LOCATION = EntityDataManager.createKey(FallenKnightEntity.class, MMODataSerializers.VECTOR_3D);
    public static final DataParameter<Float> HOME_YAW = EntityDataManager.createKey(FallenKnightEntity.class, DataSerializers.FLOAT);

    /*  Client-Only Fields
    ! IMPORTANT !
    Do not to initialize them here, since an exception will be thrown on the server.
    */
    @OnlyIn(Dist.CLIENT)
    protected AnimationFactory factory;
    @OnlyIn(Dist.CLIENT)
    protected boolean attackAnimationEnabled,
            shieldAnimationEnabled;

    public FallenKnightEntity(World worldIn) {
        super(MMOEntities.FALLEN_KNIGHT, worldIn);
        this.ignoreFrustumCheck = true;

        // Initialize client-only fields here to prevent exceptions on the server.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            factory = new AnimationFactory(this);
        }
    }

    public static AttributeModifierMap.MutableAttribute prepareAttributes() {
        return MonsterEntity.func_234295_eP_()
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 30.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 9.0D)
                .createMutableAttribute(Attributes.ARMOR, 25.0D)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(HOME_LOCATION, new Vector3d(-.5, 60, -.5));
        this.dataManager.register(HOME_YAW, 0.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new SwimGoal(this));
        this.goalSelector.addGoal(1, new FallenKnightReturnGoal((MobEntity) this.getEntity(), this));
        this.applyEntityAI();
    }

    protected void applyEntityAI() {
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true));
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
        return 4.35F;
    }

    public static class FallenKnightReturnGoal extends Goal {

        private final MobEntity creature;
        private final MonsterEntity creatureClass;

        public FallenKnightReturnGoal(MobEntity entity, MonsterEntity fallenKnightEnitity) {
            creature = entity;
            creatureClass = fallenKnightEnitity;
        }

        @Override
        public boolean shouldExecute() {
            return this.creature.getAttackTarget() == null;
        }

        @Override
        public void startExecuting() {
            Vector3d home_loc = this.creatureClass.getDataManager().get(HOME_LOCATION);
            Path path = this.creature.getNavigator().pathfind(home_loc.getX(), home_loc.getY(), home_loc.getZ(), 0);
            this.creature.getNavigator().setPath(path, 1);
        }

        @Override
        public boolean shouldContinueExecuting() {
            if (!this.creature.getNavigator().hasPath()) {
                Vector3d home_loc = this.creatureClass.getDataManager().get(HOME_LOCATION);
                Float home_yaw = this.creatureClass.getDataManager().get(HOME_YAW);
                if (this.creature.getPositionVec().isWithinDistanceOf(home_loc, 1)) {
                    this.creature.setPositionAndRotation(home_loc.getX(), home_loc.getY(), home_loc.getZ(), home_yaw, 0.0F);
                }
                return false;
            } else {
                return this.creature.getAttackTarget() == null;
            }
        }
    }
}
