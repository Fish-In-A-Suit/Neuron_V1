package neuron.android.com.neuron.tools;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

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
}
