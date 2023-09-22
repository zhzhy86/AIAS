package me.aias.service;

import ai.djl.ModelException;
import ai.djl.modality.cv.Image;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.util.List;

/**
 * 特征提取服务接口
 * Feature Extraction Service Interface
 *
 * @author Calvin
 * @date 2021-12-12
 **/
public interface FeatureService {
    List<Float> textFeature(String text) throws IOException, ModelException, TranslateException;

    List<Float> imageFeature(Image image) throws IOException, ModelException, TranslateException;
}
