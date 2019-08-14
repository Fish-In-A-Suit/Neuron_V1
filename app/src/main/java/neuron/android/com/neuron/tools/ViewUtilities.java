package neuron.android.com.neuron.tools;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;

import android.widget.ImageView;

public class ViewUtilities {
    public static void setDrawableTint(ImageView imgView, Context context, int colorId) {
        imgView.setColorFilter(ContextCompat.getColor(context, colorId), PorterDuff.Mode.SRC_IN);
    }
}
