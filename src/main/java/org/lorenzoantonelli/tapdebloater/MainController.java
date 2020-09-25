package org.lorenzoantonelli.tapdebloater;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.lorenzoantonelli.tapdebloater.core.AdbUtils;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController {

    @FXML
    Label appNameLabel;

    @FXML
    Label packageNameLabel;

    @FXML
    TextField customApp;

    @FXML
    Button uninstallCurrentAppButton;

    @FXML
    Button uninstallCustomAppButton;

    @FXML
    Button uninstallFacebookButton;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledExecutorService scheduler2 = Executors.newSingleThreadScheduledExecutor();

    private final AdbUtils utils=new AdbUtils();

    public void updateCurrentApp(){
        Platform.runLater(() -> {
            String packageName= utils.getPackageName();
            appNameLabel.setText("App corrente: "+utils.getAppName(packageName));
            packageNameLabel.setText("Package name: "+packageName);
        });
    }

    @FXML
    public void initialize(){

        Runnable currentApp=()->{
            boolean buttonStatus=!utils.findDevice();
            uninstallCurrentAppButton.setDisable(buttonStatus);
            uninstallCustomAppButton.setDisable(buttonStatus);
            customApp.setDisable(buttonStatus);
            uninstallFacebookButton.setDisable(buttonStatus);
            updateCurrentApp();
        };

        Runnable installTool=()->{
            if(!utils.checkTools())
                utils.installTool();
            else scheduler2.shutdown();
        };

        scheduler.scheduleWithFixedDelay(currentApp,0,1,TimeUnit.SECONDS);
        scheduler2.scheduleWithFixedDelay(installTool,0,2,TimeUnit.SECONDS);
    }

    @FXML
    public void uninstallCurrentApp(){
        String packageName=utils.getPackageName();
        new Alert(Alert.AlertType.NONE,(utils.removeApp(packageName)? "Operazione completata con successo!":"Operazione fallita!"), ButtonType.OK).show();
    }

    @FXML
    public void uninstallCustomApp(){
        boolean status=utils.removeApp(customApp.getText());
        new Alert(Alert.AlertType.NONE,((status)? "Operazione completata con successo!":"Operazione fallita!"), ButtonType.OK).show();
        if (status){
            customApp.clear();
        }
    }

    @FXML
    public void uninstallFacebook(){
        utils.removeApp("com.facebook.appmanager");
        utils.removeApp("com.facebook.services");
        utils.removeApp("com.facebook.system");
        new Alert(Alert.AlertType.NONE,"Operazione completata!", ButtonType.OK).show();
    }

    public static void shutdown(){
        scheduler.shutdown();
        scheduler2.shutdown();
    }

}
