package me.aias.util;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
/**
 *
 * @author Calvin
 *
 * @email 179209347@qq.com
 **/
public class NDArrayUtils {
  // NDArray to opencv_core.Mat
  public static Mat toOpenCVMat(NDManager manager, NDArray srcPoints, NDArray dstPoints) {
    NDArray svdMat = SVDUtils.transformationFromPoints(manager, srcPoints, dstPoints);
    double[] doubleArray = svdMat.toDoubleArray();
    Mat newSvdMat = new Mat(2, 3, CvType.CV_64F);
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 3; j++) {
        newSvdMat.put(i, j, doubleArray[i * 3 + j]);
      }
    }
    return newSvdMat;
  }
}
