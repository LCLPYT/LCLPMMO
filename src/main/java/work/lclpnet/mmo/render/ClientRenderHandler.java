package work.lclpnet.mmo.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.entity.MMOEntities;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.facade.race.Races;
import work.lclpnet.mmo.render.model.ElvenPlayerModel;
import work.lclpnet.mmo.render.model.HumanPlayerModel;
import work.lclpnet.mmo.render.model.VampirePlayerModel;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientRenderHandler {

    private static final Map<MMORace, MMOPlayerRenderer> playerRenderers = new HashMap<>();
    private static final Map<String, MMOPlayerRenderer> humanRenderers = new HashMap<>();

    public static void setup() {
        RenderingRegistry.registerEntityRenderingHandler(MMOEntities.PIXIE, PixieRenderer::new);

        registerPlayerModels();
    }

    public static void registerPlayerModels() {
        EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
        if(renderManager == null) throw new NullPointerException("Render manager is null");

        humanRenderers.put("default", new MMOPlayerRenderer(renderManager, new HumanPlayerModel(false)));
        humanRenderers.put("slim", new MMOPlayerRenderer(renderManager, new HumanPlayerModel(true)));

        playerRenderers.put(Races.VAMPIRE, new MMOPlayerRenderer(renderManager, new VampirePlayerModel()));
        playerRenderers.put(Races.ELVEN, new MMOPlayerRenderer(renderManager, new ElvenPlayerModel()));
    }

    @Nullable
    public static MMOPlayerRenderer getPlayerRenderer(AbstractClientPlayerEntity player) {
        MMOCharacter mmoCharacter = IMMOUser.getMMOUser(player).getMMOCharacter();
        if(mmoCharacter == null) return null;

        MMORace r = mmoCharacter.getRace();
        if(Races.HUMAN.equals(r)) return humanRenderers.get(player.getSkinType());
        else return playerRenderers.get(r);
    }

}
