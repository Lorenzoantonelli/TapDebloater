package org.lorenzoantonelli.tapdebloater;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.lorenzoantonelli.tapdebloater.core.AdbUtils;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController {

    @FXML
    Label appNameLabel, lastRemovedLabel;

    @FXML
    CheckBox liveUpdateCheckBox;

    @FXML
    TextField packageNameTextField;

    @FXML
    Button uninstallCustomAppButton, disableCustomAppButton, uninstallFacebookButton, undoButton, uninstallGappsButton;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledExecutorService scheduler2 = Executors.newSingleThreadScheduledExecutor();

    private final AdbUtils utils=new AdbUtils();

    public void updateCurrentApp(){
        Platform.runLater(() -> {
            if (liveUpdateCheckBox.isSelected()){
                packageNameTextField.setEditable(false);
                String packageName = utils.getPackageName();
                if (!packageNameTextField.getText().equals(packageName)) {
                    appNameLabel.setText("App corrente: " + utils.getAppName(packageName));
                    packageNameTextField.setText(packageName);
                }
            }
            else{
                packageNameTextField.setEditable(true);
                appNameLabel.setText("App corrente: ");
            }
        });
    }

    @FXML
    public void initialize(){

        Runnable currentApp=()->{
            boolean buttonStatus=!utils.findDevice();
            liveUpdateCheckBox.setDisable(buttonStatus);
            uninstallCustomAppButton.setDisable(buttonStatus);
            disableCustomAppButton.setDisable(buttonStatus);
            undoButton.setDisable(buttonStatus);
            packageNameTextField.setDisable(buttonStatus);
            uninstallFacebookButton.setDisable(buttonStatus);
            uninstallGappsButton.setDisable(buttonStatus);
            updateCurrentApp();
        };

        Runnable installTool=()->{
            if(!utils.checkTools())
                utils.installTool();
            else scheduler2.shutdown();
        };

        scheduler.scheduleWithFixedDelay(currentApp,0,1,TimeUnit.SECONDS);
        scheduler2.scheduleWithFixedDelay(installTool,0,2,TimeUnit.SECONDS);

        utils.startAdbServer();
    }

    @FXML
    public void uninstallCustomApp(){
        String packageName = packageNameTextField.getText();
        boolean status=utils.removeApp(packageName);
        new Alert(Alert.AlertType.NONE,((status)? "Operazione completata con successo!":"Operazione fallita!"), ButtonType.OK).show();
        if(status) lastRemovedLabel.setText("Ultima app rimossa: "+packageName);
    }

    public boolean uninstallCustomAppSilent(String packageName){
        System.out.println(packageName);
        return utils.removeApp(packageName);
    }

    @FXML
    public void disableCustomApp(){
        boolean status=utils.disableApp(packageNameTextField.getText());
        new Alert(Alert.AlertType.NONE,((status)? "Operazione completata con successo!":"Operazione fallita!"), ButtonType.OK).show();
    }

    @FXML
    public void uninstallFacebook(){
        boolean status=
                utils.removeApp("com.facebook.appmanager") |
                utils.removeApp("com.facebook.services") |
                utils.removeApp("com.facebook.system");
        new Alert(Alert.AlertType.NONE,((status)? "Operazione completata con successo!":"Operazione fallita!"), ButtonType.OK).show();
    }

    @FXML
    public void restoreApp(){
        String packageName=packageNameTextField.getText();
        boolean status=utils.restoreApp(packageName);
        new Alert(Alert.AlertType.NONE,((status)? "Operazione completata con successo!":"Operazione fallita!"), ButtonType.OK).show();
        if (status && lastRemovedLabel.getText().equals("Ultima app rimossa: "+packageName)){
            lastRemovedLabel.setText("Ultima app rimossa: ");
        }
    }

    @FXML
    public void uninstallGapps(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GoogleFxml.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void connectAdb(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("adbConnectFxml.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.setResizable(false);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void shutdown(){
        scheduler.shutdown();
        scheduler2.shutdown();
    }

}
