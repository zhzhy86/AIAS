package me.aias.example;

import me.aias.example.util.AudioArrayUtils;
import me.aias.example.util.AudioConversionUtils;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
/**
 *
 * @author Calvin
 *
 * @email 179209347@qq.com
 **/
public class AudioExample {
    private static final Logger logger = LoggerFactory.getLogger(AudioExample.class);

    // 测试 - Test
    public static void main(String[] args) throws FrameGrabber.Exception, FrameRecorder.Exception {
        // wav sample rate 参数转换
        // Convert wav sample rate parameter
        AudioConversionUtils.convert("src/test/resources/test.wav", "build/output/test_.wav", avcodec.AV_CODEC_ID_PCM_S16LE, 8000, 1);
        // wav转mp3编码示例
        // Example of wav to mp3 encoding
        AudioConversionUtils.convert("src/test/resources/test.wav", "build/output/test.mp3", avcodec.AV_CODEC_ID_MP3, 8000, 1);
        // mp3转wav编码示例
        // Example of mp3 to wav encoding
        AudioConversionUtils.convert("src/test/resources/test.mp3", "build/output/test.wav", avcodec.AV_CODEC_ID_PCM_S16LE, 16000, 1);
        // 音频的float数组
        // Audio float array
        logger.info("audio float array: {}", Arrays.toString(AudioArrayUtils.frameData("src/test/resources/test.wav")));
    }
}