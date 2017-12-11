package com.example.michael.todolist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Michael Purcell on 12/3/2017.
 */

public class ExtendedTextView extends android.support.v7.widget.AppCompatTextView {
    public ExtendedTextView(Context context) {
        super(context);
    }

    public ExtendedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
