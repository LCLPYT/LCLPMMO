package work.lclpnet.mmo.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RenderWorker {

    private static final List<Runnable> renderQueue = Collections.synchronizedList(new ArrayList<>());

    public static void push(Runnable run) {
        renderQueue.add(run);
    }

    public static void doWork() {
        if (renderQueue.isEmpty()) return;

        synchronized (renderQueue) {
            Iterator<Runnable> iterator = renderQueue.iterator();
            iterator.forEachRemaining(Runnable::run);
            renderQueue.clear();
        }
    }
}
