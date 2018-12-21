package phouse.com.phonemouse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Connection implements Serializable
{
    private InetSocketAddress address;
    private transient ExecutorService executorService;
    private transient  Socket socket;

    public Connection(final String ip_address, final int port_num) throws IOException
    {
        address = new InetSocketAddress(ip_address, port_num);
    }

    public void send(final String data)
    {
        Runnable sender = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    DataOutputStream output = new DataOutputStream(getSocket().getOutputStream());
                    output.writeUTF(data);
                    output.flush();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        };

        getExecutorService().submit(sender);

    }

    public boolean testConnection()
    {

        Callable<Boolean> tester = new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                DataOutputStream output = new DataOutputStream(getSocket().getOutputStream());
                output.writeUTF("test");
                output.flush();

                DataInputStream input = new DataInputStream(getSocket().getInputStream());
                String data = input.readUTF();

                return data.contains("ok");
            }
        };

        Future<Boolean> result = getExecutorService().submit(tester);
        try
        {
            return result.get(10, TimeUnit.SECONDS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public void disconnect()
    {
        try
        {
            getSocket().close();
            socket = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        getExecutorService().shutdown();
    }

    private ExecutorService getExecutorService()
    {
        if(executorService == null)
        {
            executorService = Executors.newSingleThreadExecutor();
        }

        return executorService;
    }

    private Socket getSocket() throws IOException
    {
        if(socket == null)
        {
            socket = new Socket();
            socket.connect(address);

        }

        return socket;
    }
}
