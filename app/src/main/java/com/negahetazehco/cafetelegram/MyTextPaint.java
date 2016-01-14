package com.negahetazehco.cafetelegram;

import android.graphics.Typeface;
import android.text.TextPaint;

import org.telegram.messenger.AndroidUtilities;

public class MyTextPaint extends TextPaint
{
    public MyTextPaint(int Flag)
    {
        super(Flag);
        Typeface localTypeface = AndroidUtilities.getTypeface(
                "fonts/" + Setting.get(Setting.FONT_FAMILY_LABLE, Setting.FONT_FAMILY_VALUE));
        if ((localTypeface != null) || (localTypeface != Typeface.DEFAULT)) {
            setTypeface(localTypeface);
        }
    }
}

