module etu.ensicaen.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires etu.ensicaen.shared;

    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;

    opens etu.ensicaen.client to javafx.fxml;
    exports etu.ensicaen.client;

    exports etu.ensicaen.client.views;
    opens etu.ensicaen.client.views to javafx.fxml;

}