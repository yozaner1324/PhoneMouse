package phouse.com.phonemouse;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

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

        ControlFragment fragment = (ControlFragment) getSupportFragmentManager().findFragmentById(R.id.frag);
        fragment.setup(commander);

        View pad = findViewById(R.id.pad);
        pad.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getActionMasked();
                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        commander.moveCursor(event.getX() - startX, event.getY() - startY);
                        startX = event.getX();
                        startY = event.getY();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        commander.disconnect();
        super.onBackPressed();
    }
}
