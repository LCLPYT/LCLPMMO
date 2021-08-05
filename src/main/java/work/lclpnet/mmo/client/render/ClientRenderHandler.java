package work.lclpnet.mmo.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.client.render.model.ElvenPlayerModel;
import work.lclpnet.mmo.client.render.model.HumanPlayerModel;
import work.lclpnet.mmo.client.render.model.VampirePlayerModel;
import work.lclpnet.mmo.entity.EquesterEntity;
import work.lclpnet.mmo.entity.MMOEntities;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.facade.race.Races;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler;

@OnlyIn(Dist.CLIENT)
public class ClientRenderHandler {

    private static final Map<MMORace, MMOPlayerRenderer> playerRenderers = new HashMap<>();
    private static final Map<String, MMOPlayerRenderer> humanRenderers = new HashMap<>();

    public static void setup() {
        registerEntityRenderingHandler(MMOEntities.PIXIE, PixieRenderer::new);
        registerEntityRenderingHandler(MMOEntities.BOLETUS, BoletusRenderer::new);
        registerEntityRenderingHandler(MMOEntities.FALLEN_KNIGHT, FallenKnightRenderer::new);
        registerEntityRenderingHandler(MMOEntities.EQUESTER, EquesterRenderer::new);
        registerPlayerModels();
    }

    public static void registerPlayerModels() {
        EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();

        humanRenderers.put("default", new MMOPlayerRenderer(renderManager, new HumanPlayerModel(false)));
        humanRenderers.put("slim", new MMOPlayerRenderer(renderManager, new HumanPlayerModel(true)));

        playerRenderers.put(Races.VAMPIRE, new MMOPlayerRenderer(renderManager, new VampirePlayerModel()));
        playerRenderers.put(Races.ELVEN, new MMOPlayerRenderer(renderManager, new ElvenPlayerModel()));
    }

    @Nullable
    public static MMOPlayerRenderer getPlayerRenderer(AbstractClientPlayerEntity player) {
        MMOCharacter mmoCharacter = IMMOUser.getMMOUser(player).getMMOCharacter();
        if (mmoCharacter == null) return null;

        MMORace r = mmoCharacter.getRace();
        if (Races.HUMAN.equals(r)) return humanRenderers.get(player.getSkinType());
        else return playerRenderers.get(r);
    }
}
