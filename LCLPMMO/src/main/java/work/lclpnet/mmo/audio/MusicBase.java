package work.lclpnet.mmo.audio;

import java.io.File;

public class MusicBase {

	protected File file;
	protected float volume;
	
	public MusicBase(File file, float volume) {
		this.file = file;
		this.volume = volume;
	}
	
}
