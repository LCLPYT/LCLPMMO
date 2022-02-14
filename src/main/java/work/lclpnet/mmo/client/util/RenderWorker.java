package work.lclpnet.mmo.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@Environment(EnvType.CLIENT)
public class RenderWorker {

    private static final AtomicBoolean dirty = new AtomicBoolean(false);
    private static final ReentrantLock lock = new ReentrantLock();
    private static final List<Runnable> preQueue = new ArrayList<>();
    private static final List<Runnable> renderQueue = Collections.synchronizedList(new ArrayList<>());

    public static void push(Runnable run) {
        lock.lock();
        preQueue.add(run);
        dirty.set(true);
        lock.unlock();
    }

    public static void doWork() {
        if (dirty.get()) {
            lock.lock();
            renderQueue.addAll(preQueue);
            preQueue.clear();
            dirty.set(false);
            lock.unlock();
        }

        if (renderQueue.isEmpty()) return;

        synchronized (renderQueue) {
            Iterator<Runnable> iterator = renderQueue.iterator();
            iterator.forEachRemaining(Runnable::run);
            renderQueue.clear();
        }
    }
}
