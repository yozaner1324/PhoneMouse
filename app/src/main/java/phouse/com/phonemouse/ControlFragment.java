package phouse.com.phonemouse;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class ControlFragment extends Fragment
{
    private MouseCommander commander;
    private float lastY;

    public ControlFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        commander = null;
        lastY = 0;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_control, container, false);
    }

    // must be called for controls to work
    @SuppressLint("ClickableViewAccessibility")
    public void setup(MouseCommander com)
    {
        commander = com;
        // left click
        final Button left = getView().findViewById(R.id.leftClick);
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

        // right click
        final Button right = getActivity().findViewById(R.id.rightClick);
        right.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                commander.rightClick();
            }
        });

        // scrolling
        ImageView scroll = getView().findViewById(R.id.scroll);
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
}
