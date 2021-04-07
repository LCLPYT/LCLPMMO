package work.lclpnet.mmo.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class QueueWorker {

    @OnlyIn(Dist.CLIENT)
    private static final List<Runnable> renderQueue = Collections.synchronizedList(new ArrayList<>());

    @OnlyIn(Dist.CLIENT)
    public static void enqueueOnRender(Runnable run) {
        renderQueue.add(run);
    }

    @OnlyIn(Dist.CLIENT)
    public static void doRenderWork() {
        if (renderQueue.isEmpty()) return;

        synchronized (renderQueue) {
            Iterator<Runnable> iterator = renderQueue.iterator();
            iterator.forEachRemaining(Runnable::run);
            renderQueue.clear();
        }
    }

}
