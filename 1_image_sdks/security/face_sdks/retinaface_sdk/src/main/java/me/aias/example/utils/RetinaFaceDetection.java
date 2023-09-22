package me.aias.example.utils;

import ai.djl.Device;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.training.util.ProgressBar;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class RetinaFaceDetection {

  public RetinaFaceDetection() {}

  public Criteria<Image, DetectedObjects> criteria(int topK, double confThresh, double nmsThresh) {
    double[] variance = {0.1f, 0.2f};
    int[][] scales = {{16, 32}, {64, 128}, {256, 512}};
    int[] steps = {8, 16, 32};
    FaceDetectionTranslator translator =
        new FaceDetectionTranslator(confThresh, nmsThresh, variance, topK, scales, steps);

    Criteria<Image, DetectedObjects> criteria =
        Criteria.builder()
            .setTypes(Image.class, DetectedObjects.class)
            .optModelPath(Paths.get("models/retinaface.zip"))
            .optModelName("retinaface") // specify model file prefix
            .optTranslator(translator)
            .optProgress(new ProgressBar())
            .optEngine("PyTorch") // Use PyTorch engine
                .optDevice(Device.cpu())
            .build();

    return criteria;
  }
}
