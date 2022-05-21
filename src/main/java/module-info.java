module com.codegym.mediaplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;

    opens com.codegym.mediaplayer to javafx.fxml;
    exports com.codegym.mediaplayer;
}