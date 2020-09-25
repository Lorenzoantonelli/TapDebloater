module org.lorenzoantonelli.tapdebloater {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.lorenzoantonelli.tapdebloater to javafx.fxml;
    exports org.lorenzoantonelli.tapdebloater;
}