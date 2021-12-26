package LOGIN_OFFICIAL;

import Message.Returned_SearchMessage;
import Message.SearchMessage;
import User.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.Controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class dashBoard_official {
    public Button user;
    public Button logoutBt;
    public TextField HIDTF;
    public TextField vaccine_nameTF;
    public TextField increaseTF;
    public Button update_btn;
    public Label status_label;
    public void lbUser(ActionEvent actionEvent)throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("BACK");
                Parent root=null;
                Stage stage = (Stage) user.getScene().getWindow();
                try{
                    root = FXMLLoader.load(getClass().getResource("./user_vac_status.fxml"));
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                stage.setScene(new Scene(root,950, 740));
            }
        });
    }

    public void onclickBt(ActionEvent actionEvent)throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "../sample/sample.fxml"
                )
        );

        Stage stage = (Stage) logoutBt.getScene().getWindow();
//            stage.hide();
        stage.setScene(
                new Scene(loader.load(),950, 740)
        );
        stage.show();
        Controller controller = loader.getController();
        controller.initSample();
    }

    public void Update(ActionEvent actionEvent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("in dashboard official->update vaccine thread");
                    Socket socket = new Socket("localhost",5402);
                    ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                    int hid = Integer.parseInt(HIDTF.getText());
                    String vaccine_name = vaccine_nameTF.getText();
                    SearchMessage new_obj = new SearchMessage("", "", hid, vaccine_name, null, 4);
                    new_obj.no_of_vaccines = Integer.parseInt(increaseTF.getText());
                    op.writeObject(new_obj);
                    op.flush();
                    try{
                        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                        Returned_SearchMessage rsm= (Returned_SearchMessage) oi.readObject();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                status_label.setText(rsm.StatusOfBookingOperation);
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
}
