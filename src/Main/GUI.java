
package Main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class GUI extends Application {
    private ImageView imageView;
    private VideoCaptureHandler videoCaptureHandler;
    private ObjectDetection objectDetection;
    private MotionTracking motionTracking;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        videoCaptureHandler = new VideoCaptureHandler(0);
        objectDetection = new ObjectDetection("C:\\Users\\Rucha\\eclipse-workspace\\RealTimeVideoAnalysis\\resources\\haarcascade_frontalface_default.xml");
        motionTracking = new MotionTracking();

        imageView = new ImageView();
        StackPane root = new StackPane();
        root.getChildren().add(imageView);

        Scene scene = new Scene(root, 640, 480);
        primaryStage.setTitle("Real-Time Video Stream Analysis");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(this::startVideoCapture).start();
    }

    private void startVideoCapture() {
        WritableImage writableImage = new WritableImage(640, 480);
        imageView.setImage(writableImage);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        
        Mat processedImage = new Mat();
        
        while (true) {
            Mat frame = videoCaptureHandler.getFrame();
            if (frame != null) {
                objectDetection.detectAndDraw(frame);
                motionTracking.trackMotion(frame);
                updateImageView(frame, pixelWriter);
            }

        // Release the Mat resources
        frame.release();
        processedImage.release();

        if (!imageView.getScene().getWindow().isShowing()) {
            break;
        }
    }

    // Clean up
    videoCaptureHandler.releaseCamera();
}


    private void updateImageView(Mat frame, PixelWriter pixelWriter) {
        Mat processedImage = new Mat();
        Imgproc.cvtColor(frame, processedImage, Imgproc.COLOR_BGR2BGRA);
        byte[] buffer = new byte[(int) (processedImage.total() * processedImage.channels())];
        processedImage.get(0, 0, buffer);

        Platform.runLater(() -> {
            for (int y = 0; y < processedImage.rows(); y++) {
                for (int x = 0; x < processedImage.cols(); x++) {
                    int index = y * processedImage.cols() * processedImage.channels() + x * processedImage.channels();
                    int r = buffer[index] & 0xFF;
                    int g = buffer[index + 1] & 0xFF;
                    int b = buffer[index + 2] & 0xFF;
                    int a = buffer[index + 3] & 0xFF;
                    pixelWriter.setArgb(x, y, (a << 24) | (r << 16) | (g << 8) | b);
                }
            }
        });
    }

    @Override
    public void stop() {
        videoCaptureHandler.releaseCamera();
    }
}