package ca.carsonbrown.android.runon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingDeque;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.PhoneLookup;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;

public class SpeakSmsService extends Service implements TextToSpeech.OnInitListener, OnUtteranceCompletedListener, AudioManager.OnAudioFocusChangeListener {
	
	private static final String TAG = "SpeakSmsService";
	
	private SharedPreferences mSharedPrefs;
	private TextToSpeech mTts;
	private LinkedBlockingDeque<String> mMessages;
    private int mTtsStatus;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "Starting up from message");
		int retValue = super.onStartCommand(intent, flags, startId);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (checkPreferences()) {
            if (mMessages == null) {
                mMessages = new LinkedBlockingDeque<String>();
            }

			String message = "";
			try {
				message = buildNotificationString(intent.getStringExtra("originatingAddress"), intent.getStringExtra("messageBody"));
			} catch (NullPointerException e) {
				//error in getting Intent's extra strings, cannot continue
				return retValue;
			}
            mMessages.add(message);
            Log.v(TAG, "Added message to queue");

            if (mTts == null) {
                Log.v(TAG, "Creating new TTS");
                mTts = new TextToSpeech(this, this);
                mTts.setOnUtteranceCompletedListener(this);
            }
		} else {
            stopSelf();
        }

		return retValue;
	}

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        Log.v(TAG, "Shutting down SMS Speaker");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

	/**
	 * Check if preferences allow this text message to be announced at all.
	 * @return true if allowed, false otherwise
	 */
	private boolean checkPreferences() {
		//check if the app is enabled at all
		if (mSharedPrefs.getBoolean("enable", true)) {
			//check for headset rule
			int policy = Integer.parseInt(mSharedPrefs.getString(getString(R.string.run_policy_key), "1"));
			AudioManager am = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
			boolean headsetEnabled = am.isWiredHeadsetOn() || am.isBluetoothA2dpOn();
			switch(policy) {
			case 1:	//only with headset
				return headsetEnabled;
			case 2: //only without headset
				return !headsetEnabled;
			case 3:
				return true;
			}
			return true;
		} else {
			return false;
		}
	}
	
	private String buildNotificationString(String originatingAddress, String messageBody) {
		String notificationString = "";
		notificationString += "New message";
		
		if (!mSharedPrefs.getString("say_sender", "1").equals("3")) {
			notificationString += " from ";
			if (mSharedPrefs.getString("say_sender", "1").equals("1")) {
				notificationString += contactName(originatingAddress);
			} else {
				notificationString += originatingAddress;
			}
		}
		notificationString += ". ";
		
		if (mSharedPrefs.getBoolean("say_message", true)) {
			notificationString += messageBody;
		}
		
		return notificationString;
	}
	
	private String contactName(String number) {
		//short circuit for the Settings activity test message
		if (number.equals(getString(R.string.tts_test_number))) {
			return getString(R.string.tts_test_name);
		}
		/// number is the phone number
		Uri lookupUri = Uri.withAppendedPath(
		PhoneLookup.CONTENT_FILTER_URI, 
		Uri.encode(number));
		String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
		Cursor cur = getApplicationContext().getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
		String displayName = "" + number;
		try {
		   if (cur.moveToFirst()) {
		      displayName = cur.getString(2);
		   }
		} finally {
            if (cur != null) {
               cur.close();
            }
		}
		return displayName;
	}

    private void speakMessageFromQueue() {
        String message = null;
        try {
            message = mMessages.pop();
        } catch (NoSuchElementException e) {
            //Nothing to do, null messages are handled later
        }
        if (mTtsStatus != TextToSpeech.ERROR && mTtsStatus != TextToSpeech.LANG_MISSING_DATA && mTtsStatus != TextToSpeech.LANG_NOT_SUPPORTED && message != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "" + message.hashCode());
            params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));

            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
            mTtsStatus = mTts.speak(message, TextToSpeech.QUEUE_FLUSH, params);
            Log.v(TAG, "Speaking SMS: " + (mTtsStatus == TextToSpeech.SUCCESS ? "success" : "failure"));
        } else {
            Log.v(TAG, "Error on SMS: " + mTtsStatus);
        }
    }

	@Override
	public void onInit(int status) {
        mTtsStatus =  status;
        if (status == TextToSpeech.SUCCESS) {
            Locale locale = null;
            //check if default locale works
            if (mSharedPrefs.getBoolean(getString(R.string.default_locale_key), false)) {
                locale = Locale.getDefault();
            } else {
                locale = Locale.US;
            }
            mTtsStatus = mTts.setLanguage(locale);
        }
        speakMessageFromQueue();
	}

    @Override
    public void onUtteranceCompleted(String uttId) {
        Log.v(TAG, "Completed TTS utterance");
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
        if (mMessages.size() == 0) {
            stopSelf();
        } else {
            speakMessageFromQueue();
        }
    }

    @Override
    public void onAudioFocusChange(int i) {
        //don't care, doing it anyway
    }
}
