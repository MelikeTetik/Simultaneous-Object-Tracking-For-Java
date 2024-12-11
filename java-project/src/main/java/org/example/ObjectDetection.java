
package org.example;

import org.opencv.core.*;
import org.opencv.dnn.*;
import org.opencv.imgproc.Imgproc;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectDetection {

    // Nesne bilgilerini tutmak için bir sınıf
    static class DetectedObject {
        Rect boundingBox;
        String label;
        double confidence;

        DetectedObject(Rect boundingBox, String label, double confidence) {
            this.boundingBox = boundingBox;
            this.label = label;
            this.confidence = confidence;
        }
    }

    // İzlenen nesneleri tutmak için bir harita
    private static Map<Integer, DetectedObject> trackedObjects = new HashMap<>();
    private static int objectIdCounter = 0;
    private static boolean alarmPlayed = false; // Alarmın çalıp çalmadığını

    public static void detectObjects(Mat frame, Net net, List<String> classNames) {
        Mat blob = Dnn.blobFromImage(frame, 0.00392, new Size(416, 416), new Scalar(0, 0, 0), true, false);
        net.setInput(blob);
        List<Mat> result = new ArrayList<>();
        List<String> outputLayerNames = net.getUnconnectedOutLayersNames();
        net.forward(result, outputLayerNames);

        double confidenceThreshold = 0.5;

        // Sınıf renkleri için bir map
        Map<String, Scalar> classColors = new HashMap<>();
        classColors.put("car", new Scalar(0, 0, 255));   // Kırmızı
        classColors.put("person", new Scalar(255, 0, 0)); // Mavi
        classColors.put("dog", new Scalar(0, 255, 0));   // Yeşil
        classColors.put("cat", new Scalar(255, 255, 0)); // Sarı

        List<DetectedObject> currentDetectedObjects = new ArrayList<>();

        for (Mat mat : result) {
            if (mat.type() != CvType.CV_32F) {
                mat.convertTo(mat, CvType.CV_32F);
            }

            for (int i = 0; i < mat.rows(); i++) {
                float[] data = new float[mat.cols()];
                mat.row(i).get(0, 0, data);

                float confidence = data[4];

                if (confidence > confidenceThreshold) {
                    int centerX = (int) (data[0] * frame.cols());
                    int centerY = (int) (data[1] * frame.rows());
                    int width = (int) (data[2] * frame.cols());
                    int height = (int) (data[3] * frame.rows());

                    // Sınıf ID'sini al
                    float maxConfidence = 0;
                    int classId = -1;
                    for (int j = 5; j < data.length; j++) {
                        if (data[j] > maxConfidence) {
                            maxConfidence = data[j];
                            classId = j - 5;
                        }
                    }

                    if (classId >= 0 && classId < classNames.size()) {
                        String label = classNames.get(classId);  // Nesne adı
                        Rect boundingBox = new Rect(centerX - width / 2, centerY - height / 2, width, height);
                        currentDetectedObjects.add(new DetectedObject(boundingBox, label, confidence));

                        // Sınıfa göre renk seçimi
                        Scalar color = classColors.getOrDefault(label, new Scalar(0, 255, 255)); // Sarı renk varsayılan

                        // Nesneye çerçeve çizme
                        Imgproc.rectangle(frame, boundingBox.tl(), boundingBox.br(), color, 2);
                        Imgproc.putText(frame, label + ": " + String.format("%.2f", confidence),
                                new Point(centerX - width / 2, centerY - height / 2 - 10),
                                Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, color, 1);

                        // Araba algılandığında alarm sesi çal
                        if (label.equals("umbrella")) {
                            playAlarmSound();
                        } else {
                            resetAlarm();
                        }
                    }
                }
            }
        }

        // İzlenen nesneleri güncelle
        updateTrackedObjects(currentDetectedObjects);
    }

    private static void updateTrackedObjects(List<DetectedObject> currentDetectedObjects) {
        // Önce mevcut nesneleri kontrol et
        for (DetectedObject detectedObject : currentDetectedObjects) {
            boolean isNewObject = true;

            // Mevcut nesneleri kontrol et
            for (Map.Entry<Integer, DetectedObject> entry : trackedObjects.entrySet()) {
                DetectedObject trackedObject = entry.getValue();
                if (isOverlap(trackedObject.boundingBox, detectedObject.boundingBox)) {
                    // Nesne zaten izleniyor, güncelle
                    trackedObject.boundingBox = detectedObject.boundingBox;
                    trackedObject.confidence = detectedObject.confidence;
                    isNewObject = false;
                    break;
                }
            }

            // Yeni nesne ekle
            if (isNewObject) {
                trackedObjects.put(objectIdCounter++, detectedObject);
            }
        }

        // İzlenmeyen nesneleri temizle
        List<Integer> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, DetectedObject> entry : trackedObjects.entrySet()) {
            DetectedObject trackedObject = entry.getValue();
            boolean found = false;

            for (DetectedObject current : currentDetectedObjects) {
                if (isOverlap(trackedObject.boundingBox, current.boundingBox)) {
                    found = true;
                    break;
                }
            }

            // Eğer izlenmeyen bir nesne varsa, onu sil
            if (!found) {
                toRemove.add(entry.getKey());
            }
        }

        for (Integer id : toRemove) {
            trackedObjects.remove(id);
        }
    }

    private static boolean isOverlap(Rect rect1, Rect rect2) {
        return rect1.x < rect2.x + rect2.width &&
                rect1.x + rect1.width > rect2.x &&
                rect1.y < rect2.y + rect2.height &&
                rect1.y + rect1.height > rect2.y;
    }

    private static void playAlarmSound() {
        if (!alarmPlayed) { // Eğer alarm henüz çalmadıysa
            try {
                File soundFile = new File("src/main/java/org/example/alarm.wav"); // Ses dosyasının yolu
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                alarmPlayed = true; // Alarmın çaldığını işaretle

                // Sesin sürekli çalmasını sağlamak için
                clip.loop(Clip.LOOP_CONTINUOUSLY); // Sürekli döngüde çal

                // Alarm sesinin belirli bir süre sonra durdurulması için bir yeni bir Thread oluşturabiliriz
                new Thread(() -> {
                    try {
                        Thread.sleep(2000); // 2 saniye bekle
                        clip.stop(); // Alarm sesini durdur
                        clip.close(); // Kaynağı serbest bırak
                        alarmPlayed = false; // Alarm durumunu sıfırla
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start(); // Thread'i başlat

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void resetAlarm() {
        alarmPlayed = false; // Alarm çalma durumunu sıfırla
}
}
