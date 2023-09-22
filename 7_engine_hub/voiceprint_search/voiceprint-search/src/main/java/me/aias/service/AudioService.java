package me.aias.service;

import me.aias.common.exception.BusinessException;
import me.aias.domain.AudioInfo;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务接口
 *
 * @author Calvin
 * @date 2021-12-12
 **/
public interface AudioService {
    /**
     * 根据ID查询
     * Get info by id
     */
    String findById(String id);

    /**
     * 添加音频文件
     * Add Audio Ffile
     */
    void addAudioFile(String id, String audioPath);

    /**
     * 获取清单
     * Get file list
     */
    ConcurrentHashMap<String, String> getMap();

    /**
     * 上传音频文件
     * upload audio files
     */
    public List<AudioInfo> uploadAudios(String rootPath, String UUID)
            throws BusinessException, IOException;
}