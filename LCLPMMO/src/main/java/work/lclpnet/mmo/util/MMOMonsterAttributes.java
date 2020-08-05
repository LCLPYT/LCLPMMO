package work.lclpnet.mmo.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class MMOMonsterAttributes {

	public static final IAttribute SCALE_WIDTH = new RangedAttribute((IAttribute) null, "mmo.scaleWidth", 1D, 0.0D, 127.0D).setShouldWatch(true);
	public static final IAttribute SCALE_HEIGHT = new RangedAttribute((IAttribute) null, "mmo.scaleHeight", 1D, 0.0D, 127.0D).setShouldWatch(true);

	public static float getScaleWidth(Entity en) {
		if(!(en instanceof LivingEntity)) return 1F;
		LivingEntity livingEntity = (LivingEntity) en;

		IAttributeInstance widthAttribute = livingEntity.getAttribute(MMOMonsterAttributes.SCALE_WIDTH);
		return widthAttribute != null ? (float) widthAttribute.getValue() : 1F;
	}
	
	public static float getScaleHeight(Entity en) {
		if(!(en instanceof LivingEntity)) return 1F;
		LivingEntity livingEntity = (LivingEntity) en;
		
		IAttributeInstance heightAttribute = livingEntity.getAttribute(MMOMonsterAttributes.SCALE_HEIGHT);
		return heightAttribute != null ? (float) heightAttribute.getValue() : 1F;
	}
	
	public static void setScaleWidth(Entity en, float scale) {
		if(!(en instanceof LivingEntity)) return;
		LivingEntity livingEntity = (LivingEntity) en;
		
		IAttributeInstance widthAttribute = livingEntity.getAttribute(MMOMonsterAttributes.SCALE_WIDTH);
		widthAttribute.setBaseValue(scale);
		en.recalculateSize();
	}
	
	public static void setScaleHeight(Entity en, float scale) {
		if(!(en instanceof LivingEntity)) return;
		LivingEntity livingEntity = (LivingEntity) en;
		
		IAttributeInstance widthAttribute = livingEntity.getAttribute(MMOMonsterAttributes.SCALE_HEIGHT);
		widthAttribute.setBaseValue(scale);
		en.recalculateSize();
	}
	
	public static void setScale(Entity en, float scale) {
		setScaleWidth(en, scale);
		setScaleHeight(en, scale);
	}
	
}
