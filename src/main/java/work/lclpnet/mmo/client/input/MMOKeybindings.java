package work.lclpnet.mmo.client.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import work.lclpnet.mmo.LCLPMMO;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = LCLPMMO.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class MMOKeybindings {

    private static final List<KeyBinding> keyBindings = new ArrayList<>();

    public static final String GROUP_LCLPMMO = "LCLPMMO";

    public static final KeyBinding KEY_MAGIC_OVERLAY = register("key.keyboard.g", "Open Magic HUD", InputMappings.Type.KEYSYM, GROUP_LCLPMMO);

    private static KeyBinding register(String keyName, String description, InputMappings.Type type, String group) {
        int keyCode = InputMappings.getInputByName(keyName).getKeyCode();
        KeyBinding binding = new KeyBinding(description, type, keyCode, group);
        keyBindings.add(binding);
        return binding;
    }

    public static void init() {
        keyBindings.forEach(ClientRegistry::registerKeyBinding);
        keyBindings.clear();
    }

}
