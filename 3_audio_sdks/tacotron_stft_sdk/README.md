### 官网：
[官网链接](http://www.aias.top/)

### 下载模型，放置于models目录
- 链接: https://github.com/mymagicpower/AIAS/releases/download/apps/tacotronSTFT.zip

### TacotronSTFT 提取mel(梅尔)频谱
为什么tacotron生成语音时需要先生成Mel(梅尔)频谱？
一般认为语音的频域信号（频谱）相对于时域信号（波形振幅）具备更强的一致性（相同的发音频谱上表现一致但波形差别很大），
经过加窗等处理后相邻帧的频谱具备连贯性，相比于波形数据具备更好的可预测性；另外就是频谱一般处理到帧级别，而波形处理采样点，数量多很多，计算量也自然更大，
所以一般会先预测频谱，然后经由vocoder重建波形，把mel(梅尔)频谱特征表达逆变换为时域波形样本。

- mel频谱
![img](https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/voice_sdks/mel_spec.jpeg)

#### 傅立叶变换
音频信号由几个单频声波组成。 在一段时间内对信号进行采样时，我们仅捕获得到的幅度。 
因为每个信号都可以分解为一组正弦波和余弦波，它们加起来等于原始信号。 这就是著名傅立叶定理。
傅立叶变换是一个数学公式，它使我们可以将信号分解为单个频率和频率幅度。 换句话说，它将信号从时域转换到频域。 结果称为频谱。
快速傅立叶变换（FFT）是一种可以有效计算傅立叶变换的算法。 它广泛用于信号处理。

#### 短时傅立叶变换 - 生成频谱图
快速傅立叶变换是一种功能强大的工具，可让我们分析信号的频率成分。
但是大多数音频信号的频率成分随时间变化，这些信号称为非周期性信号。 
这时我们需要一种表示这些信号随时间变化的频谱的方法。 
我们通过对信号的多个窗口部分执行FFT来计算多个频谱来解决这个问题，称为短时傅立叶变换。 
FFT是在信号的重叠窗口部分上计算的，我们得到了所谓的频谱图。 
- 短时傅立叶变换(STFT)
![stft](https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/voice_sdks/fft.jpeg)

#### 运行例子 - TacotronSTFTExample
运行成功后，命令行应该看到下面的信息:
```text
...
# 测试语音文件：
# src/test/resources/biaobei-009502.mp3

# 生成频谱矩阵：
[INFO ] - melspec shape: [80, 379]

# 频谱矩阵：
[INFO ] - Row 0: [-8.715169, -8.714043, -8.540381, ..., -8.508061, -8.397091]
[INFO ] - Row 1: [-8.985863, -8.835877, -8.99666, ..., -8.95394, -9.151459]
[INFO ] - Row 2: [-9.355588, -9.197474, -9.328396, ..., -10.741011, -10.295704]
...
[INFO ] - Row 77: [-11.512925, -11.512925, -11.512925, ..., -11.512925, -11.512925]
[INFO ] - Row 78: [-11.512925, -11.512925, -11.352638, ..., -11.512925, -11.512925]
[INFO ] - Row 79: [-11.512925, -11.512925, -11.512925, ..., -11.512925, -11.512925]

```

### 开源算法
#### 1. sdk使用的开源算法
- [ttskit](https://gitee.com/kuangdd/ttskit)


#### 2. 模型如何导出 ?
- [how_to_convert_your_model_to_torchscript](http://docs.djl.ai/docs/pytorch/how_to_convert_your_model_to_torchscript.html)

### 其它帮助信息
http://aias.top/guides.html

### Git地址：   
[Github链接](https://github.com/mymagicpower/AIAS)    
[Gitee链接](https://gitee.com/mymagicpower/AIAS)   


#### 参考链接
https://gitee.com/endlesshh/ttskit-java



#### 帮助文档：
- http://aias.top/guides.html
- 1.性能优化常见问题:
- http://aias.top/AIAS/guides/performance.html
- 2.引擎配置（包括CPU，GPU在线自动加载，及本地配置）:
- http://aias.top/AIAS/guides/engine_config.html
- 3.模型加载方式（在线自动加载，及本地配置）:
- http://aias.top/AIAS/guides/load_model.html
- 4.Windows环境常见问题:
- http://aias.top/AIAS/guides/windows.html