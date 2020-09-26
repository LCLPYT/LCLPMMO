package work.lclpnet.mmo.util;

import java.util.ArrayList;
import java.util.List;

public class Enqueuer {

	private static volatile List<Runnable> renderQueue = new ArrayList<>();
	
	public static void enqueueOnRender(Runnable run) {
		renderQueue.add(run);
	}
	
	public static void workRender() {
		if(renderQueue.isEmpty()) return;
		
		renderQueue.forEach(Runnable::run);
		renderQueue.clear();
	}
	
}
