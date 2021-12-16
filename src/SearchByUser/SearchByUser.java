package SearchByUser;

import Message.Returned_SearchMessage;
import Message.SearchMessage;
import Message.Temp;
import User.User;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SearchByUser {
    public Label StateLabel;
    public Label CityLabel;
    public Label NotFoundLabel;
    public TextField state_name;
    public TextField city_name;
    public Button searchbtn;
    public Button backbtn;
    public TextArea txtarea;
    public Socket socket;
    private Label nameLabel;
    ObjectInputStream oi = null;
    Returned_SearchMessage m;

    private User user;

    public void Searching(ActionEvent actionEvent)throws Exception {
//        System.out.println("in searchByUser.java -> User: " + user.getEmail());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("in searchByUser.java thread");
                    socket = new Socket("localhost",5402);
                    ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                    String state=state_name.getText();
                    String city = city_name.getText();
                    System.out.println(state);
                    op.writeObject(new SearchMessage(state, city));
                    op.flush();
                    try{
                        oi = new ObjectInputStream(socket.getInputStream());
                        m= (Returned_SearchMessage) oi.readObject();
                        if(m.ans.size() == 0){

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    NotFoundLabel.setText("Not Found Anything");
                                }
                            });
                        }else{

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    for(Temp a: m.ans){
                                        txtarea.appendText(a + "\n");
                                    }
                                }
                            });

                        }
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
//    public void go_back(ActionEvent actionEvent) {
//        Parent root=null;
//        Stage stage = (Stage) nameLabel.getScene().getWindow();
//        try{
//            FXMLLoader loader = new FXMLLoader(
//                    getClass().getResource(
//                            "../HomePage/HomePage.fxml"
//                    )
//            );
//            stage.setScene(new Scene(loader.load(),600, 400));
//            SearchByUser controller = loader.getController();
//            controller.initSearchByUserData(user);
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//    }
    public void initSearchByUserData(User u) throws IOException {
        user = u;
    }

}
