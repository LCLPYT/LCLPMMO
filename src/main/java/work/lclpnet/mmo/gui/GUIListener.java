package work.lclpnet.mmo.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.gui.magic.MagicOverlay;
import work.lclpnet.mmo.gui.questbook.QuestOverlay;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = LCLPMMO.MODID)
public class GUIListener {

    @SubscribeEvent
    public static void onGameOverlay(RenderGameOverlayEvent.Post e) {
        if(e.isCanceled() || e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

        QuestOverlay.onEvent(e);
        MagicOverlay.onEvent(e);
    }

    @SubscribeEvent
    public static void onMouseRaw(InputEvent.RawMouseEvent e) {
        if (MagicOverlay.isShowing()) e.setCanceled(true);
    }

}
