package phouse.com.phonemouse;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class TouchPadActivity extends AppCompatActivity
{

    private MouseCommander commander;
    private float startX = 0;
    private float startY = 0;
    private float lastY = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_pad);

        Connection conn = (Connection) getIntent().getExtras().get("conn");
        commander = new MouseCommander(conn);

        // left click
        Button left = findViewById(R.id.leftClick);
        left.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        commander.leftClick();
                        break;

                    case MotionEvent.ACTION_UP:
                        commander.releaseLeftClick();
                        break;
                }

                return false;
            }

        });

        // scrolling
        ImageView scroll = findViewById(R.id.scroll);
        scroll.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int index = event.getActionIndex();
                int pointerId = event.getPointerId(index);
                if(event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    commander.scroll(Math.round(lastY - event.getY(pointerId)));
                    lastY = event.getY(pointerId);
                }
                return false;
            }
        });
    }

    // touch pad
    public boolean onTouchEvent(MotionEvent event)
    {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX(pointerId);
                startY = event.getY(pointerId);
                break;
            case MotionEvent.ACTION_MOVE:
                commander.moveCursor(event.getX(pointerId) - startX, event.getY(pointerId) - startY);
                startX = event.getX(pointerId);
                startY = event.getY(pointerId);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startX = 0;
                startY = 0;
                break;
        }
        return true;
    }

    // right click
    public void rightClick(View view)
    {
        commander.rightClick();
    }
}
