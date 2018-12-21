package phouse.com.phonemouse;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

        // init control fragment
        ControlFragment fragment = (ControlFragment) getSupportFragmentManager().findFragmentById(R.id.frag);
        fragment.setup(commander);

        // starts the image processing for mouse movement
        commander.startMotionTracking();
    }

    @Override
    public void onBackPressed()
    {
        commander.stopMotionTracking();
        commander.disconnect();
        super.onBackPressed();
    }
}
