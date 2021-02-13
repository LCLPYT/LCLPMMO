package work.lclpnet.mmo.asm.mixin.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import work.lclpnet.mmo.asm.type.IMMOEntity;
import work.lclpnet.mmo.util.ClickListener;
import work.lclpnet.mmo.util.MMOMonsterAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mixin(Entity.class)
public class MixinEntity implements IMMOEntity<Entity> {

	@Shadow
	@Final
	private EntityType<?> type;
	
	@Redirect(
			method = "getSize(Lnet/minecraft/entity/Pose;)Lnet/minecraft/entity/EntitySize;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/EntityType;getSize()Lnet/minecraft/entity/EntitySize;"
					)
			)
	public EntitySize getSize(EntityType<?> type) {
		Entity entity = (Entity) (Object) this;
		return this.type.getSize().scale(MMOMonsterAttributes.getScaleWidth(entity), MMOMonsterAttributes.getScaleHeight(entity));
	}

	@Inject(
			method = "getJumpFactor()F",
			at = @At("RETURN"),
			cancellable = true
			)
	public void onGetJumpFactor(CallbackInfoReturnable<Float> cir) {
		float scaleHeight = MMOMonsterAttributes.getScaleHeight((Entity) (Object) this);
		if(scaleHeight <= 1F || !((Object) this instanceof PlayerEntity)) return;
		cir.setReturnValue(cir.getReturnValue() * scaleHeight);
		cir.cancel();
	}
	
	private final Map<String, ClickListener<Entity>> clickListeners = new HashMap<>();

	@Override
	public void addClickListener(String id, ClickListener<Entity> listener) {
		Objects.requireNonNull(id);
		if(listener == null) removeClickListener(id);
		
		if(clickListeners.containsKey(id)) throw new IllegalArgumentException("Listener with that id already exists.");
		
		clickListeners.put(id, listener);
	}

	@Override
	public void removeClickListener(String id) {
		clickListeners.remove(Objects.requireNonNull(id));
	}

	@Override
	public void removeAllClickListeners() {
		clickListeners.clear();
	}

	@Override
	public boolean onClick(PlayerEntity clicker) {
		return clickListeners.values().stream()
				.anyMatch(consumer -> consumer.onClick((Entity) (Object) this, clicker));
	}
	
}
