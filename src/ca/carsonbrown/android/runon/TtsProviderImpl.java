package ca.carsonbrown.android.runon;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class TtsProviderImpl extends TtsProviderFactory implements OnInitListener {

	private TextToSpeech tts;

	@Override
	public void init(Context context) {
	    if (tts == null) {
	        tts = new TextToSpeech(context, this);
	    }
	}

	@Override
	public void say(String sayThis) {
		tts.speak(sayThis, TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	public void onInit(int status) {
	    /*Locale loc = new Locale("de", "", "");
	    if (tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE) {
	        tts.setLanguage(loc);
	    }*/
	}

	public void shutdown() {
	    tts.shutdown();
	}
}
