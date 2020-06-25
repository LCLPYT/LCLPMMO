package work.lclpnet.mmo.event;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.LCLPMMO;

@EventBusSubscriber(modid = LCLPMMO.MODID, bus = Bus.FORGE)
public class AttributeListener {

	/*@SubscribeEvent
	public static void onEntityConstruct(EntityConstructing e) {
		e.getEntity().setBoundingBox(e.getEntity().getBoundingBox().grow(2));
	}	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onEntityRenderPre(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
		event.getMatrixStack().scale(2F, 2F, 2F);
	}*/

}
