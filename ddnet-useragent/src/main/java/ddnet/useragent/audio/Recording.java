package ddnet.useragent.audio;

import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.TargetDataLine;

import org.apache.log4j.Logger;

public class Recording {
	private static final Logger log = Logger.getLogger(Recording.class);
	
	private TargetDataLine targetDataLine;
	private File lastOutputFile;
	private File currentOutputFile;
	private boolean playing;
	private boolean recording;
	private boolean empty = true;
	private Thread startRecordingThread;
	private Clip clip;
	private int recordingStartPosition;
	private final Object recordingLock = new Object();
	private final AudioFormat audioFormat = getAudioFormat();
	private final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
	private final LineListener lineListener = new PlayerLineListener();
	private final Collection<EventListener> listeners = new ArrayList<>();

	
	public void addListener(EventListener listener) {
		if (listener == null) 
			return;
		
		synchronized (listeners) {
			if (!listeners.contains(listener))
				listeners.add(listener);
		}		
	}

	public void removeListener(EventListener listener) {
		if (listener == null) 
			return;
		
		synchronized (listeners) {
			listeners.remove(listener);
		}		
	}
	
	public void beginRecording() throws LineUnavailableException, IOException {
		synchronized(recordingLock) {
			if (recording)
				throw new IllegalStateException("¡Ya se encuentra grabando audio!");
			
			if (!AudioSystem.isLineSupported(dataLineInfo))
				throw new RuntimeException("¡No se pudo encontrar un dispositivo adecuado para la grabación de audio!");

			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			targetDataLine.addLineListener(new LineListener() {				
				@Override
				public void update(LineEvent event) {
					if (event.getType() == Type.STOP)
						onRecordStopped();
				}
			});
			
			if (!empty) {
				lastOutputFile = new File(currentOutputFile.getAbsolutePath());
				recordingStartPosition = getPosition();
			}
			
			targetDataLine.open(audioFormat);
			targetDataLine.start(); // start capturing
			
			// start recording
			startRecordingThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						startRecording();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			recording = true;
			startRecordingThread.start();
			notifyListeners();
		}
	}

	public void endRecording() {
		synchronized(recordingLock) {
			if (!recording)
				throw new IllegalStateException("¡No se encuentra grabando audio!");
	
			try { if (startRecordingThread != null) startRecordingThread.interrupt(); } catch(Throwable ignore) {}
			
			targetDataLine.stop();
			targetDataLine.close();
			recording = false;
			startRecordingThread = null;
			targetDataLine = null;

			notifyListeners();
		}
	}			
	
	public void startPlaying() {
		synchronized(recordingLock) {
			if (empty)
				throw new IllegalStateException("¡No ha grabado nada aún!");
			if (playing)
				throw new IllegalStateException("¡Ya se encuentra reproduciendo este registro!");
			if (recording)
				throw new IllegalStateException("¡Imposible reproducir archivo ya que actualmente se encuentra grabando audio!");
			
			clip.start();
			playing = true;
			notifyListeners();			
		}
	}

	public void stopPlaying() {
		synchronized(recordingLock) {
			if (!playing)
				throw new IllegalStateException("¡No se encuentra reproduciendo este registro!");
			
			clip.stop();
			clip.setMicrosecondPosition(clip.getMicrosecondLength());
			playing = false;
			notifyListeners();
		}		
	}
	
	public void pausePlaying() {
		synchronized(recordingLock) {
			if (!playing)
				throw new IllegalStateException("¡No se encuentra reproduciendo este registro!");
			
			clip.stop();
			playing = false;
			notifyListeners();
		}		
	}
	
	
	/**
	 * Solicita que se notifique, vìa los listeners registrados, el estado actual de esta instancia.
	 */
	public void requestStatus() {
		notifyListeners();
	}
	
	public void setPosition(int millis) {	
		synchronized(recordingLock) {
			if (empty)
				throw new IllegalStateException("¡No ha grabado nada aún!");
			if (playing)
				throw new IllegalStateException("¡Ya se encuentra reproduciendo este registro!");
						
			if (millis < 0) {
				millis = 0;
			} else {
				final int currentLength = getLength();
				if (millis > currentLength)
					millis = currentLength;
			}
			
			clip.setMicrosecondPosition(millis * 1000L);			
			notifyListeners();
		}
	}	
	
	public int getPosition() {
		synchronized(recordingLock) {
			return isEmpty() ? 0 : (int)clip.getMicrosecondPosition() / 1000;
		}		
	}

	public boolean atBeginning() {
		synchronized(recordingLock) {
			return isEmpty() ? false : getPosition() == 0;
		}		
	}

	public boolean atEnd() {
		synchronized(recordingLock) {
			return isEmpty() ? false : getPosition() == getLength();
		}		
	}
	
	public boolean isRecording() {
		synchronized(recordingLock) {
			return recording;
		}
	}

	public boolean isPlaying() {
		synchronized(recordingLock) {
			return playing;
		}
	}

