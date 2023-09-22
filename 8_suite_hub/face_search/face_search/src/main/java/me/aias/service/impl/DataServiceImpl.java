package me.aias.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import me.aias.common.exception.BadRequestException;
import me.aias.common.exception.BusinessException;
import me.aias.common.utils.common.FileUtil;
import me.aias.common.utils.common.UUIDUtil;
import me.aias.domain.DataInfo;
import me.aias.domain.ImageType;
import me.aias.domain.ResEnum;
import me.aias.service.DataService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 音频文件服务
 *
 * @author Calvin
 * @date 2021-12-12
 **/
@Slf4j
@Service
public class DataServiceImpl implements DataService {
    private static final String FILE_LIST = "data-list.json";
    private Logger logger = LoggerFactory.getLogger(LocalStorageServiceImpl.class);
    private ConcurrentHashMap<String, String> map;

    public DataServiceImpl() {
        StringBuilder sb = new StringBuilder();
        try {
            String path = System.getProperty("user.dir");
            File file = new File(path, FILE_LIST);
            BufferedReader br;
            if (file.exists()) {
                br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
        } catch (IOException e) {
            logger.error("File read error", e);
        }
        String jsonStr = sb.toString();
        if (!StringUtils.isBlank(jsonStr)) {
            map = new Gson().fromJson(jsonStr, new TypeToken<ConcurrentHashMap<String, String>>() {
            }.getType());
        } else {
            map = new ConcurrentHashMap<>();
        }
    }

    /**
     * 新增文件
     * Add file
     */
    public void addData(String id, String audioPath) {
        map.put(id, audioPath);
        saveAudioList();
    }

    /**
     * 根据ID查询
     * Query by ID
     */
    public String findById(String id) {
        return map.get(id);
    }

    /**
     * 获取清单
     * Get file list
     */
    public ConcurrentHashMap<String, String> getMap() {
        return map;
    }

    /**
     * 保存上传文件列表
     * Save uploaded file list
     */
    private void saveAudioList() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonStr = gson.toJson(map);
        try {
            File file = new File(FILE_LIST);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.print(jsonStr);
        } catch (FileNotFoundException e) {
            logger.error("Storage file not found", e);
        }
    }

    @Override
    public List<DataInfo> uploadData(String rootPath, String UUID)
            throws BusinessException, IOException {
        if (!new File(rootPath).exists()) {
            new File(rootPath).mkdirs();
        }
        String unZipFilePath = rootPath + UUID;
        // 以压缩文件名为新目录
        // the new directory name is the same as the compressed file name
        try {
            // 记录文件集合
            // record the file collection
            List<DataInfo> resultList = new ArrayList<>();
            // 解压缩后的文件
            // decompressed files
            File[] listFiles = new File(unZipFilePath).listFiles();

            // 判断上传文件是否包含图片文件
            // determine whether the uploaded file contains image files
            boolean found = false;
            for (File file : listFiles) {
                String suffix = FileUtil.getExtensionName(file.getName());
                if ((suffix.equalsIgnoreCase(ImageType.FILE_JPEG.key)
                        || suffix.equalsIgnoreCase(ImageType.FILE_JPG.key)
                        || suffix.equalsIgnoreCase(ImageType.FILE_PNG.key))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // 通用异常
                // general exception
                throw new BadRequestException(ResEnum.IMAGE_NOT_FOUND.VALUE);
            }

            // 保存文件到可访问路径
            // save file to accessible path
            for (File file : listFiles) {
                DataInfo dataInfo = new DataInfo();
                dataInfo.setPreName(file.getName());
                String suffix = FileUtil.getExtensionName(file.getName());
                if ((suffix.equalsIgnoreCase(ImageType.FILE_JPEG.key)
                        || suffix.equalsIgnoreCase(ImageType.FILE_JPG.key)
                        || suffix.equalsIgnoreCase(ImageType.FILE_PNG.key))) {

                    byte[] bytes = FileUtil.getByte(file);
                    String uuid = UUIDUtil.getUUID();
                    dataInfo.setUuid(uuid);
                    // 待存储的文件名
                    // file name to be stored
                    String fileName = uuid + "." + suffix;
                    String relativePath = FileUtil.generatePath(rootPath);
                    // filePath 完整路径（含uuid文件名）
                    // filePath complete path (including uuid filename)
                    String filePath = rootPath + relativePath + fileName;
                    dataInfo.setFullPath(filePath);
                    dataInfo.setRelativePath(relativePath + fileName);
                    // 转成文件保存
                    // convert to file and save
                    FileUtil.bytesToFile(bytes, filePath);
                    resultList.add(dataInfo);
                }
            }
            return resultList;
        } finally {
            // unZipFilePath = rootPath + uuid; // 以压缩文件名为新目录
            // unZipFilePath = rootPath + uuid; // the new directory name is the same as the compressed file name
            FileUtil.delete(unZipFilePath);

        }
    }
}