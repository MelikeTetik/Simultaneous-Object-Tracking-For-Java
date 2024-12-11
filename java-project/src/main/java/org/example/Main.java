package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class Main extends Application {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // OpenCV'yi yükle
    }

    private Thread thread1, thread2, thread3, thread4;
    private VideoTracker tracker1, tracker2, tracker3, tracker4;

    @Override
    public void start(Stage primaryStage) {
        // Dört video için ImageView oluştur
        ImageView videoView1 = new ImageView();
        ImageView videoView2 = new ImageView();
        ImageView videoView3 = new ImageView();
        ImageView videoView4 = new ImageView();

        // Görüntü boyutlarını ayarla
        videoView1.setFitWidth(300);
        videoView1.setFitHeight(300);
        videoView2.setFitWidth(300);
        videoView2.setFitHeight(300);
        videoView3.setFitWidth(300);
        videoView3.setFitHeight(300);
        videoView4.setFitWidth(300);
        videoView4.setFitHeight(300);

        // Düğmeler oluştur
        Button startButton1 = new Button("Start 1");
        Button stopButton1 = new Button("Stop 1");
        Button startButton2 = new Button("Start 2");
        Button stopButton2 = new Button("Stop 2");
        Button startButton3 = new Button("Start 3");
        Button stopButton3 = new Button("Stop 3");
        Button startButton4 = new Button("Start 4");
        Button stopButton4 = new Button("Stop 4");
        Button enlargeButton = new Button("Enlarge");

        // CSS stilleri
        startButton1.setStyle("-fx-background-color:#228b22 ; -fx-text-fill: white;");
        stopButton1.setStyle("-fx-background-color: #8b0000; -fx-text-fill: white;");
        startButton2.setStyle("-fx-background-color: #228b22; -fx-text-fill: white;");
        stopButton2.setStyle("-fx-background-color: #8b0000; -fx-text-fill: white;");
        startButton3.setStyle("-fx-background-color: #228b22; -fx-text-fill: white;");
        stopButton3.setStyle("-fx-background-color: #8b0000; -fx-text-fill: white;");
        startButton4.setStyle("-fx-background-color: #228b22; -fx-text-fill: white;");
        stopButton4.setStyle("-fx-background-color: #8b0000; -fx-text-fill: white;");
        enlargeButton.setStyle("-fx-background-color: #002633; -fx-text-fill: white;");


        // GridPane düzeni kullanarak videoları yerleştir
        GridPane grid = new GridPane();
        grid.add(videoView1, 0, 0); // Sol üst
        grid.add(videoView2, 1, 0); // Sağ üst
        grid.add(videoView3, 0, 1); // Sol alt
        grid.add(videoView4, 1, 1); // Sağ alt
        grid.add(startButton1, 0, 2);
        grid.add(stopButton1, 0, 3);
        grid.add(startButton2, 1, 2);
        grid.add(stopButton2, 1, 3);
        grid.add(startButton3, 0, 4);
        grid.add(stopButton3, 0, 5);
        grid.add(startButton4, 1, 4);
        grid.add(stopButton4, 1, 5);
        grid.add(enlargeButton, 0, 6, 2, 1);

        // GridPane'in arka planını ayarla
        grid.setBackground(new Background(new BackgroundFill(Color.DARKCYAN, CornerRadii.EMPTY, Insets.EMPTY)));

        // Scene ayarları
        Scene scene = new Scene(grid, 800, 800); // Pencere boyutu
        primaryStage.setTitle("Dört Video Gösterimi");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Video kaynaklarını başlat ve görüntüle
        String videoPath1 = "src/main/java/org/example/video2.mp4";
        String videoPath2 = "src/main/java/org/example/video1.mp4";
        String videoPath3 = "src/main/java/org/example/video4.mp4"; // Yeni video
        String videoPath4 = "src/main/java/org/example/video5.mp4"; // Yeni video

        // Video tracker nesneleri oluştur
        tracker1 = new VideoTracker(videoPath1, videoView1);
        tracker2 = new VideoTracker(videoPath2, videoView2);
        tracker3 = new VideoTracker(videoPath3, videoView3);
        tracker4 = new VideoTracker(videoPath4, videoView4);

        // Başlat/Durdur butonları için işlevsellik
        startButton1.setOnAction(event -> {
            if (tracker1 == null || thread1 == null || !thread1.isAlive()) {
                tracker1 = new VideoTracker(videoPath1, videoView1); // Yeni bir tracker oluştur
                thread1 = new Thread(tracker1);
                thread1.start();
            }
        });

// Aynı şekilde diğer start butonları için de güncelleme yapın

        stopButton1.setOnAction(event -> {
            tracker1.stop();  // Videoyu durdur
            if (thread1 != null) {
                thread1.interrupt();  // Thread'i kes
                thread1 = null;  // Thread'i null yap
            }
        });

        startButton2.setOnAction(event -> {
            if (tracker2 == null || thread2 == null || !thread2.isAlive()) {
                tracker2 = new VideoTracker(videoPath2, videoView2); // Yeni bir tracker oluştur
                thread2 = new Thread(tracker2);
                thread2.start();
            }
        });

// Aynı şekilde diğer start butonları için de güncelleme yapın

        stopButton2.setOnAction(event -> {
            tracker2.stop();
            if (thread2 != null) {
                thread2.interrupt();
                thread2 = null;
            }
        });

        startButton3.setOnAction(event -> {
            if (tracker3 == null || thread3 == null || !thread3.isAlive()) {
                tracker3 = new VideoTracker(videoPath3, videoView3); // Yeni bir tracker oluştur
                thread3 = new Thread(tracker3);
                thread3.start();
            }
        });

// Aynı şekilde diğer start butonları için de güncelleme yapın

        stopButton3.setOnAction(event -> {
            tracker3.stop();
            if (thread3 != null) {
                thread3.interrupt();
                thread3 = null;
            }
        });

        startButton4.setOnAction(event -> {
            if (tracker4 == null || thread4 == null || !thread4.isAlive()) {
                tracker4 = new VideoTracker(videoPath4, videoView4); // Yeni bir tracker oluştur
                thread4 = new Thread(tracker4);
                thread4.start();
            }
        });

// Aynı şekilde diğer start butonları için de güncelleme yapın

        stopButton4.setOnAction(event -> {
            tracker4.stop();
            if (thread4 != null) {
                thread4.interrupt();
                thread4 = null;
            }
        });

        // Video boyutlarını büyütme butonu
        enlargeButton.setOnAction(event -> {
            double scaleFactor = 1.1;
            videoView1.setFitWidth(videoView1.getFitWidth() * scaleFactor);
            videoView1.setFitHeight(videoView1.getFitHeight() * scaleFactor);
            videoView2.setFitWidth(videoView2.getFitWidth() * scaleFactor);
            videoView2.setFitHeight(videoView2.getFitHeight() * scaleFactor);
            videoView3.setFitWidth(videoView3.getFitWidth() * scaleFactor);
            videoView3.setFitHeight(videoView3.getFitHeight() * scaleFactor);
            videoView4.setFitWidth(videoView4.getFitWidth() * scaleFactor);
            videoView4.setFitHeight(videoView4.getFitHeight() * scaleFactor);
        });
    }

    public static void main(String[] args) {
        launch(args); // JavaFX uygulamasını başlat
    }
}
