package neuron.android.com.neuron.tools;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

import mehdi.sakout.fancybuttons.FancyButton;

public class ViewUtilities {
    public static void setDrawableTint(ImageView imgView, Context context, int colorId) {
        imgView.setColorFilter(ContextCompat.getColor(context, colorId), PorterDuff.Mode.SRC_IN);
    }

    public static void setGoogleButtonText(SignInButton signInButton, String buttonText) {
        for(int i = 0; i<signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if(v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    /**
     * Sets the margin to the text of the fancy button
     * @param button
     * @param left
     * @param right
     * @param top
     * @param bottom
     */
    public static void setFancyButtonTextMargin(FancyButton button, int left, int right, int top, int bottom) {
        System.out.println("[Neuron.ViewUtilities.setFancyButtonRightPadding]: Fancybutton has " + button.getChildCount() + " children.");

        for(int i = 0; i<button.getChildCount(); i++) {
            View v = button.getChildAt(i);

            if (v instanceof TextView) {
                System.out.println("[Neuron.ViewUtilities.setFancyButtonRightPadding]: Found a text view!");
                TextView tv = (TextView) v;

                /*
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(left,top,right,bottom);
                tv.setLayoutParams(params);
                */
                tv.setPadding(left, top, right, bottom);
            }
        }
    }

    public static void test(FancyButton button) {
        System.out.println("[Neuron.ViewUtilities.setFancyButtonRightPadding]: test");

        for(int i = 0; i<button.getChildCount(); i++) {
            View v = button.getChildAt(i);

            if(v instanceof ImageView) {
                System.out.println("[Neuron.ViewUtilities.setFancyButtonRightPadding]: Found an image view");

                ImageView imageView = (ImageView) v;
                button.findViewById(imageView.getId()).setForegroundGravity(Gravity.LEFT);
            }
        }
    }
}
