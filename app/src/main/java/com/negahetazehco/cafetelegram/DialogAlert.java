package com.negahetazehco.cafetelegram;

import android.app.Dialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;


public class DialogAlert extends Dialog {

    public TextView txtDesc;
    public TextView positive;
    public TextView negative;

    public DialogAlert(Context context, int animat) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.setContentView(R.layout.dialog_alert);

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        this.getWindow().setLayout((display.getWidth() - 40), LayoutParams.WRAP_CONTENT);
        this.getWindow().getAttributes().windowAnimations = animat;

        txtDesc = (TextView) findViewById(R.id.txtDesc);
        txtDesc.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        txtDesc.setTypeface(AndroidUtilities.getTypeface("fonts/"+Setting.get(Setting.FONT_FAMILY_LABLE, Setting.FONT_FAMILY_VALUE)));

        positive = (TextView) findViewById(R.id.txtSend);
        positive.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        positive.setTypeface(AndroidUtilities.getTypeface("fonts/"+Setting.get(Setting.FONT_FAMILY_LABLE, Setting.FONT_FAMILY_VALUE)));

        negative = (TextView) findViewById(R.id.txtCancel);
        negative.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        negative.setTypeface(AndroidUtilities.getTypeface("fonts/"+Setting.get(Setting.FONT_FAMILY_LABLE, Setting.FONT_FAMILY_VALUE)));
    }

}
