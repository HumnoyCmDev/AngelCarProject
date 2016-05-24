package com.hndev.library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.hndev.library.R;

/**
 * Created by humnoy on 25/2/59.
 */
public class AngelCarToggleButton extends ImageView{

    public AngelCarToggleButton(Context context) {
        super(context);
        initialize();
    }

    public AngelCarToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public AngelCarToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public AngelCarToggleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }


    boolean isSelected = false;
    private void initialize(){
        setImageResource(R.drawable.ic_gear_mt);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isSelected)
                    setImageResource(R.drawable.ic_gear_mt);
                else
                    setImageResource(R.drawable.ic_gear_at);

                isSelected = !isSelected ? true : false ;
            }
        });
    }
}
