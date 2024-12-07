package org.example;

import org.opencv.core.Core;

public class Main {
    public static void main(String[] args) {
        // OpenCV'yi yükleyelim
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Video kaynakları
        String video1 = "video1.mp4";
        String video2 = "video2.mp4";

        // Video kaynağına göre YoloDetection işlemleri başlatıyoruz
        VideoTracker tracker1 = new VideoTracker(video1);
        VideoTracker tracker2 = new VideoTracker(video2);

        // Her bir video kaynağını paralel olarak işlemek için thread oluşturuyoruz
        Thread thread1 = new Thread(tracker1);
        Thread thread2 = new Thread(tracker2);

        thread1.start();
        thread2.start();
    }
}








//package org.example;
////
//import org.opencv.core.*;
//import org.opencv.dnn.*;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.videoio.VideoCapture;
//import org.opencv.core.Core;
//import org.opencv.highgui.HighGui;
//import org.opencv.core.Point;
//import org.opencv.core.Scalar;
//
//public class Main {
//    public static void main(String[] args) {
//        // OpenCV kütüphanesini yükleyin
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        // YOLO modelini ve ağırlıklarını yükle
//        String modelConfiguration = "src/main/java/org/example/yolov4.cfg"; // Yolov4 config dosyanızın yolu
//        String modelWeights = "src/main/java/org/example/yolov4.weights";    // Yolov4 weights dosyanızın yolu
//
//        // Webcam için video kaynağını aç (0, genellikle ana kamerayı temsil eder)
//        VideoCapture cap = new VideoCapture(0); // 0, bilgisayarın ana webcam'ini temsil eder
//        if (!cap.isOpened()) {
//            System.out.println("Error opening video capture.");
//            return;
//        }
//
//        // YOLO DNN modeli
//        Net net = Dnn.readNetFromDarknet(modelConfiguration, modelWeights);
//
//        // Webcam üzerinde tespit yapmak için sürekli video akışını okuyun
//        Mat frame = new Mat();
//        while (cap.read(frame)) {
//            // Video frame'ini BGR'den RGB'ye dönüştürme
//            Mat blob = Dnn.blobFromImage(frame, 1 / 255.0, new Size(416, 416), new Scalar(0), true, false);
//
//            // Tespiti yapmak için ağı yönlendirme
//            net.setInput(blob);
//            Mat detections = net.forward();
//
//            // Tespit edilen nesneleri işleme
//            for (int i = 0; i < detections.rows(); i++) {
//                // Detected object confidence
//                double confidence = detections.get(i, 4)[0];
//
//                // Sadece yüksek güvenli nesneleri işleme
//                if (confidence > 0.5) {
//                    // Nesnenin konumunu alma
//                    int x1 = (int) (detections.get(i, 0)[0] * frame.cols());
//                    int y1 = (int) (detections.get(i, 1)[0] * frame.rows());
//                    int x2 = (int) (detections.get(i, 2)[0] * frame.cols());
//                    int y2 = (int) (detections.get(i, 3)[0] * frame.rows());
//
//                    // Nesne tespiti için dikdörtgen çizme
//                    Imgproc.rectangle(frame, new Point(x1, y1), new Point(x2, y2), new Scalar(0, 255, 0), 2);
//                }
//            }
//
//            // Frame'i ekranda gösterme
//            Imgproc.putText(frame, "Detection", new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 2);
//
//            // Webcam penceresini gösterme
//            HighGui.imshow("YOLO Detection - Webcam", frame);
//
//            // 'q' tuşuna basıldığında çıkma
//            if (HighGui.waitKey(1) == 'q') {
//                break;
//            }
//        }
//
//        // Kaynakları serbest bırakma
//        cap.release();
//        HighGui.destroyAllWindows();
//    }
//}


