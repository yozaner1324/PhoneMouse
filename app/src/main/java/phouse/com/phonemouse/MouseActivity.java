package phouse.com.phonemouse;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MouseActivity extends AppCompatActivity
{

    private MouseCommander commander;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        Connection conn = (Connection) getIntent().getExtras().get("conn");
        commander = new MouseCommander(conn);

        Button left = findViewById(R.id.leftClick);
        left.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_BUTTON_PRESS:
                        commander.leftClick();
                        break;

                    case MotionEvent.ACTION_BUTTON_RELEASE:
                        commander.releaseLeftClick();
                        break;
                }

                return true;
            }

        });

    }

    public void rightClick(View view)
    {
        commander.rightClick();
    }
}
