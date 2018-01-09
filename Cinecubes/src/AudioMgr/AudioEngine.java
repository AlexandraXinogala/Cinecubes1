package AudioMgr;

import java.util.ArrayList;

public abstract class AudioEngine {

	public ArrayList<Audio> audio;

	public AudioEngine() {
	};

	abstract public void InitializeVoiceEngine();

	abstract public void CreateAudio(String textTobeSound,
			String FileNameOfSound);

	abstract public String randomIdentifier();

}