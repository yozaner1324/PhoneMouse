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

        ControlFragment fragment = (ControlFragment) getSupportFragmentManager().findFragmentById(R.id.frag);
        fragment.setup(commander);
    }

    @Override
    public void onBackPressed()
    {
        commander.disconnect();
        super.onBackPressed();
    }
}