	public boolean isEmpty() {
		synchronized(recordingLock) {
			return empty;
		}
	}
	
	public int getLength() {
		synchronized(recordingLock) {
			return isEmpty() ? 0 : (int)clip.getMicrosecondLength() / 1000;
		}
	}
	
	public File getOutputFile() {
		synchronized(recordingLock) {
			return currentOutputFile;
		}
	}	
	
	private void startRecording() throws IOException {			
		currentOutputFile = File.createTempFile("ddnetuseragent-voice-recording", ".wav");
		AudioInputStream ais = new AudioInputStream(targetDataLine);
		AudioSystem.write(ais, AudioFileFormat.Type.WAVE, currentOutputFile);
	}

	private void onRecordStopped() {
		int rsp = recordingStartPosition;
		recordingStartPosition = 0;
		empty = true;
		
		try {			
		    if (clip != null) {
		    	clip.removeLineListener(lineListener);
		    	try { clip.close(); } catch (Throwable ignore) { }
		    	clip = null;
		    	
		    	if (rsp > 0) {
		    		// Elimino de lo actualmente grabado todo desde el punto de inicio de la nueva grabacion hasta el final. 
		    		AudioInputStream previous = new Trimmer(AudioSystem.getAudioInputStream(lastOutputFile), 0, rsp);
		    		lastOutputFile = File.createTempFile("ddnetuseragent-voice-recording", ".wav");
		    		AudioSystem.write(previous, AudioFileFormat.Type.WAVE, lastOutputFile);		    		
		    		
		    		// Cargo el audio trimeado y el que acabo de grabar
		    		AudioInputStream previousTrimmed = AudioSystem.getAudioInputStream(lastOutputFile);
		    		AudioInputStream newRecorded = AudioSystem.getAudioInputStream(currentOutputFile);
		    		
		    		// Concateno ambos audios
		    		AudioInputStream appended = new AudioInputStream(new SequenceInputStream(previousTrimmed, newRecorded),     
		    				previousTrimmed.getFormat(), previousTrimmed.getFrameLength() + newRecorded.getFrameLength());

		    		// Grabo el resultado concatenado
		    		currentOutputFile = File.createTempFile("ddnetuseragent-voice-recording", ".wav");
		    		AudioSystem.write(appended, AudioFileFormat.Type.WAVE, currentOutputFile);		    				    		
		    	}
		    }
		    
		    Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
	    	clip = AudioSystem.getClip(selectMixer(mixerInfo));
	    	clip.open(AudioSystem.getAudioInputStream(currentOutputFile));
		    clip.addLineListener(lineListener);
		    empty = false;
		    
		    if (rsp > 0)
		    	setPosition(rsp);		    
		} catch (Throwable t) {
			log.error("Error cargando grabación de audio", t);
			throw new RuntimeException(t);
		} finally {
			notifyListeners();
		}
	}
	
	private Info selectMixer(Info[] mixersInfo) {
		Info selectedMixer = null; 
		
		StringBuilder sb = new StringBuilder();
		sb.append("Audio Mixers Info: \r\n");
		if (mixersInfo != null) {
			for(int index=0; index<mixersInfo.length; index++) {
				Info mixer = mixersInfo[index];
				sb.append(String.format("%d.  %s // %s // %s // %s\r\n",
						index, mixer.getName(), mixer.getDescription(), mixer.getVendor(), mixer.getVersion()));
				
				if ("default [default]".equalsIgnoreCase(mixer.getName()))
					selectedMixer = mixer;
			}					
			
			if (selectedMixer == null)
				selectedMixer = mixersInfo[0]; 
		}
		log.debug(sb.toString());				
		
		return selectedMixer;
	}

	private AudioFormat getAudioFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

	private void notifyListeners() {
		Collection<EventListener> listenersCopy;
		
		synchronized (listeners) {
			listenersCopy = new ArrayList<>(listeners);
		}
		
		int lengthMillis;
		int positionMillis;
		
		boolean emptyState, recordingState, playingState;
		synchronized (recordingLock) {
			emptyState = empty; 
			recordingState = recording;
			playingState = playing;
			lengthMillis = getLength();
			positionMillis = getPosition();
		}
		if (positionMillis > lengthMillis) 
			lengthMillis = positionMillis;		
			
		for(EventListener listener : listenersCopy)
			try { listener.update(emptyState, recordingState, playingState, lengthMillis, positionMillis); } catch (Throwable ignore) {}
	}
	
	private void onClipStopped() {
		synchronized (recordingLock) {
			if (playing) {
				playing = false;
				notifyListeners();
			}
		}
	}

	private class PlayerLineListener implements LineListener {
		@Override
		public void update(LineEvent event) {
			if (event.getType() == LineEvent.Type.STOP)
				onClipStopped();
		}
	}
	
	public interface EventListener {
		void update(boolean empty, boolean recording, boolean playing, int lengthMillis, int positionMillis);
	}
}
