package phouse.com.phonemouse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class ConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
    }

    public void onConnectClick(View view) {
        Intent intent = new Intent(this, MouseActivity.class);
        EditText port = findViewById(R.id.port_entry);
        EditText ip = findViewById(R.id.ip_entry);

        // validate port and ip
        Pattern port_pat = Pattern.compile("\\d{1,5}");
        Pattern ip_pat = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        if(port_pat.matcher(port.getText().toString()).matches() && ip_pat.matcher(ip.getText().toString()).matches())
        {
            // put connection here
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please enter valid IP Address and Port Number", Toast.LENGTH_SHORT).show();
        }
    }
}
