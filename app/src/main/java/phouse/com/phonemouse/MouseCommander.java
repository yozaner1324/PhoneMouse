package phouse.com.phonemouse;

import org.opencv.core.Mat;

public class MouseCommander
{
    private Connection conn;
    private Mat prevPic;

    public MouseCommander(Connection connection)
    {
        conn = connection;
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

    }

    private void processMotion()
    {

    }

    public void disconnect()
    {
        conn.disconnect();
    }

}
