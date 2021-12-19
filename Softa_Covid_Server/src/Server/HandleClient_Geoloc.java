package Server;

import Message.*;

import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.*;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

public class HandleClient_Geoloc implements Runnable {
    final private Socket socket;
    ObjectInputStream oi;
    ObjectOutputStream op;

    public HandleClient_Geoloc(Socket s) {
        this.socket = s;
        try {
            oi = new ObjectInputStream(socket.getInputStream());
            op = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
