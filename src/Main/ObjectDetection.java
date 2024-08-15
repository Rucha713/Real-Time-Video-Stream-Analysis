package Main;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class ObjectDetection {
    private CascadeClassifier faceDetector;

    public ObjectDetection(String cascadePath) {
        faceDetector = new CascadeClassifier(cascadePath);
        if (faceDetector.empty()) {
            System.out.println("Error loading cascade file");
        }
    }

    public void detectAndDraw(Mat frame) {
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(frame, faceDetections);

        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }
    }
}