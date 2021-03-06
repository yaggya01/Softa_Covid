package HomePage;

import Message.SearchMessage;
import NearbyCentres.NearbyCentres;
import SearchByUser.SearchByUser;
import User.User;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class HomePage {

    @FXML
    private Button choosePhotoButton;

    @FXML
    private Button uploadPhotoButton;

    @FXML
    private Button nearbyCentresButton;

    @FXML
    private Button cancel_button;

    @FXML
    private Button chooseIdProofButton;

    @FXML
    private Button uploadIdProofButton;

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label numberLabel;

    @FXML
    private Label booking_status_label;

    @FXML
    private Label firstDoseStatusLabel;

    @FXML
    private Label secondDoseStatusLabel;

    @FXML
    private ImageView photoImageView;

    @FXML
    private Hyperlink downloadIdProofLink;

    private static Socket socket;

    private File selectedIdProof;

    private File selectedPhoto;

    private User user;

    public void chooseIdProofButtonAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("F:\\projects"));
        selectedIdProof = fc.showOpenDialog(null);

        if(selectedIdProof != null) {
            System.out.println("selected id proof");
        } else {
            System.out.println("File is not valid");
        }
    }
    public void cancel_booking(ActionEvent actionEvent) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("in HomePage->cancel booking thread");
                    Socket socket2 = new Socket("localhost",5402);
                    ObjectOutputStream op = new ObjectOutputStream(socket2.getOutputStream());
                    op.writeObject(new SearchMessage("", "", -1, "", user,3));
                    op.flush();
                    try{
                        ObjectInputStream oi = new ObjectInputStream(socket2.getInputStream());
                        user = (User) oi.readObject();
                        initHomePageData(user);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }).start();
    }

    public void uploadIdProofButtonAction(ActionEvent event) throws IOException {
        upload("IdProof", selectedIdProof);
    }

    public void choosePhotoButtonAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("C:"));
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("jpg Files", "*.jpg")
        );
        selectedPhoto = fc.showOpenDialog(null);

        if(selectedPhoto != null) {
            System.out.println("selected photo");
        } else {
            System.out.println("File is not valid");
        }
    }

    public void uploadPhotoButtonAction(ActionEvent event) throws IOException {
        upload("Photo", selectedPhoto);
    }

    private void upload(String type, File selectedFile) {
        if (selectedFile == null) {
            System.out.println("Please select a file");
        } else {
            try {
                socket = new Socket("localhost", 25000);
                ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                op.writeObject(user);
                op.flush();

                // Create an input stream into the file you want to send.
                FileInputStream fileInputStream = new FileInputStream(selectedFile.getAbsolutePath());
                // Create an output stream to write to write to the server over the socket connection.
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                byte[] typeBytes = type.getBytes();
                // Send the length of the name of the file so server knows when to stop reading.
                dataOutputStream.writeInt(typeBytes.length);
                // Send the file name.
                dataOutputStream.write(typeBytes);

                // Get the name of the file you want to send and store it in filename.
                String fileName = selectedFile.getName();
                // Convert the name of the file into an array of bytes to be sent to the server.
                byte[] fileNameBytes = fileName.getBytes();
                // Create a byte array the size of the file so don't send too little or too much data to the server.

                byte[] fileBytes = new byte[(int) selectedFile.length()];

                // Put the contents of the file into the array of bytes to be sent so these bytes can be sent to the server.
                fileInputStream.read(fileBytes);

                // Send the length of the name of the file so server knows when to stop reading.
                dataOutputStream.writeInt(fileNameBytes.length);
                // Send the file name.
                dataOutputStream.write(fileNameBytes);

                // Send the length of the byte array so the server knows when to stop reading.
                dataOutputStream.writeInt(fileBytes.length);
                // Send the actual file.
                dataOutputStream.write(fileBytes);


                //Get the return message from the server
                ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                user = (User)oi.readObject();
                System.out.println("Message received from the server : " + user);
                initHomePageData(user);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //Closing the socket
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initHomePageData(User u) throws IOException {
        user = u;
        nameLabel.setText(user.getName());
        emailLabel.setText(user.getEmail());
        numberLabel.setText(user.getNumber());

        if(user.getBooking_status() == "none"){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    booking_status_label.setText(user.getBooking_status());
                    cancel_button.setVisible(false);
                }
            });

        }else{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    booking_status_label.setText(user.getBooking_status());
                    cancel_button.setVisible(true);
                }
            });
        }
        System.out.println("In HomePage and displaying value of user"  + user);
//        if(user != null){
            nameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
            numberLabel.setText(user.getNumber());
//        }

        if(user.getPhoto() != null) {
//            Image img = new Image(new ByteArrayInputStream(user.getPhoto()));
//            BufferedImage bg = ImageIO.read((ImageInputStream) img);
//
//            if(bg != null){
//                Image image = SwingFXUtils.toFXImage(bg, null);
//                photoImageView.setImage(image);
//            }
            BufferedImage img=ImageIO.read(new ByteArrayInputStream(user.getPhoto()));
            Image image = SwingFXUtils.toFXImage(img, null);
            photoImageView.setImage(image);
        } else {
//            Image image = new Image("man.png");
            Image image  = new Image(getClass().getResourceAsStream("man.png "));
            System.out.println("image : " + image);
            photoImageView.setImage(image);
        }
    }

    public void downloadIdProofAction(ActionEvent actionEvent) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select a folder to download the IdProof");
        File selectedDir = dirChooser.showDialog(nameLabel.getScene().getWindow());
        String selectedDirPath = selectedDir.getAbsolutePath();
        File file = getUniqueFileName(selectedDirPath, "IdProof.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(user.getIdProof());
//            fos.close(); // no need, try-with-resources auto close
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadCertificateAction(ActionEvent actionEvent) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select a folder to download the Certificate");
        File selectedDir = dirChooser.showDialog(nameLabel.getScene().getWindow());
        String selectedDirPath = selectedDir.getAbsolutePath();
        File file = getUniqueFileName(selectedDirPath, "CovidCertificate.pdf");
        GenerateCertificate.generateCertificate(file, user);
    }

    private File getUniqueFileName(String folderName, String searchedFilename) {
        int num = 1;
        String extension = getExtension(searchedFilename);
        String filename = searchedFilename.substring(0, searchedFilename.lastIndexOf("."));
        File file = new File(folderName, searchedFilename);
        while (file.exists()) {
            searchedFilename = filename + "("+(num++)+")"+extension;
            file = new File(folderName, searchedFilename);
        }
        return file;
    }

    private String getExtension(String name) {
        return name.substring(name.lastIndexOf("."));
    }

    public void logoutButtonAction(ActionEvent actionEvent) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "../sample/sample.fxml"
                )
        );

        Stage stage = (Stage) nameLabel.getScene().getWindow();
//            stage.hide();
        stage.setScene(
                new Scene(loader.load(),950, 740)
        );
        stage.show();
        Controller controller = loader.getController();
        controller.initSample();
    }

    public void searchSlotsButtonAction(ActionEvent actionEvent) {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "../SearchByUser/SearchByUser.fxml"
                    )
            );
            stage.setScene(new Scene(loader.load(),950, 740));
            SearchByUser controller = loader.getController();
            controller.initSearchByUserData(user);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void nearbyCentresButtonAction(ActionEvent actionEvent) {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "../NearbyCentres/NearbyCentres.fxml"
                    )
            );
            stage.setScene(new Scene(loader.load(),950, 740));
            NearbyCentres controller = loader.getController();
            controller.initNearbyCentres();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
