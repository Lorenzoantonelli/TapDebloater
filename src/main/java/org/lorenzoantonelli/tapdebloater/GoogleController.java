package org.lorenzoantonelli.tapdebloater;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;

public class GoogleController {

    @FXML
    CheckBox googleCheckbox, calendarCheckbox, gmailCheckbox, mapsCheckbox, fotoCheckbox;

    @FXML
    CheckBox youtubeCheckbox, chromeCheckbox, driveCheckbox, keepCheckbox, libriCheckbox;

    @FXML
    CheckBox payCheckbox, traduttoreCheckbox, documentiCheckbox, fogliCheckbox, presentazioniCheckbox;

    @FXML
    CheckBox filmCheckbox, musicaCheckbox, giochiCheckbox, newsCheckbox, duoCheckbox;

    @FXML
    Button selectAllButton;

    @FXML
    Button deselectAllButton;

    @FXML
    Button uninstallButton;

    private final MainController mainInstance = new MainController();

    @FXML
    public void selectAll(){
        googleCheckbox.setSelected(true);
        calendarCheckbox.setSelected(true);
        gmailCheckbox.setSelected(true);
        mapsCheckbox.setSelected(true);
        fotoCheckbox.setSelected(true);
        youtubeCheckbox.setSelected(true);
        chromeCheckbox.setSelected(true);
        driveCheckbox.setSelected(true);
        keepCheckbox.setSelected(true);
        libriCheckbox.setSelected(true);
        payCheckbox.setSelected(true);
        traduttoreCheckbox.setSelected(true);
        documentiCheckbox.setSelected(true);
        fogliCheckbox.setSelected(true);
        presentazioniCheckbox.setSelected(true);
        filmCheckbox.setSelected(true);
        musicaCheckbox.setSelected(true);
        giochiCheckbox.setSelected(true);
        newsCheckbox.setSelected(true);
        duoCheckbox.setSelected(true);
    }

    @FXML
    public void deselectAll(){
        googleCheckbox.setSelected(false);
        calendarCheckbox.setSelected(false);
        gmailCheckbox.setSelected(false);
        mapsCheckbox.setSelected(false);
        fotoCheckbox.setSelected(false);
        youtubeCheckbox.setSelected(false);
        chromeCheckbox.setSelected(false);
        driveCheckbox.setSelected(false);
        keepCheckbox.setSelected(false);
        libriCheckbox.setSelected(false);
        payCheckbox.setSelected(false);
        traduttoreCheckbox.setSelected(false);
        documentiCheckbox.setSelected(false);
        fogliCheckbox.setSelected(false);
        presentazioniCheckbox.setSelected(false);
        filmCheckbox.setSelected(false);
        musicaCheckbox.setSelected(false);
        giochiCheckbox.setSelected(false);
        newsCheckbox.setSelected(false);
        duoCheckbox.setSelected(false);
    }

    @FXML
    public void uninstallGapps(){
        boolean status=false;

        if (googleCheckbox.isSelected()) status = mainInstance.uninstallCustomAppSilent("com.google.android.googlequicksearchbox");
        if (calendarCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.calendar");
        if (gmailCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.gm");
        if (mapsCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.apps.maps");
        if (fotoCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.apps.photos");

        if(youtubeCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.youtube");
        if(chromeCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.android.chrome");
        if(driveCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.apps.docs");
        if(keepCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.keep");
        if(libriCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.apps.books");

        if(payCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.apps.walletnfcrel");
        if(traduttoreCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.apps.translate");
        if(documentiCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.apps.docs.editors.docs");
        if(fogliCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.apps.docs.editors.sheets");
        if(presentazioniCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.apps.docs.editors.slides");

        if(filmCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.videos");
        if(musicaCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.music");
        if(giochiCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.play.games");
        if(newsCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.apps.magazines");
        if(duoCheckbox.isSelected()) status |= mainInstance.uninstallCustomAppSilent("com.google.android.apps.tachyon");

        new Alert(Alert.AlertType.NONE,((status)? "Operazione completata con successo!":"Operazione fallita!"), ButtonType.OK).show();
        deselectAll();
    }
    
}
