package me.aias.example;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import me.aias.example.utils.FeatureComparison;
import me.aias.example.utils.ImageEncoder;
import me.aias.example.utils.TextEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Image & Text search【40 Languages】
 *
 * @author calvin
 * @mail 179209347@qq.com
 */
public final class ImageTextSearchExample {

  private static final Logger logger = LoggerFactory.getLogger(ImageTextSearchExample.class);

  private ImageTextSearchExample() {}

  public static void main(String[] args) throws IOException, ModelException, TranslateException {

    List<String> texts = new ArrayList<>();
    texts.add("There are two dogs in the snow.\n");
    texts.add("A cat on the table");
    texts.add("London at night");

    logger.info("texts: {}", Arrays.toString(texts.toArray()));

    Path imageFile = Paths.get("src/test/resources/two_dogs_in_snow.jpg");
    Image image = ImageFactory.getInstance().fromFile(imageFile);
    logger.info("image: {}", "src/test/resources/two_dogs_in_snow.jpg");

    TextEncoder sentenceEncoder = new TextEncoder();
    ImageEncoder imageEncoder = new ImageEncoder();

    //If text is chinese, isChinese = true, otherwise isChinese = false
    try (ZooModel<String, float[]> textModel = ModelZoo.loadModel(sentenceEncoder.criteria(true));
         Predictor<String, float[]> textPredictor = textModel.newPredictor();
         ZooModel<Image, float[]> imageModel = ModelZoo.loadModel(imageEncoder.criteria());
         Predictor<Image, float[]> imagePredictor = imageModel.newPredictor()) {

      float[] imageEmbeddings = imagePredictor.predict(image);
      logger.info("Vector dimension: {}", imageEmbeddings.length);
      logger.info("image embeddings: {}", Arrays.toString(imageEmbeddings));

      List<float[]> list = new ArrayList<>();
      for (String text : texts) {
        float[] textEmbedding = textPredictor.predict(text);
        list.add(textEmbedding);
      }

      float[] sims = new float[texts.size()];
      for (int i = 0; i < sims.length; i++) {
        logger.info("text [{}] embeddings: {}", texts.get(i), Arrays.toString(list.get(i)));
        sims[i] = 100 * FeatureComparison.cosineSim(imageEmbeddings, list.get(i));
        logger.info("Similarity: {}%", sims[i]);
      }

      logger.info("Label probs: {}", Arrays.toString(FeatureComparison.softmax(sims)));
    }
  }
}
