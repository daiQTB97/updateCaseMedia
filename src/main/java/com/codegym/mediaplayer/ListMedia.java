package com.codegym.mediaplayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;

public class ListMedia implements Initializable {
    @FXML
    private ListView<Media> idListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void setModel(List<Media> mediaList) {

        ObservableList<Media> listMedia = FXCollections.observableArrayList(mediaList);
        idListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        idListView.getSelectionModel().selectIndices(1, 2);
        idListView.getFocusModel().focus(1);
        idListView.getItems().addAll(listMedia);
        idListView.setCellFactory(param -> new ListCell<Media>() {
            @Override
            protected void updateItem(Media item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    File file = new File(item.getSource());
                    setText(URLDecoder.decode(file.getName(), StandardCharsets.UTF_8));
                }
            }
        });
        idListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2) {
                    //Use ListView's getSelected Item
                    Media media = idListView.getSelectionModel()
                            .getSelectedItem();

                    System.out.println(media.getSource());
                    //use this to do whatever you want to. Open Link etc.
                }
            }
        });
    }
}
