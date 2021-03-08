package org.lorenzoantonelli.tapdebloater;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.lorenzoantonelli.tapdebloater.core.AdbUtils;

import static java.lang.Thread.sleep;

public class AdbConnectController {

    @FXML
    TextField ipPort;

    @FXML
    TextField passCode;

    @FXML
    public void connect(){
        AdbUtils temp = new AdbUtils();
        temp.connectWirelessAdb(ipPort.getText(), passCode.getText());
        try {
            sleep(1000);
        } catch (InterruptedException ignored) { }
        if (temp.findDevice()) {
            Stage stage = (Stage) ipPort.getScene().getWindow();
            stage.close();
        }
        else new Alert(Alert.AlertType.ERROR,"Connessione fallita, verifica che i dati inseriti siano corretti e riprova!", ButtonType.OK).show();

    }

}
