package work.lclpnet.mmo.facade.race;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.LCLPMMO;

import java.util.Map;

public class RaceElve extends MMORace {

	public static final transient Map<Pose, EntitySize> SIZES = ImmutableMap.<Pose, EntitySize>builder()
			.put(Pose.STANDING, EntitySize.flexible(0.5F, 2.125F))
			.put(Pose.CROUCHING, EntitySize.flexible(0.5F, 1.75F))
			.build(); 
	
	RaceElve() {
		super("elve", new TranslationTextComponent("mmo.race.dwarf.title"));
	}
	
	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(LCLPMMO.MODID, "textures/entity/elve/icon.png");
	}

	@Override
	public Map<Pose, EntitySize> getEntitySizeOverrides() {
		return SIZES;
	}
	
	@Override
	public float getEyeHeightOverride(Pose pose, EntitySize size) {
		switch (pose) {
		case STANDING:
			return 1.85F;
		case CROUCHING:
			return 1.48F;
		default:
			return super.getEyeHeightOverride(pose, size);
		}
	}

}
