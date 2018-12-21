package phouse.com.phonemouse;

import org.opencv.core.Mat;

import java.util.Timer;
import java.util.TimerTask;

public class MouseCommander
{
    private Connection conn;
    private Timer timer;
    private Mat prevPic;

    public MouseCommander(Connection connection)
    {
        conn = connection;
        timer = null;
        prevPic = null;
    }

    public void leftClick()
    {
        conn.send("left");
    }

    public void releaseLeftClick()
    {
        conn.send("release");
    }

    public void rightClick()
    {
        conn.send("right");
    }

    public void moveCursor(float x, float y)
    {
        conn.send("move " + Math.round(x) + " " + Math.round(y));
    }

    public void scroll(float amount)
    {
        conn.send("scroll " + Math.round(amount));
    }

    public void startMotionTracking()
    {
        timer = new Timer();

        TimerTask process_motion = new TimerTask()
        {
            @Override
            public void run()
            {

            }
        };

        timer.scheduleAtFixedRate(process_motion, 1000, 1000);
    }

    public void stopMotionTracking()
    {
        timer.cancel();
    }

    public void disconnect()
    {
        conn.disconnect();
    }
}
