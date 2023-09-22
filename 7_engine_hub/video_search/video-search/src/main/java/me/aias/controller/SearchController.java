package me.aias.controller;

import io.milvus.Response.SearchResultsWrapper;
import io.milvus.grpc.SearchResults;
import io.milvus.param.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.aias.common.face.FaceObject;
import me.aias.common.utils.ImageUtil;
import me.aias.domain.ImageInfoRes;
import me.aias.domain.ResEnum;
import me.aias.domain.ResultRes;
import me.aias.service.DetectService;
import me.aias.service.ImageService;
import me.aias.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 搜索管理
 * Search management
 *
 * @author Calvin
 * @date 2021-12-12
 **/
@Slf4j
@Api(tags = "搜索管理 - Search management")
@RequestMapping("/api/search")
@RequiredArgsConstructor
@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private DetectService detectService;
    @Value("${image.baseurl}")
    String baseurl;

    @PostMapping(value = "/image")
    @ApiOperation(value = "搜索图片 - Search for images", nickname = "searchImage")
    public ResponseEntity<Object> searchImage(@RequestParam("image") MultipartFile imageFile, @RequestParam(value = "topK") String topk) {
        // 根据base64 生成向量
        // Generate vectors based on base64
        BufferedImage bufferedImage = ImageUtil.multipartFileToBufImage(imageFile);
        Integer topK = Integer.parseInt(topk);

        List<Float> vectorToSearch;
        try {
            //人脸检测 & 特征提取
            // Face detection & feature extraction
            List<FaceObject> faceObjects = detectService.faceDetect(bufferedImage);
            //如何有多个人脸，取第一个（也可以选最大的，或者一起送入搜索引擎搜索）
            // If there are multiple faces, take the first one (can also choose the largest, or search all in the search engine)
            vectorToSearch = faceObjects.get(0).getFeature();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseEntity<>(ResultRes.error(ResEnum.MODEL_ERROR.KEY, ResEnum.MODEL_ERROR.VALUE), HttpStatus.OK);
        }

        List<List<Float>> vectorsToSearch = new ArrayList<List<Float>>();
        vectorsToSearch.add(vectorToSearch);

        try {
            // 根据图片向量搜索
            // Search based on image vectors
            R<SearchResults> searchResponse = searchService.search(topK, vectorsToSearch);
            SearchResultsWrapper wrapper = new SearchResultsWrapper(searchResponse.getData().getResults());
            List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(0);

            // 根据ID获取图片信息
            // Get image information based on ID
            ConcurrentHashMap<String, String> map = imageService.getMap();
            List<ImageInfoRes> imageInfoResList = new ArrayList<>();
            for (SearchResultsWrapper.IDScore score : scores) {
                ImageInfoRes imageInfoRes = new ImageInfoRes();
                Float value = maxScoreForImageId(scores, score.getLongID());
                imageInfoRes.setScore(value);
                imageInfoRes.setId(score.getLongID());
                imageInfoRes.setImgUrl(baseurl + map.get("" + score.getLongID()));
                imageInfoResList.add(imageInfoRes);
            }

            return new ResponseEntity<>(ResultRes.success(imageInfoResList, imageInfoResList.size()), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseEntity<>(ResultRes.error(ResEnum.MILVUS_CONNECTION_ERROR.KEY, ResEnum.MILVUS_CONNECTION_ERROR.VALUE), HttpStatus.OK);
        }
    }

    private Float maxScoreForImageId(List<SearchResultsWrapper.IDScore> scores, Long imageId) {
        float maxScore = -1;
        for (SearchResultsWrapper.IDScore score : scores) {
            if (score.getLongID() == imageId.longValue()) {
                if (score.getScore() > maxScore) {
                    maxScore = score.getScore();
                }
            }
        }
        return maxScore;
    }
}
