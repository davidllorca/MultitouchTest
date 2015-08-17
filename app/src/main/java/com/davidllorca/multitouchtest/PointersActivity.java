package com.davidllorca.multitouchtest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;


public class PointersActivity extends ActionBarActivity {

    Paint mPaint = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_pointers);
        View v = new Board(this);
        setContentView(v);
        // set up brush
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(4);

        Resources res = getResources();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
            showMessage(res.getString(R.string.no_touch));
        } else if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)) {
            showMessage(res.getString(R.string.one_finger_touch));

        } else if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT)) {
            showMessage(res.getString(R.string.two_fingers_touch));
        } else {
            showMessage(res.getString(R.string.multifinger_touch));
        }
    }

    /**
     * +
     * Show Toast message
     *
     * @param msg
     */
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /*
     This method don't
    */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final String TAG = "Pointer";
        String[] actions = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(actions[actionCode]);
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("{").append("pointer " + i);
            // id pointer
            sb.append(" pid= ").append(event.getPointerId(i));
            // position on screen (x, y)
            sb.append("}->(").append((int) event.getX(i));
            sb.append(", ").append((int) event.getY(i));
            // pressure [0 - 1]
            sb.append(", ").append((event.getPressure(i)));
            sb.append(")");
            if (i + 1 < event.getPointerCount()) sb.append(";");
        }
        sb.append("]");
        Log.d(TAG, sb.toString());

        getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        return super.onTouchEvent(event);
    }

    /**
     * View that controls touchs.
     */
    public class Board extends View {

        private ArrayList<PointF> pointers = null;

        public Board(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFAAAAAA);
            if (pointers != null) {
                PointF p = null;
                Iterator<PointF> it = pointers.iterator();
                while (it.hasNext()) {
                    p = it.next();
                    //paint a circumference with r=40 and center(x,y) of pulsation
                    canvas.drawCircle(p.x, p.y, 40, mPaint);
                }
            }
            super.onDraw(canvas);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_POINTER_UP:
                    pointers = new ArrayList<PointF>();
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        pointers.add(new PointF(event.getX(i), event.getY(i)));
                    }
                    invalidate();
                    break;
            }
            // If return false, onTouchEvent method of Activity will work because It means that OnTouchEvent of Board class hasn't handled event and it go next
            return true;
        }
    }
}
