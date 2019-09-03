package neuron.android.com.neuron.tools;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firestore.admin.v1beta1.Progress;

import java.util.ArrayList;
import java.util.HashMap;

import neuron.android.com.neuron.core.Constants;

/**
 * Holds various methods to create animations in activities
 */
public class AnimationTools {
    private static HashMap<Integer, HashMap<ProgressBar, LinearLayout>> loadingAnimations; //placeholder which holds references to all of the loading animations (used for stopping them)
    private static ArrayList<Integer> usedAnimationCodes; //used so that animations dont get created at every button click, etc

    public static void init() {
        loadingAnimations = new HashMap<>();
        usedAnimationCodes = new ArrayList<>();
    }

    /**
     * Starts a circular progress bar loading animation by adding the progress bar to the root linear layout
     * @param code The code by which the created ProgressBar view can be accessed
     * @param activityContext The context of the calling activity
     * @param rootLayout The layout to which to add the ProgressBar
     * @param style The style of the progress bar, or 0 to use the default progress bar style of android
     */
    public static void startLoadingAnimation(int code, Context activityContext, LinearLayout rootLayout, int style) {
        System.out.println("[Neuron.AnimationTools.startLoadingAnimation]: Creating loading animation with code " + code);

        if(usedAnimationCodes.contains(code)) {
            //if animation already exists, then don't do anything
            return;
        }

        int realStyle;

        if(style == 0) {
            realStyle = android.R.attr.progressBarStyle;
        } else {
            realStyle = style;
        }

        ProgressBar progressBar = new ProgressBar(activityContext);
        progressBar.setScaleX(0.6f);
        progressBar.setScaleY(0.6f);
        progressBar.setId(Constants.id_AGSU_loading_bar);
        progressBar.setVisibility(View.INVISIBLE);

        rootLayout.addView(progressBar, 0);
        progressBar.setVisibility(View.VISIBLE);

        HashMap<ProgressBar, LinearLayout> temp = new HashMap<>();
        temp.put(progressBar, rootLayout);

        loadingAnimations.put(code, temp);
        usedAnimationCodes.add(code);
    }

    /**
     * Stops the loading animation by hiding the view and removing it from it's parent layout
     * @param code
     */
    public static void stopLoadingAnimation(int code) {
        System.out.println("[Neuron.AnimationTools.stopLoadingAnimation]: Stopping animation with code " + code);

        for(ProgressBar bar : loadingAnimations.get(code).keySet()) {
            bar.setVisibility(View.GONE);
            loadingAnimations.get(code).get(bar).removeView(bar);
        }

        usedAnimationCodes.remove(new Integer(code));
    }


}
