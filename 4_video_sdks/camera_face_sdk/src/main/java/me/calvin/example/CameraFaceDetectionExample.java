package me.calvin.example;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import me.calvin.example.utils.FaceDetection;
import me.calvin.example.utils.OpenCVImageUtil;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * 本地摄像头人脸检测
 * Local camera face detection.
 *
 * @author Calvin
 */
public class CameraFaceDetectionExample {
  public static void main(String[] args) throws IOException, ModelException, TranslateException {
    faceDetection();
  }

  /**
   * 人脸检测
   * Face detection.
   *
   */
  public static void faceDetection()
      throws IOException, ModelException, TranslateException {
    float shrink = 0.5f;
    float threshold = 0.7f;

    Criteria<Image, DetectedObjects> criteria = new FaceDetection().criteria(shrink, threshold);

    // 开启摄像头，获取图像（得到的图像为frame类型，需要转换为mat类型进行检测和识别）
    // Open the camera, get the image (the obtained image is of frame type, which needs to be converted to mat type for detection and recognition)
    OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
    
    grabber.start();

    // Frame与Mat转换
    // Frame to Mat conversion
    OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

    CanvasFrame canvas = new CanvasFrame("人脸检测"); // 新建一个预览窗口
    canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    canvas.setVisible(true);
    canvas.setFocusable(true);
    // 窗口置顶
    // Window top
    if (canvas.isAlwaysOnTopSupported()) {
      canvas.setAlwaysOnTop(true);
    }
    Frame frame = null;

    try (ZooModel model = ModelZoo.loadModel(criteria);
        Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
      // 获取图像帧
      // Get image frame
      for (; canvas.isVisible() && (frame = grabber.grab()) != null; ) {

        // 将获取的frame转化成mat数据类型
        // Convert the obtained frame into mat data type
        Mat img = converter.convert(frame);
        BufferedImage buffImg = OpenCVImageUtil.mat2BufferedImage(img);

        Image image = ImageFactory.getInstance().fromImage(buffImg);
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        DetectedObjects detections = predictor.predict(image);
        List<DetectedObjects.DetectedObject> items = detections.items();

        // 遍历人脸
        // Traverse faces
        for (DetectedObjects.DetectedObject item : items) {
          BoundingBox box = item.getBoundingBox();
          Rectangle rectangle = box.getBounds();
          int x = (int) (rectangle.getX() * imageWidth);
          int y = (int) (rectangle.getY() * imageHeight);
          Rect face =
              new Rect(
                  x,
                  y,
                  (int) (rectangle.getWidth() * imageWidth),
                  (int) (rectangle.getHeight() * imageHeight));

          // 绘制人脸矩形区域，scalar色彩顺序：BGR(蓝绿红)
          // // Draw face rectangle area, scalar color order: BGR (blue green red)
          rectangle(img, face, new Scalar(0, 0, 255, 1));

          int pos_x = Math.max(face.tl().x() - 10, 0);
          int pos_y = Math.max(face.tl().y() - 10, 0);
          // 在人脸矩形上面绘制文字
          // Draw text above the face rectangle
          putText(
              img,
              "Face",
              new Point(pos_x, pos_y),
              FONT_HERSHEY_COMPLEX,
              1.0,
              new Scalar(0, 0, 255, 2.0));
        }

        // 显示视频图像
        // Display video image
        canvas.showImage(frame);
      }
    }

    canvas.dispose();
    grabber.close();
  }
}
