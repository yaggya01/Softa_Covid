package Server.Upload;


import User.User;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class HandleClient_Upload implements Runnable {

    final private Socket socket;

    public HandleClient_Upload(Socket s) {
        this.socket = s;
//        try {
//            oi = new ObjectInputStream(socket.getInputStream());
//            op = new ObjectOutputStream(socket.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void run() {
        try {

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            User user = (User) in.readObject();
            System.out.println("HandleClient_Upload : " + user);

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            int uploadTypeLength = dataInputStream.readInt();
            byte[] uploadTypeBytes = new byte[uploadTypeLength];

            // Read from the input stream into the byte array.
            dataInputStream.readFully(uploadTypeBytes, 0, uploadTypeBytes.length);
            String uploadType = new String(uploadTypeBytes, StandardCharsets.UTF_8); // for UTF-8 encoding
            System.out.println("uploadType : " + uploadType);

            // Read the size of the file name so know when to stop reading.
            int fileNameLength = dataInputStream.readInt();
            System.out.println("File name size: " + fileNameLength);

            // If the file exists
            if (fileNameLength > 0) {
                // Byte array to hold name of file.
                byte[] fileNameBytes = new byte[fileNameLength];

                // Read from the input stream into the byte array.
                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);

                // Create the file name from the byte array.
                String fileName = new String(fileNameBytes);
                System.out.println("File name: " + fileName);

                // Read how much data to expect for the actual content of the file.
                int fileContentLength = dataInputStream.readInt();
                System.out.println("File content length: " + fileContentLength);

                // If the file exists.
                if (fileContentLength > 0) {
                    int fileId = 0;

                    // Array to hold the file data.
                    byte[] fileContentBytes = new byte[fileContentLength];
                    // Read from the input stream into the fileContentBytes array.
                    dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);

                    uploadFile(fileContentBytes, user, uploadType);

//                    //Sending the response back to the client
//                    String returnMessage = "Return message";
//                    op.writeObject(returnMessage);
//                    System.out.println("Message sent to the client is " + returnMessage);
//                    op.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadFile(byte[] fileContentBytes, User user, String uploadType) {        //uploading the file to database
        String url = "jdbc:mysql://localhost:3306/Covid";
        String sqlUser = "root";
        String password = "";

        String sql = "UPDATE user set " + uploadType + "=? WHERE username=?";

        try (Connection con = DriverManager.getConnection(url, sqlUser, password);
             PreparedStatement pst = con.prepareStatement(sql)) {
            try {
                pst.setBinaryStream(1, new ByteArrayInputStream(fileContentBytes), fileContentBytes.length);
                pst.setString(2, user.getUsername());
                pst.executeUpdate();

                String q = "Select * from USER where username=?";
                PreparedStatement p = con.prepareStatement(q);
                p.setString(1, user.getUsername());
                ResultSet res = p.executeQuery();
                if (res.next()) {
                    User u = getUser(res);

                    ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                    op.writeObject(u);
                    op.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(uploadType + " inserted successfully....");
    }

    User getUser(ResultSet resultSet) {
        User user = new User();
        try {
            ResultSetMetaData metadata = resultSet.getMetaData();
            int numberOfCols = metadata.getColumnCount();

            for (int i = 1; i <= numberOfCols; i++) {
                switch (metadata.getColumnName(i)) {
                    case "username":
                        user.setUsername(resultSet.getString(i));
                        break;
                    case "name":
                        user.setName(resultSet.getString(i));
                        break;
                    case "number":
                        user.setNumber(resultSet.getString(i));
                        break;
                    case "email":
                        user.setEmail(resultSet.getString(i));
                        break;
                    case "idproof":
                        user.setIdProof(resultSet.getBytes(i));
                        break;
                    case "photo":
                        user.setPhoto(resultSet.getBytes(i));
                        break;
                    case "certificate":
                        System.out.println("Certificate HandleClient_Upload.java Line 151");
                        break;
                    default:
                        System.out.println("Unexpected value: " + metadata.getColumnName(i));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("HandleClient 160 User:\n" + user);
        return user;
    }
}