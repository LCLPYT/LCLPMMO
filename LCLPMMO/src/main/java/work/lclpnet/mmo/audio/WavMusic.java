package work.lclpnet.mmo.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.minecraft.util.math.MathHelper;

public class WavMusic extends MusicBase implements MusicInstance{

	private Clip clip = null;
	private Thread thread = null;
	
	public WavMusic(File file, float volume) {
		super(file, volume);
	}
	
	@Override
	public void play() {
		if(clip != null) return;
		
		thread = new Thread(this::asyncPlay);
		thread.start();
	}
	
	private void asyncPlay() {
		try (AudioInputStream in = AudioSystem.getAudioInputStream(file)) {
			clip = AudioSystem.getClip();
			AudioListener listener = new AudioListener();
			clip.addLineListener(listener);
			clip.open(in);
			setVolume(volume);
			clip.start();
			listener.waitUntilDone();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Wav music thread interrupted.");
		} finally {
			stop();
		}
	}

	@Override
	public void stop() {
		if(clip == null) return;
		
		clip.stop();
		thread.interrupt();
		
		MusicSystem.untrack(file);
	}

	@Override
	public void setVolume(float perc) {
		if(clip == null) return;
		
		perc = MathHelper.clamp(perc, 0F, 1F);
		
		volume = perc;
		
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
	    gainControl.setValue(20f * (float) Math.log10(perc));
	}
	
	private static class AudioListener implements LineListener {
		private boolean done = false;
		@Override public synchronized void update(LineEvent event) {
			Type eventType = event.getType();
			if (eventType == Type.STOP || eventType == Type.CLOSE) {
				done = true;
				notifyAll();
			}
		}
		public synchronized void waitUntilDone() throws InterruptedException {
			while (!done) wait();
		}
	}

}
