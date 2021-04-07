package work.lclpnet.mmo.gui;

import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class MenuAdapter<T, P> {

    private final Map<Integer, Function<P, T>> lookup = new HashMap<>();
    private final Function<P, T> defaultValue;

    public MenuAdapter(Function<P, T> defaultValue) {
        this.defaultValue = Objects.requireNonNull(defaultValue);
    }

    public T get(@Nonnull Minecraft mc, P parameter) {
        return get(mc.gameSettings.guiScale, parameter);
    }

    public T get(int guiScale, P parameter) {
        Function<P, T> fun;
        if (!lookup.containsKey(guiScale) || (fun = lookup.get(guiScale)) == null) return defaultValue.apply(parameter);

        return fun.apply(parameter);
    }

    public MenuAdapter<T, P> register(int guiScale, Function<P, T> value) {
        lookup.put(guiScale, value);
        return this;
    }

}
