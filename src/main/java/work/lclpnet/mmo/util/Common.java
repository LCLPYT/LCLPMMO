package work.lclpnet.mmo.util;

import net.minecraft.util.math.vector.Vector3f;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Common {

    public static final Vector3f ZERO = new Vector3f(0F, 0F, 0F);

    public static <T> void applyFilteredAction(Collection<T> list, Predicate<T> filter, Consumer<Collection<T>> action) {
        action.accept(list.stream().filter(filter).collect(Collectors.toList()));
    }

}
