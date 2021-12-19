package Server;

import java.io.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_login_signup{


    public static void main(String args[])throws IOException{
        ServerSocket serversocket;
        Socket socket;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Server Started: ");
        try{
            serversocket = new ServerSocket(5400);//port number
        }
        catch(IOException e){
            e.printStackTrace();
            return;
        }
        while(true)
        {
            try{
                socket = serversocket.accept();
                System.out.println("Conected to Client: ");
                Thread t = new Thread(new HandleClient_Username(socket));
                t.start();
            }
            catch(IOException e){
                e.printStackTrace();
                return;
            }
        }
    }
}
