package me.aias;

import ai.djl.ModelException;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.translate.TranslateException;
import me.aias.util.ImageUtils;
import me.aias.util.SuperResolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
/**
 * 超分辨- 提升分辨率
 * Super Resolution - Enhance Resolution
 *
 * @author Calvin
 *
 * @email 179209347@qq.com
 **/
public final class SuperResolutionExample {

  private static final Logger logger = LoggerFactory.getLogger(SuperResolutionExample.class);

  private SuperResolutionExample() {}

  public static void main(String[] args) throws IOException, ModelException, TranslateException {
    String imagePath = "src/test/resources/";
    Path imageFile = Paths.get(imagePath + "srgan.png");
    
    Image image = ImageFactory.getInstance().fromFile(imageFile);
    SuperResolution enhancer = new SuperResolution();

    // Super Resolution - Enhance Resolution
    Image img = enhancer.predict(image);
    ImageUtils.saveImage(img, "single.png", "build/output");

    logger.info("Images generated: {}", enhancedImages.size());
    ImageUtils.saveImages(inputImages, enhancedImages, "build/output/");

  }
}
