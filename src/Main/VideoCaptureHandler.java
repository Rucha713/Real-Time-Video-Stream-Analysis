
package Main;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class VideoCaptureHandler {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private VideoCapture camera;

    public VideoCaptureHandler(int cameraIndex) {
        camera = new VideoCapture(cameraIndex);  // Default camera
        if (!camera.isOpened()) {
            System.out.println("Error: Camera not available!");
        }
    }

    public Mat getFrame() {
        Mat frame = new Mat();
        if (camera.read(frame)) {
            return frame;
        }
        return null;
    }

    public void releaseCamera() {
        camera.release();
    }
}


