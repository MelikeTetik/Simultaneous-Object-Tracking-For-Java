package org.example;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

public class YoloDetector {
    public static void detect(Mat frame) {
        // YOLO modelini yükleme
        String modelConfiguration = "src/main/java/org/example/yolov4.cfg";  // YOLO yapılandırma dosyası
        String modelWeights = "src/main/java/org/example/yolov4.weights";     // YOLO ağırlık dosyası

        // YOLO ağını yükleyelim
        Net net = Dnn.readNetFromDarknet(modelConfiguration, modelWeights);

        // Resmi ön işleme
        Mat blob = Dnn.blobFromImage(frame, 1.0, new Size(416, 416), new org.opencv.core.Scalar(0, 0, 0), true, false);

        // Resmi modele gönderme
        net.setInput(blob);

        // Çıktıyı al
        Mat output = net.forward();

        // Çıktıları işleme (tespit edilen nesneleri ekrana yazma vb.)
        // Burada çıktı üzerinde işlem yaparak nesne tespiti yapılır.
    }
}
