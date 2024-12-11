package org.example;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class VideoTracker implements Runnable {
    private final String videoSource;
    private final ImageView imageView;
    private final Net net;
    private final List<String> classNames;
    private volatile boolean running = true; // Thread durumunu kontrol etmek için
    private Thread thread;

    private MediaPlayer mediaPlayer;
    private double currentTime = 0;

    public VideoTracker(String videoSource, ImageView imageView) {
        this.videoSource = videoSource;
        this.imageView = imageView;

        // YOLOv4 ağı yükleme
        net = Dnn.readNetFromDarknet("src/main/java/org/example/yolov4.cfg", "src/main/java/org/example/yolov4.weights");
        net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
        net.setPreferableTarget(Dnn.DNN_TARGET_CPU);

        // coco.names dosyasını yükle
        classNames = loadClassNames("src/main/java/org/example/coco.names");
    }

    @Override
    public void run() {
        VideoCapture capture = new VideoCapture(videoSource);
        if (!capture.isOpened()) {
            System.out.println("Video açılamadı: " + videoSource);
            return;
        }

        Mat frame = new Mat();
        try {
            while (running && capture.read(frame)) {
                // Renk formatını BGR'den RGB'ye çevir
                Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGB);

                // Nesne tespiti
                ObjectDetection.detectObjects(frame, net, classNames);

                // JavaFX Image formatına dönüştür
                Image image = matToImage(frame);

                // ImageView'e görüntüyü yükle
                Platform.runLater(() -> imageView.setImage(image));

                try {
                    Thread.sleep(33); // ~30 FPS
                } catch (InterruptedException e) {
                    // Thread.interrupt() çağrıldığında bu blok çalışacak
                    Thread.currentThread().interrupt(); // Interrupt durumunu tekrar ayarla
                    break; // Döngüyü sonlandır
                }
            }
        } finally {
            capture.release();
        }
    }

    public void stop() {
        running = false; // Döngüyü sonlandırmak için running değerini false yapıyoruz
        if (thread != null) {
            thread.interrupt(); // İş parçacığını uyandır ve interrupt durumuna getir
            try {
                thread.join(); // İş parçacığını durdurana kadar bekle
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = null; // thread'i null yaparak tekrar başlatmadan önce kontrol edebiliriz
        }
    }

    private List<String> loadClassNames(String path) {
        List<String> names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                names.add(line);
            }
        } catch (IOException e) {
            System.err.println("Sınıf isimleri yüklenirken hata oluştu: " + e.getMessage());
        }
        return names;
    }

    private Image matToImage(Mat frame) {
        // Mat nesnesini JavaFX Image nesnesine çevirme
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", frame, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        return new Image(new ByteArrayInputStream(byteArray));
    }


}
