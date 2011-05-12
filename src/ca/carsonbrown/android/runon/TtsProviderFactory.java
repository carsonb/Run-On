package ca.carsonbrown.android.runon;

import android.content.Context;
import android.os.Build;
import android.util.Log;

public abstract class TtsProviderFactory {

	public abstract void say(String sayThis);

	public abstract void init(Context context);

	public abstract void shutdown();


	private static TtsProviderFactory sInstance;

	public static TtsProviderFactory getInstance() {
	    if (sInstance == null) {
	        int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
	        if (sdkVersion < Build.VERSION_CODES.DONUT) {
	            return null;
	        }

	        try {
	            String className = "TtsProviderImpl";
	            Class<? extends TtsProviderFactory> clazz =
	                    Class.forName(TtsProviderFactory.class.getPackage().getName() + "." + className)
	                            .asSubclass(TtsProviderFactory.class);
	            sInstance = clazz.newInstance();
	        } catch (Exception e) {
	            throw new IllegalStateException(e);
	        }
	    }
	    return sInstance;
	}
}
