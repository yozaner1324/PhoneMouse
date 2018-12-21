package phouse.com.phonemouse;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

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

    @Override
    public void onBackPressed()
    {
        commander.disconnect();
        super.onBackPressed();
    }
}
