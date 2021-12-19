package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_SearchByUser {


    public static void main(String args[])throws IOException{
        ServerSocket serversocket;
        Socket socket;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Server_SearchByUser Started: ");
        try{
            System.out.println("Server_SearchByUser Started: ");
            serversocket = new ServerSocket(5402);//port number
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
                Thread t = new Thread(new HandleClient_SearchByUser(socket));
                t.start();
            }
            catch(IOException e){
                e.printStackTrace();
                return;
            }
        }
    }
}
