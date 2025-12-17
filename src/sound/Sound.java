package sound;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
	
	private Clip clip;
	private FloatControl gainControl;
	private float volume;
	
	public Sound(String path) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Sound.class.getResource(path));
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			audioInputStream.close();
			if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }
			setVolume(1f);
		}catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void play(int loop) {
		reset();
	    clip.loop(loop);
	}
	
	public void play() {
		reset();
		clip.start();
	}
	
	public void stop() {
		clip.stop();
	}
	
	public void reset() {
		clip.setMicrosecondPosition(0);
	}
	
	public void setVolume(float volume) {
		if(volume < 0 || volume > 1) {
			throw new IllegalArgumentException("Volume must be between 0 and 1");
		}
		this.volume = volume;
		if(gainControl != null) {
		   float min = gainControl.getMinimum();
		   float dB;
		   if(volume == 0) {
			   dB = min;
		   }else {
			   dB = (float) (20.0 * Math.log10(this.volume));
		   }
           gainControl.setValue(dB);
	    }
	}
	
	public float getVolume() {
		return volume;
	}
	
	public boolean getIsRunning() {
		return clip.isRunning();
	}
}
