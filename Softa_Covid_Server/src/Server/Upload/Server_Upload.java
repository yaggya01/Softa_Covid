package Server.Upload;

import java.net.ServerSocket;
import java.net.Socket;

public class Server_Upload {

    private static Socket socket;

    public static void main(String[] args)
    {
        try
        {
            int port = 25000;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server Started and listening to the port 25000");
            //Server is running always. This is done using this while(true) loop
            while(true)
            {
                //Reading the message from the client
                socket = serverSocket.accept();
                Thread t = new Thread(new HandleClient_Upload(socket));
                t.start();
            }
        }
        catch (Exception e)
        {
            System.out.println("Error : "+e.getMessage());
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (Exception e) {
                System.out.println("Error : "+e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
