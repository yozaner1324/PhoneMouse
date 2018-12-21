package phouse.com.phonemouse;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MouseActivity extends AppCompatActivity
{

    private MouseCommander commander;
    private float lastY = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        Connection conn = (Connection) getIntent().getExtras().get("conn");
        commander = new MouseCommander(conn);

        // left click
        final Button left = findViewById(R.id.leftClick);
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
                    int notches = Math.round(event.getY(pointerId) - lastY)/2;
                    if(notches != 0)
                    {
                        commander.scroll(notches);
                        lastY = event.getY(pointerId);
                    }
                }
                else if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    lastY = event.getY(pointerId);
                }
                return true;
            }
        });
    }

    public void rightClick(View view)
    {
        commander.rightClick();
    }

    @Override
    public void onBackPressed()
    {
        commander.disconnect();
        super.onBackPressed();
    }
}
