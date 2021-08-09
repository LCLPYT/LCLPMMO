package work.lclpnet.mmo.entity;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.nbt.CompoundNBT;
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

import javax.annotation.ParametersAreNonnullByDefault;

public class NPCEntity extends MonsterEntity implements IAnimatable {
    
    public static final DataParameter<Vector3d> TARGET_LOCATION = EntityDataManager.createKey(NPCEntity.class, MMODataSerializers.VECTOR_3D);
    public static final DataParameter<Float> TARGET_YAW = EntityDataManager.createKey(NPCEntity.class, DataSerializers.FLOAT);

    /*  Client-Only Fields
    ! IMPORTANT !
    Do not to initialize them here, since an exception will be thrown on the server.
    */
    @OnlyIn(Dist.CLIENT)
    protected AnimationFactory factory;
    @OnlyIn(Dist.CLIENT)
    protected boolean attackAnimationEnabled;

    public NPCEntity(World worldIn) {
        super(MMOEntities.NPC, worldIn);
        this.ignoreFrustumCheck = true;

        // Initialize client-only fields here to prevent exceptions on the server.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            factory = new AnimationFactory(this);
        }
    }

    public Vector3d getTargetLocation() {
        return this.dataManager.get(TARGET_LOCATION);
    }

    public void setTargetLocation(Vector3d location) {
        this.dataManager.set(TARGET_LOCATION, location);
    }

    public Float getTargetYaw() {
        return this.dataManager.get(TARGET_YAW);
    }

    public void setTargetYaw(Float yaw) {
        this.dataManager.set(TARGET_YAW, yaw);
    }

    public static AttributeModifierMap.MutableAttribute prepareAttributes() {
        return MonsterEntity.func_234295_eP_()
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 30.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D)
                .createMutableAttribute(Attributes.ARMOR, 5.0D)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(TARGET_LOCATION, new Vector3d(-.5, 60, -.5));
        this.dataManager.register(TARGET_YAW, 0.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new NPCEntity.NPCGoToGoal((MobEntity) this.getEntity(), this));
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

    public static class NPCGoToGoal extends Goal {

        private final MobEntity creature;
        private final MonsterEntity creatureClass;

        public NPCGoToGoal(MobEntity entity, MonsterEntity fallenKnightEnitity) {
            creature = entity;
            creatureClass = fallenKnightEnitity;
        }

        @Override
        public boolean shouldExecute() {
            return this.creature.getAttackTarget() == null;
        }

        @Override
        public void startExecuting() {
            Vector3d target_loc = this.creatureClass.getDataManager().get(TARGET_LOCATION);
            Path path = this.creature.getNavigator().pathfind(target_loc.getX(), target_loc.getY(), target_loc.getZ(), 0);
            this.creature.getNavigator().setPath(path, 1);
        }

        @Override
        public boolean shouldContinueExecuting() {
            if (!this.creature.getNavigator().hasPath()) {
                Vector3d target_loc = this.creatureClass.getDataManager().get(TARGET_LOCATION);
                Float target_yaw = this.creatureClass.getDataManager().get(TARGET_YAW);
                if (this.creature.getPositionVec().isWithinDistanceOf(target_loc, 1)) {
                    this.creature.setPositionAndRotation(target_loc.getX(), target_loc.getY(), target_loc.getZ(), target_yaw, 0.0F);
                }
                return false;
            } else {
                return this.creature.getAttackTarget() == null;
            }
        }
    }


    @Override
    @ParametersAreNonnullByDefault
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);

        Vector3d tar = this.getTargetLocation();
        compound.putBoolean("TargetLocation", tar != null);
        if (tar != null) {
            compound.putDouble("TargetX", tar.x);
            compound.putDouble("TargetY", tar.y);
            compound.putDouble("TargetZ", tar.z);
            compound.putFloat("TargetYaw", this.getTargetYaw());
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);

        if (compound.getBoolean("TargetLocation")) {
            double tx = compound.getDouble("TargetX");
            double ty = compound.getDouble("TargetY");
            double tz = compound.getDouble("TargetZ");
            this.setTargetLocation(new Vector3d(tx, ty, tz));

            Float yaw = compound.getFloat("TargetYaw");
            this.setTargetYaw(yaw);
        }
    }
}
