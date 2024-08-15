package Main;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import java.util.List;

public class MotionTracking {
    private Mat prevFrame = new Mat();
    private MatOfPoint2f prevFeatures = new MatOfPoint2f();

    public void trackMotion(Mat currentFrame) {
        // Convert the current frame to grayscale
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(currentFrame, grayFrame, Imgproc.COLOR_BGR2GRAY);

        // If prevFrame is empty, initialize it and find initial features
        if (prevFrame.empty()) {
            grayFrame.copyTo(prevFrame);

            // Use goodFeaturesToTrack to find corners or features in the initial frame
            MatOfPoint features = new MatOfPoint();
            Imgproc.goodFeaturesToTrack(prevFrame, features, 100, 0.01, 10);

            prevFeatures.fromList(features.toList());
            return;
        }

        MatOfPoint2f currFeatures = new MatOfPoint2f();
        MatOfByte status = new MatOfByte(); // Updated to MatOfByte
        MatOfFloat err = new MatOfFloat();  // Updated to MatOfFloat

        // Calculate optical flow between previous frame and current frame
        Video.calcOpticalFlowPyrLK(prevFrame, grayFrame, prevFeatures, currFeatures, status, err);

        // Draw circles at the current feature points
        List<Point> currPoints = currFeatures.toList();
        byte[] statusArray = status.toArray();
        for (int i = 0; i < currPoints.size(); i++) {
            if (statusArray[i] == 1) { // Check if the feature point is valid
                Point p = currPoints.get(i);
                Imgproc.circle(currentFrame, p, 5, new Scalar(0, 0, 255), 2);
            }
        }

        // Update previous frame and features
        prevFrame.release();
        prevFrame = grayFrame.clone();
        prevFeatures.release();
        prevFeatures.fromList(currFeatures.toList());
    }
}
