/**
 * (C)opyright 2014 - UrbanPulse - All rights Reserved
 * Released by CARDIWEB
 * File : ImageHighlight.java
 * @date 28 févr. 2014
 * @author Anthony
 */
package com.formation.utils.ihm;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

/**
 * @author Anthony
 *
 */
public class ButtonHighlight extends Button implements OnTouchListener {

    //pour gerer le clic
    private int colorId, highlightedColorId;

    /**
     * @param context
     */
    public ButtonHighlight(final Context context) {
        super(context);
        setOnTouchListener(this);
    }

    /**
     * @param context
     * @param attrs
     */
    public ButtonHighlight(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ButtonHighlight(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (highlightedColorId != 0) {
                getBackground().setColorFilter(highlightedColorId, Mode.MULTIPLY);
            }
            else {
                //grey color filter, you can change the color as you like
                getBackground().setColorFilter(Color.argb(155, 185, 185, 185), Mode.MULTIPLY);
            }
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            if (colorId != 0) {
                getBackground().setColorFilter(colorId, Mode.MULTIPLY);
            }
            else {
                getBackground().setColorFilter(Color.argb(0, 185, 185, 185), Mode.MULTIPLY);
            }
        }
        return false;
    }

    /**
     * Permet de gerer les changements de couleur d'une image sur le ontouch
     * @param colorId
     * @param highlightedColorId
     */
    public void setColorFilter(final int colorId, final int highlightedColorId) {
        getBackground().setColorFilter(colorId, Mode.MULTIPLY);
        this.highlightedColorId = highlightedColorId;
        this.colorId = colorId;
    }

    /**
     * Même chose avec des couleurs du fichier de resource
     * @param resource
     * @param resColorId
     * @param resHighlightedColorId
     */
    public void setColorFilter(final Resources resource, final Integer resColorId, final Integer resHighlightedColorId) {

        setColorFilter(resColorId != null ? resource.getColor(resColorId) : 0,
                resHighlightedColorId != null ? resource.getColor(resHighlightedColorId) : 0);
    }

}
