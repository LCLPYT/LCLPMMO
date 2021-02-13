package work.lclpnet.mmo.entity;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;

import javax.annotation.Nullable;
import java.util.Map;

public interface IEntitySizeOverride {

	@Nullable
	default Map<Pose, EntitySize> getEntitySizeOverrides() {
		return null;
	}
	
	default float getEyeHeightOverride(Pose pose, EntitySize size) {
		return Float.NaN;
	}
	
}
