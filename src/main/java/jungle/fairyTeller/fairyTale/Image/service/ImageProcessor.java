package jungle.fairyTeller.fairyTale.Image.service;

import lombok.AllArgsConstructor;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

public class ImageProcessor {
    static {
        // Load OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public ImageProcessor(){
    }

    public String removeBackgroundAndReturnBase64(String base64Img) {

        // Decode base64 image to bytes
        byte[] decodedBytes = Base64.getDecoder().decode(base64Img);

        // Create a Mat from the decoded bytes
        MatOfByte matOfByte = new MatOfByte(decodedBytes);
        Mat image = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_COLOR);

        // Perform background removal (Same code as before)
        Mat mask = new Mat();
        Mat bgModel = new Mat();
        Mat fgModel = new Mat();
        Rect rect = new Rect(50, 50, image.cols() - 100, image.rows() - 100);
        Imgproc.grabCut(image, mask, rect, bgModel, fgModel, 5, Imgproc.GC_INIT_WITH_RECT);
        Core.compare(mask, new Scalar(Imgproc.GC_PR_FGD), mask, Core.CMP_EQ);
        Mat foreground = new Mat(image.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        image.copyTo(foreground, mask);

        // Encode the result image to base64
        MatOfByte matOfByteResult = new MatOfByte();
        Imgcodecs.imencode(".jpg", foreground, matOfByteResult);
        byte[] byteArray = matOfByteResult.toArray();
        String base64Result = Base64.getEncoder().encodeToString(byteArray);

        return base64Result;
    }
}
