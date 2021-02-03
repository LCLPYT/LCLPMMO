package work.lclpnet.mmo.entity;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;

public interface IEntitySizeOverride {

	@Nullable
	default Map<Pose, EntitySize> getEntitySizeOverrides() {
		return null;
	}
	
	default float getEyeHeightOverride(Pose pose, EntitySize size) {
		return Float.NaN;
	}
	
}