package work.lclpnet.mmo.event;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.entity.MMOMonsterAttributes;

@EventBusSubscriber(modid = LCLPMMO.MODID, bus = Bus.FORGE)
public class AttributeListener {

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onEntityRenderPre(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
		LivingEntity le = event.getEntity();
		float scaleWidth = MMOMonsterAttributes.getScaleWidth(le),
				scaleHeight = MMOMonsterAttributes.getScaleHeight(le);
		event.getMatrixStack().scale(scaleWidth, scaleHeight, scaleWidth);
	}
	
}
