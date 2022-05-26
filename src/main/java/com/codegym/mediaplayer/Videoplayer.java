package com.codegym.mediaplayer;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class Videoplayer implements Initializable {

    private MediaPlayer mediaPlayer;
    @FXML
    private MediaView mediaView;

    @FXML
    private Slider sliderTime;

    @FXML
    private Slider sliderVolume;

    @FXML
    private Button idslow;

    @FXML
    private Button isfast;

    @FXML
    private Button VolumeBtn;

    @FXML
    private Button previousbtn;

    @FXML
    private Button resetBtn;

    @FXML
    private Button nextbtn;

    @FXML
    private HBox hboxVolume;
    @FXML
    private HBox hBoxControls;
    @FXML
    private StackPane stackPane;


    @FXML
    private Label labelCurrentTime;
    @FXML
    private Label labelTotalTime;


    private boolean atEndOfVideo = false;
    private boolean isPlaying = true;
    private boolean isMuted = true;

    private ImageView ivVolume;
    private ImageView ivMute;
    private ImageView ivNext;
    private ImageView ivPrevious;
    int count = 0;

    List<Media> mediaList;

    public void starvideo(List<Media> mediaList) {
        Media mediaStart = mediaList.get(count);
        mediaPlayer = new MediaPlayer(mediaStart);

        mediaView.setMediaPlayer(mediaPlayer);
        DoubleProperty widthProp = mediaView.fitWidthProperty();
        DoubleProperty heightProp = mediaView.fitHeightProperty();

        widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                sliderTime.setValue(newValue.toSeconds());
            }
        });

        sliderTime.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(sliderTime.getValue()));
            }
        });

        sliderTime.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(sliderTime.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration total = mediaStart.getDuration();
                sliderTime.setMax(total.toSeconds());
            }
        });

        sliderVolume.setValue(mediaPlayer.getVolume() * 100);
        sliderVolume.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(sliderVolume.getValue() / 100);
            }
        });


        mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
                sliderTime.setMax(newDuration.toSeconds());
                labelTotalTime.setText(getTime(newDuration));
            }
        });

        mediaPlayer.play();
        bindCurrentTimeLabel();



    }

    public void chooseMultiFileMethod(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("mp4", "*.mp4"),
                new FileChooser.ExtensionFilter("mp3", "*.mp3"));
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        for (File file : files) {
            Media media = new Media(file.toURI().toString());
            mediaList.add(media);
        }
        starvideo(mediaList);
    }

    public void showList(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ListMedia.fxml"));
            Parent root = loader.load();

            ((ListMedia) loader.getController()).setModel(mediaList);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.getIcons().add(new Image("https://media-exp1.licdn.com/dms/image/C510BAQEdjKl11NB6-g/company-logo_200_200/0/1519959194921?e=2147483647&v=beta&t=aw701egxIiTMH5Bvt-yh_lpGyHkBPo5yrORm7DeQ840"));
            stage.setTitle("Media List!");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void previous() {
        mediaPlayer.stop();
        int size = count;
        for (int i = count; i < 0; i--) {
            size = i - 1;
        }
        Media mediaStart = mediaList.get(size);
        count = count - 1;
        mediaPlayer = new MediaPlayer(mediaStart);
        mediaView.setMediaPlayer(mediaPlayer);

        DoubleProperty widthProp = mediaView.fitWidthProperty();
        DoubleProperty heightProp = mediaView.fitHeightProperty();

        widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                sliderTime.setValue(newValue.toSeconds());
            }
        });

        sliderTime.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(sliderTime.getValue()));
            }
        });

        sliderTime.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(sliderTime.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration total = mediaStart.getDuration();
                sliderTime.setMax(total.toSeconds());
            }
        });

        sliderVolume.setValue(mediaPlayer.getVolume() * 100);
        sliderVolume.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(sliderVolume.getValue() / 100);
            }
        });
        mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
                sliderTime.setMax(newDuration.toSeconds());
                labelTotalTime.setText(getTime(newDuration));
            }
        });
        mediaPlayer.play();
        bindCurrentTimeLabel();
    }

    public void next() {
        mediaPlayer.stop();
        int size = 0;
        for (int i = 0; i <= count; i++) {
            size = i + 1;
        }
        Media mediaStart = mediaList.get(size);
        count = count + 1;
        mediaPlayer = new MediaPlayer(mediaStart);

        mediaView.setMediaPlayer(mediaPlayer);

        DoubleProperty widthProp = mediaView.fitWidthProperty();
        DoubleProperty heightProp = mediaView.fitHeightProperty();

        widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                sliderTime.setValue(newValue.toSeconds());
            }
        });

        sliderTime.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(sliderTime.getValue()));
            }
        });

        sliderTime.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(Duration.seconds(sliderTime.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration total = mediaStart.getDuration();
                sliderTime.setMax(total.toSeconds());
            }
        });

        sliderVolume.setValue(mediaPlayer.getVolume() * 100);
        sliderVolume.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(sliderVolume.getValue() / 100);
            }
        });
        mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
                sliderTime.setMax(newDuration.toSeconds());
                labelTotalTime.setText(getTime(newDuration));
            }
        });
        mediaPlayer.play();
        bindCurrentTimeLabel();
    }

    public void play(ActionEvent event) {
        mediaPlayer.play();
        mediaPlayer.setRate(1);
    }

    public void pause(ActionEvent event) {
        mediaPlayer.pause();
    }

    public void Reset(ActionEvent event) {
        mediaPlayer.stop();
    }

    public void slowRate(ActionEvent event) {
        mediaPlayer.setRate(0.5);

        idslow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    mediaPlayer.setRate(1);
                }
            }
        });
    }

    public void fastFoward(ActionEvent event) {
        mediaPlayer.setRate(2);

        isfast.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    mediaPlayer.setRate(1);
                }
            }
        });
    }

    public void skip15(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(15)));

    }

    public void back15(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-15)));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mediaList = new ArrayList<>();
        final int IV_SIZE = 20;

        Image imageVol = new Image(new File("src/resource/volume.png").toURI().toString());
        ivVolume = new ImageView(imageVol);
        ivVolume.setFitHeight(IV_SIZE);
        ivVolume.setFitWidth(IV_SIZE);

        Image imageMute = new Image(new File("src/resource/muteVolume.png").toURI().toString());
        ivMute = new ImageView(imageMute);
        ivMute.setFitHeight(IV_SIZE);
        ivMute.setFitWidth(IV_SIZE);

        Image imageNext = new Image(new File("src/resource/next.png").toURI().toString());
        ivNext = new ImageView(imageNext);
        ivNext.setFitHeight(IV_SIZE);
        ivNext.setFitWidth(IV_SIZE);

        Image imagePrevious = new Image(new File("src/resource/previous.png").toURI().toString());
        ivPrevious = new ImageView(imagePrevious);
        ivPrevious.setFitHeight(IV_SIZE);
        ivPrevious.setFitWidth(IV_SIZE);

        previousbtn.setGraphic(ivPrevious);
        nextbtn.setGraphic(ivNext);
        VolumeBtn.setGraphic(ivVolume);

        sliderVolume.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(sliderVolume.getValue());
                if (mediaPlayer.getVolume() != 0.0) {
                    VolumeBtn.setGraphic(ivVolume);
                    isMuted = false;
                } else {
                    VolumeBtn.setGraphic(ivMute);
                    isMuted = true;
                }
            }
        });


        VolumeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (isMuted) {
                    VolumeBtn.setGraphic(ivVolume);
                    sliderVolume.setValue(0.2);
                    isMuted = false;
                } else {
                    VolumeBtn.setGraphic(ivMute);
                    sliderVolume.setValue(0);
                    isMuted = true;
                }
            }
        });
        VolumeBtn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (hboxVolume.lookup("sliderVolume") == null) {
                    hboxVolume.getChildren().add(sliderVolume);
                    sliderVolume.setValue(mediaPlayer.getVolume());
                }
            }
        });
        stackPane.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observableValue, Scene oldScene, Scene newScene) {
                if (oldScene == null && newScene != null) {
                    mediaView.fitHeightProperty().bind(newScene.heightProperty().subtract(hBoxControls.heightProperty().add(20)));
                }
            }
        });

    }

    private void bindCurrentTimeLabel() {
        labelCurrentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getTime(mediaPlayer.getCurrentTime()) + " /";
            }
        }, mediaPlayer.currentTimeProperty()));
    }

    public String getTime(Duration time) {
        int hours = (int) time.toHours();
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        if (seconds > 59) seconds = seconds % 60;
        if (minutes > 59) minutes = minutes % 60;
        if (hours > 59) hours = hours % 60;

        if (hours > 0) return String.format("%d:%02d:%02d",
                hours,
                minutes,
                seconds);
        else return String.format("%02d:%02d",
                minutes,
                seconds);
    }
}
