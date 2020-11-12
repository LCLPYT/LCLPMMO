package work.lclpnet.mmo.audio;

import java.io.File;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MusicBase {

	protected File file;
	protected float volume;
	
	public MusicBase(File file, float volume) {
		this.file = file;
		this.volume = volume;
	}
	
}
