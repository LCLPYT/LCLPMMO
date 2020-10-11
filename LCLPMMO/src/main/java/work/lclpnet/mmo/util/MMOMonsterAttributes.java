package work.lclpnet.mmo.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;

public class MMOMonsterAttributes {

	public static final Attribute SCALE_WIDTH = new RangedAttribute("mmo.scaleWidth", 1D, 0.0D, 127.0D).setShouldWatch(true);
	public static final Attribute SCALE_HEIGHT = new RangedAttribute("mmo.scaleHeight", 1D, 0.0D, 127.0D).setShouldWatch(true);

	public static float getScaleWidth(Entity en) {
		if(!(en instanceof LivingEntity)) return 1F;
		LivingEntity livingEntity = (LivingEntity) en;

		if(livingEntity.getAttributeManager() == null) return 1F;
		
		ModifiableAttributeInstance widthAttribute = livingEntity.getAttribute(MMOMonsterAttributes.SCALE_WIDTH);
		return widthAttribute != null ? (float) widthAttribute.getValue() : 1F;
	}
	
	public static float getScaleHeight(Entity en) {
		if(!(en instanceof LivingEntity)) return 1F;
		LivingEntity livingEntity = (LivingEntity) en;
		
		if(livingEntity.getAttributeManager() == null) return 1F;
		
		ModifiableAttributeInstance heightAttribute = livingEntity.getAttribute(MMOMonsterAttributes.SCALE_HEIGHT);
		return heightAttribute != null ? (float) heightAttribute.getValue() : 1F;
	}
	
	public static void setScaleWidth(Entity en, float scale) {
		if(!(en instanceof LivingEntity)) return;
		LivingEntity livingEntity = (LivingEntity) en;
		
		if(livingEntity.getAttributeManager() == null) return;
		
		ModifiableAttributeInstance widthAttribute = livingEntity.getAttribute(MMOMonsterAttributes.SCALE_WIDTH);
		widthAttribute.setBaseValue(scale);
		en.recalculateSize();
	}
	
	public static void setScaleHeight(Entity en, float scale) {
		if(!(en instanceof LivingEntity)) return;
		LivingEntity livingEntity = (LivingEntity) en;
		
		if(livingEntity.getAttributeManager() == null) return;
		
		ModifiableAttributeInstance widthAttribute = livingEntity.getAttribute(MMOMonsterAttributes.SCALE_HEIGHT);
		widthAttribute.setBaseValue(scale);
		en.recalculateSize();
	}
	
	public static void setScale(Entity en, float scale) {
		setScale(en, scale, scale);
	}
	
	public static void setScale(Entity en, float scaleWidth, float scaleHeight) {
		setScaleWidth(en, scaleWidth);
		setScaleHeight(en, scaleHeight);
	}
	
	public static void setScale(Entity en, Vector2f scale) {
		Vector2f clamped = new Vector2f(MathHelper.clamp(scale.x, 0F, 127F), MathHelper.clamp(scale.y, 0F, 127F));
		setScale(en, clamped.x, clamped.y);
	}

	public static Vector2f getScales(Entity source) {
		return new Vector2f(getScaleWidth(source), getScaleHeight(source));
	}
	
}
