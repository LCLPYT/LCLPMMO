package work.lclpnet.mmo.audio;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface MusicInstance {

	void play();

	void stop();
	
	void setVolume(float f);
	
}
