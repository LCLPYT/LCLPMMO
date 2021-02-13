package work.lclpnet.mmo.audio;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;

@OnlyIn(Dist.CLIENT)
public class MusicBase {

	protected File file;
	protected float volume;
	
	public MusicBase(File file, float volume) {
		this.file = file;
		this.volume = volume;
	}
	
}
