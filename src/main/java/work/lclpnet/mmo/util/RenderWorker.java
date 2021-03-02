package work.lclpnet.mmo.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RenderWorker {

	private static final List<Runnable> renderQueue = Collections.synchronizedList(new ArrayList<>());
	
	public static void enqueueOnRender(Runnable run) {
		renderQueue.add(run);
	}
	
	public static void workRender() {
		if(renderQueue.isEmpty()) return;

		synchronized (renderQueue) {
			Iterator<Runnable> iterator = renderQueue.iterator();
			iterator.forEachRemaining(Runnable::run);
			renderQueue.clear();
		}
	}
	
}
