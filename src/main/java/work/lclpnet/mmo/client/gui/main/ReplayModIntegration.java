package work.lclpnet.mmo.client.gui.main;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReplayModIntegration {

    @Nullable
    public static CustomImageButton getWidget(int x, int y, int dim) {
        final Class<?> replayModReplay;
        try {
            replayModReplay = Class.forName("com.replaymod.replay.ReplayModReplay");
        } catch (ClassNotFoundException ignored) {
            return null;
        }

        return new CustomImageButton(
                x, // x
                y, // y
                dim, // width
                dim, // height
                0, // u
                0, // v
                20, // hoveredVOffset
                1, // uWidth
                1, // vHeight
                new Identifier("replaymod", "logo_button.png"),
                1, // texture width
                1, // texture height
                3, // padding
                b -> openReplayModViewer(replayModReplay),
                (button, matrices, mouseX, mouseY) -> new TranslatableText("replaymod.gui.replayviewer"),
                new TranslatableText("replaymod.gui.replayviewer"));
    }

    private static void openReplayModViewer(Class<?> replayModReplay) {
        try {
            Class<?> replayModViewer = Class.forName("com.replaymod.replay.gui.screen.GuiReplayViewer");
            Constructor<?> constructor = replayModViewer.getDeclaredConstructor(replayModReplay);
            Method display = replayModViewer.getMethod("display");

            Object replayModReplayInstance = getReplayModReplayInstance(replayModReplay);
            if (replayModReplayInstance == null) return;

            Object replayViewer = constructor.newInstance(replayModReplayInstance);
            display.invoke(replayViewer);
        } catch (ReflectiveOperationException ignored) {}
    }

    @Nullable
    private static Object getReplayModReplayInstance(Class<?> replayModReplay) {
        try {
            final Field instanceField = replayModReplay.getDeclaredField("instance");
            if (!Modifier.isStatic(instanceField.getModifiers()))
                throw new NoSuchFieldException("Instance field is not static");

            return instanceField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}
