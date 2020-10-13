package work.lclpnet.mmo.audio;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.util.MMONames.Sound;

@EventBusSubscriber(modid = LCLPMMO.MODID, bus = Bus.MOD)
public class MMOSoundEvents {

	private static final List<SoundEvent> SOUNDS = new ArrayList<>();
	
	public static final SoundEvent MUSIC_LS5 = register(Sound.MUSIC_LS5),
			INTRO_THEME = register(Sound.INTRO_THEME),
			INTRO_THEME_ALT = register(Sound.INTRO_THEME_ALT),
			UI_BUTTON_HOVER = register(Sound.UI_BUTTON_HOVER);
	
	private static SoundEvent register(String name)
    {
        SoundEvent event = new SoundEvent(new ResourceLocation(name));
        event.setRegistryName(name);
        SOUNDS.add(event);
        return event;
    }
	
    @SubscribeEvent
    public static void registerSounds(final RegistryEvent.Register<SoundEvent> event)
    {
        SOUNDS.forEach(soundEvent -> event.getRegistry().register(soundEvent));
        SOUNDS.clear();
    }
	
}
