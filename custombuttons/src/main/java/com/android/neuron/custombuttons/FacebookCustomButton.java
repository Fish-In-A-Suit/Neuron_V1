package com.android.neuron.custombuttons;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public class FacebookCustomButton extends LinearLayout {
    private Context context;

    private ImageView iconView;
    private TextView textView;

    private String iconPath;
    private Drawable iconResource;
    private String textSource;

    private int iconWidth;
    private int iconHeight;
    private int iconGravity = 0x11; //Gravity.CENTER
    private int iconMarginStart;
    private int iconMarginEnd;
    private int iconMarginTop;
    private int iconMarginBottom;

    private int textGravity = 0x11; //center
    private int textSize;
    private int textColor;

    private int text_marginStart;
    private int text_marginTop;
    private int text_marginEnd;
    private int text_marginBottom;

    private Typeface font;



    public FacebookCustomButton(Context context) {
        super(context);
        this.context = context;

        initializeButton();
    }

    public FacebookCustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initAttrs(context, attrs);

        initializeButton();
    }

    public FacebookCustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initAttrs(context, attrs);

        initializeButton();
    }

    void initAttrs(Context context, AttributeSet attrs) {
        if(attrs!=null) {
            TypedArray attributeArray = context.obtainStyledAttributes(attrs, R.styleable.FacebookCustomButton);

            iconResource = attributeArray.getDrawable(R.styleable.FacebookCustomButton_icon_resource);

            String text = attributeArray.getString(R.styleable.FacebookCustomButton_text_source);
            if(text==null) { //no fb textView attribuute
                text = attributeArray.getString(R.styleable.FacebookCustomButton_android_text);
            }
            textSource = text;

            iconWidth = (int) attributeArray.getDimension(R.styleable.FacebookCustomButton_icon_width, Constants.fbButton_icon_default_width);
            iconHeight = (int) attributeArray.getDimension(R.styleable.FacebookCustomButton_icon_height, Constants.fbButton_icon_default_height);
            iconGravity = attributeArray.getInt(R.styleable.FacebookCustomButton_icon_gravity, Constants.fbButton_icon_default_gravity);

            iconMarginStart = (int) attributeArray.getDimension(R.styleable.FacebookCustomButton_icon_marginStart, Constants.fbButton_icon_default_margin_start);
            iconMarginEnd = (int) attributeArray.getDimension(R.styleable.FacebookCustomButton_icon_marginEnd, Constants.fbButton_icon_default_margin_end);
            iconMarginTop = (int) attributeArray.getDimension(R.styleable.FacebookCustomButton_icon_marginTop, Constants.fbButton_icon_default_margin_top);
            iconMarginBottom = (int) attributeArray.getDimension(R.styleable.FacebookCustomButton_icon_marginBottom, Constants.fbButton_icon_default_margin_bottom);

            text_marginStart = (int) attributeArray.getDimension(R.styleable.FacebookCustomButton_text_marginStart, Constants.fbButton_text_default_marginStart);
            text_marginTop = (int) attributeArray.getDimension(R.styleable.FacebookCustomButton_text_marginTop, Constants.fbButton_text_default_marginTop);
            text_marginEnd = (int) attributeArray.getDimension(R.styleable.FacebookCustomButton_text_marginEnd, Constants.fbButton_text_default_marginEnd);
            text_marginBottom = (int) attributeArray.getDimension(R.styleable.FacebookCustomButton_text_marginBottom, Constants.fbButton_text_default_marginBottom);

            textGravity = attributeArray.getInt(R.styleable.FacebookCustomButton_text_gravity, Constants.fbButton_icon_default_gravity);
            textSize = (int) attributeArray.getDimension(R.styleable.FacebookCustomButton_text_size, Constants.fbButton_text_size);

            //String textFontFamily = attributeArray.getString(R.styleable.FacebookCustomButton_text_font);
            //Typeface font = Typeface.createFromAsset(context.getAssets(), textFontFamily);
            font = ResourcesCompat.getFont(context, R.font.roboto_medium);
            //textView.setTypeface(font);

            textColor = attributeArray.getColor(R.styleable.FacebookCustomButton_text_color, getResources().getColor(R.color.fb_defaultColor));

        }
    }

    /**
     * Initalizes the button:
     *   - the container (linear layout)
     *   - the iconView view
     *   - the textView view
     */
    private void initializeButton() {
        initializeButtonContainer();
        iconView = initializeIconView();
        textView = initializeTextView();
        this.addView(iconView);
        this.addView(textView);
    }

    private void initializeButtonContainer() {
        this.setOrientation(LinearLayout.HORIZONTAL);

        if(this.getLayoutParams() == null) {
            LayoutParams containerParams = new LayoutParams(LayoutParams.WRAP_CONTENT, Constants.fbButton_default_height);

            containerParams.setMargins(Constants.fbButton_default_margin_start,
                    Constants.fbButton_default_margin_top,
                    Constants.fbButton_default_margin_end,
                    Constants.fbButton_default_margin_bottom);

            this.setLayoutParams(containerParams);
        }

        this.setGravity(Gravity.CENTER);

        this.setBackground(context.getDrawable(R.drawable.background_facebook_sign_up_button));
    }

    private TextView initializeTextView() {
        if(textSource == null) {
            textSource = context.getResources().getString(R.string.fbButton_default_text_source);
        }

        TextView textView = new TextView(context);
        textView.setText(textSource);

        textView.setGravity(textGravity);
        textView.setTextColor(textColor);
        textView.setTextSize(Utils.pxToSp(context, textSize));
        textView.setTypeface(font);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(text_marginStart, text_marginTop, text_marginEnd, text_marginBottom);
        textView.setLayoutParams(params);

        textView.setMaxLines(1);

        /*
        Typeface font = Typeface.createFromAsset(context.getAssets(), Constants.default_font_path);
        textView.setTypeface(font);
        */

        return textView;
    }

    private ImageView initializeIconView() {
        if(iconResource != null) {
            ImageView iconView = new ImageView(context);
            iconView.setImageDrawable(iconResource);

            LayoutParams iconViewParams = new LayoutParams(iconWidth, iconHeight);
            iconViewParams.gravity = iconGravity;
            iconViewParams.setMarginStart(iconMarginStart);
            iconViewParams.setMarginEnd(iconMarginEnd);

            iconView.setLayoutParams(iconViewParams);

            return iconView;
        } else {
            System.out.println("[Neuron.custombuttons.FCB]: Error getting iconView resource! Reutnring null.");
            return null;
        }
    }
}
