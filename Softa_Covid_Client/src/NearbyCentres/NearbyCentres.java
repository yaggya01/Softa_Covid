package NearbyCentres;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class NearbyCentres {

    @FXML
    private Label nearbyLabel;

    @FXML
    private WebView nearbyWidgetWebView;

    WebEngine webEngine = null;

    public void initNearbyCentres() {
        nearbyWidgetWebView.setZoom(0.5);
        webEngine = nearbyWidgetWebView.getEngine();
        webEngine.load((getClass().getResource("/NearbyCentres/NearbyWidget.html")).toString());
//        webEngine.executeScript(getClass().getResource("/NearbyCentres/NearbyWidget.js").toString());
//        webEngine.load("https://www.google.com/maps/@22.5574912,88.3785728,12z");
    }

}
