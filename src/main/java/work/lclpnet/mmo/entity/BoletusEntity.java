package work.lclpnet.mmo.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import work.lclpnet.mmo.audio.MMOSoundEvents;
import work.lclpnet.mmo.network.msg.MessageEntityAttack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class BoletusEntity extends MonsterEntity implements IAngerable, IAnimatable, IMMOAttacker {

    private static final UUID entityUUID = UUID.fromString("6CC930D2-9F85-11EB-A8B3-0242AC130003");
    private static final RangedInteger angerTimeRange = TickRangeConverter.convertRange(20, 39);
    private static final RangedInteger angerAlliesRange = TickRangeConverter.convertRange(4, 6);
    private static final RangedInteger angerSoundRange = TickRangeConverter.convertRange(0, 1);
    private static final RangedInteger puffRange = TickRangeConverter.convertRange(10, 20);
    private static final AttributeModifier attackingSpeedBoost = new AttributeModifier(entityUUID, "Attacking speed boost", 0.175D, AttributeModifier.Operation.ADDITION);

    protected int angerTime;
    protected int angerNearbyAlliesTimer;
    protected int angerSoundTimer;
    protected int puffTimer;
    protected UUID angerTarget;
    protected AnimationFactory factory = new AnimationFactory(this);
    protected boolean poofAnimationEnabled = false;
    protected boolean attackAnimationEnabled = false;
    protected int stepSoundTimer = 0;

    public BoletusEntity(World worldIn) {
        super(MMOEntities.BOLETUS, worldIn);
        this.ignoreFrustumCheck = true;

        this.puffTimer = puffRange.getRandomWithinRange(this.rand);
    }

    @Override
    public int getAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setAngerTime(int time) {
        this.angerTime = time;
    }

    @Nullable
    @Override
    public UUID getAngerTarget() {
        return this.angerTarget;
    }

    @Override
    public void setAngerTarget(@Nullable UUID target) {
        this.angerTarget = target;
    }

    @Override
    public void func_230258_H__() {
        this.setAngerTime(angerTimeRange.getRandomWithinRange(this.rand));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.applyEntityAI();
    }

    protected void applyEntityAI() {
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setCallsForHelp(BoletusEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PixieEntity.class, 10, false, false, le -> !((PixieEntity) le).isTamed()));
        this.targetSelector.addGoal(3, new ResetAngerGoal<>(this, true));
    }

    public static AttributeModifierMap.MutableAttribute prepareAttributes() {
        return MonsterEntity.func_234295_eP_()
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 35.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.5D)
                .createMutableAttribute(Attributes.ARMOR, 1.5D)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.world.isRemote) {
            if(puffTimer-- <= 0) {
                puffTimer = puffRange.getRandomWithinRange(this.rand);
                AnimationData data = this.factory.getOrCreateAnimationData(this.getEntityId());
                AnimationController<?> controller = data.getAnimationControllers().get("poof");
                controller.setAnimation(new AnimationBuilder().addAnimation("animation.boletus.poof"));
                controller.markNeedsReload();
                this.poofAnimationEnabled = true;
            }

            if(stepSoundTimer > 0) stepSoundTimer--;
        }
    }

    protected void updateAITasks() {
        ModifiableAttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if(attributeInstance == null) return;

        if (this.isAngry()) {
            if (!attributeInstance.hasModifier(attackingSpeedBoost)) attributeInstance.applyNonPersistentModifier(attackingSpeedBoost);

            this.tickAngerSound();
        } else if (attributeInstance.hasModifier(attackingSpeedBoost)) {
            attributeInstance.removeModifier(attackingSpeedBoost);
        }

        this.func_241359_a_((ServerWorld)this.world, true);
        if (this.getAttackTarget() != null) {
            this.tickAngerNearby();
        }

        if (this.isAngry()) this.recentlyHit = this.ticksExisted;

        super.updateAITasks();
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
            if (this.getAttackTarget() != null && this.getEntitySenses().canSee(this.getAttackTarget())) this.angerNearbyAllies();
            this.angerNearbyAlliesTimer = angerAlliesRange.getRandomWithinRange(this.rand);
        }
    }

    private void angerNearbyAllies() {
        double range = this.getAttributeValue(Attributes.FOLLOW_RANGE);
        AxisAlignedBB axisalignedbb = AxisAlignedBB.fromVector(this.getPositionVec()).grow(range, 10.0D, range);

        LivingEntity attackTarget = this.getAttackTarget();
        if(attackTarget == null) return;

        this.world.getLoadedEntitiesWithinAABB(BoletusEntity.class, axisalignedbb).stream()
                .filter(other -> other != this)
                .filter(other -> other.getAttackTarget() == null)
                .filter(other -> !other.isOnSameTeam(attackTarget))
                .forEach(other -> other.setAttackTarget(attackTarget));
    }

    public void setAttackTarget(@Nullable LivingEntity entity) {
        if (this.getAttackTarget() == null && entity != null) {
            this.angerSoundTimer = angerSoundRange.getRandomWithinRange(this.rand);
            this.angerNearbyAlliesTimer = angerAlliesRange.getRandomWithinRange(this.rand);
        }

        if (entity instanceof PlayerEntity) this.setAttackingPlayer((PlayerEntity) entity);

        super.setAttackTarget(entity);
    }

    @Override
    public int getTalkInterval() {
        return 100;
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        MessageEntityAttack.sync(this, entityIn);
        return super.attackEntityAsMob(entityIn);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onMMOAttack(Entity victim) {
        AnimationData data = this.factory.getOrCreateAnimationData(this.getEntityId());
        AnimationController<?> controller = data.getAnimationControllers().get("attack");
        controller.setAnimation(new AnimationBuilder().addAnimation("animation.boletus.attack"));
        controller.markNeedsReload();
        this.attackAnimationEnabled = true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return MMOSoundEvents.ENTITY_BOLETUS_IDLE;
    }

    protected SoundEvent getStepSound() {
        return MMOSoundEvents.ENTITY_BOLETUS_STEP;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        if(stepSoundTimer <= 0) {
            this.playSound(this.getStepSound(), 0.15F, 1.0F);
            stepSoundTimer = 100;
        }
    }

    private void playAngerSound() {
        this.playSound(MMOSoundEvents.ENTITY_BOLETUS_ANGRY, this.getSoundVolume(), this.getSoundPitch() * 1.3F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return MMOSoundEvents.ENTITY_BOLETUS_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MMOSoundEvents.ENTITY_BOLETUS_DEATH;
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        this.writeAngerNBT(compound);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if(!world.isRemote) //FORGE: allow this entity to be read from nbt on client. (Fixes MC-189565)
            this.readAngerNBT((ServerWorld)this.world, compound);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        return !this.isInvulnerableTo(source) && super.attackEntityFrom(source, amount);
    }

    /**
     * Player tries to sleep. Check if this entity is angry or if the player can sleep.
     */
    public boolean func_230292_f_(PlayerEntity p_230292_1_) {
        return this.func_233680_b_(p_230292_1_);
    }

    private <E extends IAnimatable> PlayState idlePredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.boletus.idle", true));
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState poofPredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.boletus.poof"));
        return poofAnimationEnabled ? PlayState.CONTINUE : PlayState.STOP;
    }

    private <E extends IAnimatable> PlayState attackPredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.boletus.attack"));
        return attackAnimationEnabled ? PlayState.CONTINUE : PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "idle", 0, this::idlePredicate));
        data.addAnimationController(new AnimationController<>(this, "poof", 0, this::poofPredicate));
        data.addAnimationController(new AnimationController<>(this, "attack", 0, this::attackPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

}
