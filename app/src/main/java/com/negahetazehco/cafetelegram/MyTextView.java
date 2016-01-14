package com.negahetazehco.cafetelegram;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;

public class MyTextView extends TextView
{
    public MyTextView(Context paramContext)
    {
        super(paramContext);
        Typeface localTypeface = AndroidUtilities.getTypeface(
                "fonts/" + Setting.get(Setting.FONT_FAMILY_LABLE, Setting.FONT_FAMILY_VALUE));
        if ((localTypeface != null) || (localTypeface != Typeface.DEFAULT)) {
            setTypeface(localTypeface);
        }
    }
}

