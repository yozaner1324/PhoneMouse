package phouse.com.phonemouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;

public class Connection implements Serializable
{
    private Socket socket;

    public Connection(String ip_address, int port_num) throws IOException
    {
        socket = new Socket(ip_address, port_num);
        socket.setSoTimeout(1000);
    }

    public void send(String data)
    {
        try
        {
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write(data, 0, data.length());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean testConnection()
    {
        send("TestConnection");

        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if(reader.readLine().equals("OK"))
            {
                return true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        return false;
    }
}
