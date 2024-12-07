package org.example;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class VideoTracker implements Runnable {
    private String videoSource;

    public VideoTracker(String videoSource) {
        this.videoSource = videoSource;
    }

    @Override
    public void run() {
        // Video kaynağını açıyoruz
        VideoCapture capture = new VideoCapture(videoSource);
        if (!capture.isOpened()) {
            System.out.println("Video kaynağı açılamadı: " + videoSource);
            return;
        }

        System.out.println("Video kaynağı başarıyla açıldı: " + videoSource);

        // Video karesi üzerinde işlem yapma (nesne tespiti gibi) kodları burada olacak
        Mat frame = new Mat();
        while (capture.read(frame)) {
            // Her bir video karesi üzerinde OpenCV ile işlem yapılabilir
            System.out.println("Video kaynağı işleniyor: " + videoSource);

            // Burada nesne tespiti (YOLO vb.) algoritması çalıştırılabilir.
            YoloDetector.detect(frame);
        }

        capture.release();  // Video kaynağını serbest bırak
    }
}
