package work.lclpnet.mmo.util.fun;

import java.io.IOException;

@FunctionalInterface
public interface IOConsumer<T> {

    void accept(T value) throws IOException;
}
