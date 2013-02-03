package ca.carsonbrown.android.runon;

import java.util.HashMap;
import java.util.Locale;

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

public class SpeakSmsService extends Service implements TextToSpeech.OnInitListener, OnUtteranceCompletedListener {
	
	private static final String TAG = "SpeakSmsService";
	
	private SharedPreferences mSharedPrefs;
	private TextToSpeech mTts;
	private String mMessage = null;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int retValue = super.onStartCommand(intent, flags, startId);
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (checkPreferences()) {
			String message = "";
			try {
				message = buildNotificationString(intent.getStringExtra("originatingAddress"), intent.getStringExtra("messageBody"));
			} catch (NullPointerException e) {
				//error in getting Intent's extra strings, cannot continue
				return retValue;
			}
			mMessage = message;
            mTts = new TextToSpeech(this, this);
		}
		return retValue;
	}

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
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
		notificationString += "New message ";
		
		if (!mSharedPrefs.getString("say_sender", "1").equals("3")) {
			notificationString += "from ";
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

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS && mMessage != null) {
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "" + mMessage.hashCode());

            Locale locale = null;
            //check if default locale works
            if (mSharedPrefs.getBoolean(getString(R.string.default_locale_key), false)) {
                locale = Locale.getDefault();
            } else {
                locale = Locale.US;
            }
			int result = mTts.setLanguage(locale);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
			    mTts.speak(mMessage, TextToSpeech.QUEUE_FLUSH, params);
			    mMessage = null;
            }
		}
	}

    @Override
    public void onUtteranceCompleted(String uttId) {
        stopSelf();
    }
}
