package me.aias;

import ai.djl.ModelException;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Joints;
import ai.djl.translate.TranslateException;
import me.aias.util.ImageUtils;
import me.aias.util.PersonDetection;
import me.aias.util.PoseResnet18Estimation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Calvin
 *
 * @email 179209347@qq.com
 **/
public final class PoseResnet18EstimationExample {

  private static final Logger logger = LoggerFactory.getLogger(PoseResnet18EstimationExample.class);

  private PoseResnet18EstimationExample() {}

  public static void main(String[] args) throws IOException, ModelException, TranslateException {
    Path imageFile = Paths.get("src/test/resources/pose.jpeg");
    Image image = ImageFactory.getInstance().fromFile(imageFile);
    PersonDetection personDetection = new PersonDetection();
    DetectedObjects detections = personDetection.predict(image);
    List<DetectedObjects.DetectedObject> persons = detections.items();
    //    List<String> names = new ArrayList<>();
    //    List<Double> prob = new ArrayList<>();
    //    List<BoundingBox> rect = new ArrayList<>();
    int index = 0;

    for (DetectedObjects.DetectedObject person : persons) {
      // 外扩比例 factor = 1, 100%, factor = 0.2, 20%
      // Expand the border of the bounding box by factor=1, 100%; factor=0.2, 20%
      Image subImg = ImageUtils.getSubImage(image, person.getBoundingBox(), 0f);
      Joints joints = new PoseResnet18Estimation().predict(subImg);

      // 在抠出的小图中画出关键点
      // Draw the keypoints in the cropped image
      subImg.drawJoints(joints);
      ImageUtils.saveImage(subImg, "person_" + index++ + ".png", "build/output");
      logger.info("{}", joints);

      int x = ImageUtils.getX(image, person.getBoundingBox());
      int y = ImageUtils.getY(image, person.getBoundingBox());
      //  在大图中画出关键点
      // Draw the keypoints in the original image
      ImageUtils.drawJoints(image,subImg, x, y, joints);

      //      names.add(face.getClassName());
      //      prob.add(face.getProbability());
      //      rect.add(face.getBoundingBox());
    }

    ImageUtils.drawBoundingBoxImage(image, detections);
    ImageUtils.saveImage(image, "pose-resnet18-estimation.png", "build/output");

    logger.info("{}", detections);
  }


}
