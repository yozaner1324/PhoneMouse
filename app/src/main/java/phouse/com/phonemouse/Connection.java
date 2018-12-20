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
                    Socket socket = new Socket();
                    socket.connect(address);

                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    output.writeUTF(data);
                    output.flush();

                    socket.close();
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
                Socket socket = new Socket();
                socket.connect(address);

                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF("test");
                output.flush();

                DataInputStream input = new DataInputStream(socket.getInputStream());
                String data = input.readUTF();
                socket.close();

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

    private ExecutorService getExecutorService()
    {
        if(executorService == null)
        {
            executorService = Executors.newSingleThreadExecutor();
        }

        return executorService;
    }
}
