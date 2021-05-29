package work.lclpnet.mmo.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.gui.questbook.QuestOverlay;

@EventBusSubscriber(bus = Bus.FORGE, modid = LCLPMMO.MODID)
public class QuestListener {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onGameOverlay(RenderGameOverlayEvent.Post e) {
        if (e.isCanceled() || e.getType() != ElementType.EXPERIENCE) return;

        Minecraft mc = Minecraft.getInstance();
        int width = e.getWindow().getScaledWidth();
        int overlayWidth = (int) (width * 0.2F);
        int padding = 10;
        QuestOverlay.render(mc, e.getMatrixStack(), width - overlayWidth - padding, 10, overlayWidth);
    }
}
