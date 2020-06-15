package work.lclpnet.mmo.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import net.minecraft.util.math.MathHelper;

public class MidiMusic extends MusicBase implements MusicInstance {

	private Sequencer sequencer = null;
	private Thread thread = null;
	private float volumeFactor = 0.75F;
	private float volumeBefore = 1F;

	public MidiMusic(File f, float volume) {
		super(f, volume);
	}

	@Override
	public void play() {
		if(sequencer != null) return;

		try {
			InputStream in = new BufferedInputStream(new FileInputStream(file));

			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(in);
			setVolume(volume);
			sequencer.start();
			long length = sequencer.getSequence().getMicrosecondLength();

			thread = new Thread(() -> {
				try {
					Thread.sleep(length / 1000L, (int) (length % 1000) * 1000);
					stop();
				} catch (InterruptedException e) {
					System.out.println("Midi music thread interruped.");
				}
			});
			thread.start();
		} catch (MidiUnavailableException | IOException | InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		if(sequencer == null) return;

		sequencer.stop();
		sequencer.close();

		thread.interrupt();

		MusicSystem.untrack(file);
	}

	@Override
	public void setVolume(float perc) {
		if(sequencer == null) return;

		perc = MathHelper.clamp(perc, 0F, 1F);
		
		volume = perc;

		try {
			MidiSystem.getSynthesizer();
		} catch (MidiUnavailableException e1) {
			e1.printStackTrace();
		}
		
		try {
			Sequence seq = sequencer.getSequence();
			for(Track t : seq.getTracks()) {
				for (int i = 0; i < t.size(); i++) {
					MidiEvent e = t.get(i);
					if(!(e.getMessage() instanceof ShortMessage)) continue;

					ShortMessage shortMessage = (ShortMessage) e.getMessage();
					if(shortMessage.getData1() == 7) {
						shortMessage.setMessage(shortMessage.getStatus(), 7, (int) (shortMessage.getData2() / volumeBefore * perc * volumeFactor));
					}
				}
			}
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

		Iterator<Transmitter> it = sequencer.getTransmitters().iterator();
		while(it.hasNext()) {
			Receiver r = it.next().getReceiver();
			for(int i = 0; i < 16; i++)
				try {
					r.send(new ShortMessage(ShortMessage.CONTROL_CHANGE, i, 7, (int) (127 * perc * volumeFactor)), 0);
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				}
		}
		
		volumeBefore = volume;
	}

}
