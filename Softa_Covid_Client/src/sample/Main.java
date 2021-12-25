package sample;

import HomePage.HomePage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "sample.fxml"
                )
        );

        Stage stage = primaryStage;
//            stage.hide();
        stage.setScene(
                new Scene(loader.load(),950, 740)
        );
        primaryStage.show();
        Controller controller = loader.getController();
        controller.initSample();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
