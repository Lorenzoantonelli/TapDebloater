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
        Scene scene = new Scene(loadFXML());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("TapDebloater");
        stage.show();
    }

    private static Parent loadFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainFxml" + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop(){
        MainController.shutdown();
        AdbUtils temp=new AdbUtils();
        temp.removeTool();
        temp.killAdbServer();
    }

}