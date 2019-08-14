package neuron.android.com.neuron.tools;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import neuron.android.com.neuron.core.Constants;

public class ActivityTools {

    /**
     * Starts the activity specified by target class from the currentContext
     * @param currentContext
     * @param target
     */
    public static void startNewActivity(Context currentContext, Class target) {
        Intent intent = new Intent(currentContext, target);
        currentContext.startActivity(intent);
    }

    /**
     * Starts the activity specified by target from the currentContext with parcelable as extra data
     * @param currentContext
     * @param target
     * @param parcelableKey
     * @param p
     */
    public static void startNewActivity(Context currentContext, Class target, String parcelableKey, Parcelable p) {
        Intent intent = new Intent(currentContext, target);
        intent.putExtra(parcelableKey, p);
        currentContext.startActivity(intent);
    }
}
