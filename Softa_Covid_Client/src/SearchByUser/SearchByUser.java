package SearchByUser;

import HomePage.HomePage;
import Message.Returned_SearchMessage;
import Message.SearchMessage;
import Message.Hosp_info;
import User.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    public TextField HIDTF;
    public TextField VacTF;
    public Button bookbtn;
    public Button searchbtn;
    public Button backbtn;
    public TextArea txtarea;
    public Socket socket;
    public Label label;
    ObjectInputStream oi = null;
    Returned_SearchMessage m;

    private User user;

    public void Searching(ActionEvent actionEvent)throws Exception {
        System.out.println("in searchByUser.java -> User: " + user.getEmail());
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
                    op.writeObject(new SearchMessage(state, city, -1, "", user, 1));
                    op.flush();
                    try{
                        oi = new ObjectInputStream(socket.getInputStream());
                        m= (Returned_SearchMessage) oi.readObject();
                        if(m.ans.size() == 0){

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    label.setText("Not Found Anything");
                                }
                            });
                        }else{

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    for(Hosp_info a: m.ans){
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
    public void Booking(ActionEvent actionEvent)throws Exception {
//        System.out.println("in searchByUser.java -> User: " + user.getEmail());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("in searchByUser->Booking thread");
                    socket = new Socket("localhost",5402);
                    ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                    int hid = Integer.parseInt(HIDTF.getText());
                    String vaccine_name = VacTF.getText();
                    op.writeObject(new SearchMessage("", "", hid, vaccine_name, user,2));
                    op.flush();
                    try{
                        oi = new ObjectInputStream(socket.getInputStream());
                        m= (Returned_SearchMessage) oi.readObject();
//                        System.out.println("in search by user " + m.StatusOfBookingOperation);
                        user = m.user;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                label.setText(m.StatusOfBookingOperation);
                            }
                        });
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
    public void initSearchByUserData(User u) throws IOException {
        user = u;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("in searchByUser->initialize thread");
                    socket = new Socket("localhost",5402);
                    ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                    op.writeObject(new SearchMessage("", "", -1, "", user,0));
                    op.flush();
                    try{
                        oi = new ObjectInputStream(socket.getInputStream());
                        m= (Returned_SearchMessage) oi.readObject();
                        user = m.user;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                label.setText(m.vaccineStatusOfUser);
                            }
                        });


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
    public void go_back(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "../HomePage/HomePage.fxml"
                )
        );

        Stage stage = (Stage) backbtn.getScene().getWindow();
//            stage.hide();
        stage.setScene(
                new Scene(loader.load(),950, 740)
        );
        HomePage controller = loader.getController();
        System.out.println("In LoginUser and displaying value of m.user:\n"  + m.user);
        controller.initHomePageData(m.user);
    }


}
