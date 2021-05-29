package work.lclpnet.mmo.util;

import java.io.IOException;

@FunctionalInterface
public interface IOConsumer<T> {

    void accept(T value) throws IOException;
}
