package org.lorenzoantonelli.tapdebloater;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.lorenzoantonelli.tapdebloater.core.AdbUtils;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(loadFXML("mainFxml"));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("TapDebloater");
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop(){
        new AdbUtils().removeTool();
        MainController.shutdown();
    }

}