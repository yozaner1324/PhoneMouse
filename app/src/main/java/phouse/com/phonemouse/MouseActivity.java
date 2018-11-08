package phouse.com.phonemouse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MouseActivity extends AppCompatActivity
{

    private Connection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);

        conn = (Connection) getIntent().getExtras().get("conn");
    }
}
