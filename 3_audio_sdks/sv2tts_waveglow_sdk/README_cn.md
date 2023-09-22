
### 下载模型，放置于models目录
- 链接: https://pan.baidu.com/s/1N3N0-AsUwV6fMc5BSBFNKA?pwd=dv9s

### TTS 文本转为语音
注意: 为了防止克隆他人声音用于非法用途，代码限定音色文件只能使用程序中给定的音色文件。
声音克隆是指使用特定的音色，结合文字的读音合成音频，使得合成后的音频具有目标说话人的特征，从而达到克隆的目的。
在训练语音克隆模型时，目标音色作为Speaker Encoder的输入，模型会提取这段语音的说话人特征（音色）作为Speaker Embedding。接着，
在训练模型重新合成此类音色的语音时，除了输入的目标文本外，说话人的特征也将成为额外条件加入模型的训练。
在预测时，选取一段新的目标音色作为Speaker Encoder的输入，并提取其说话人特征，最终实现输入为一段文本和一段目标音色，
模型生成目标音色说出此段文本的语音片段。
Google 团队提出了一种文本语音合成（text to speech）神经系统，能通过少量样本学习到多个不同说话者（speaker）的语音特征，
并合成他们的讲话音频。此外，对于训练时网络没有接触过的说话者，也能在不重新训练的情况下，
仅通过未知说话者数秒的音频来合成其讲话音频，即网络具有零样本学习能力。
传统的自然语音合成系统在训练时需要大量的高质量样本，通常对每个说话者，都需要成百上千分钟的训练数据，这使得模型通常不具有普适性，
不能大规模应用到复杂环境（有许多不同的说话者）。而这些网络都是将语音建模和语音合成两个过程混合在一起。
SV2TTS工作首先将这两个过程分开，通过第一个语音特征编码网络（encoder）建模说话者的语音特征，接着通过第二个高质量的TTS网络完成特征到语音的转换。

- SV2TTS论文
[Transfer Learning from Speaker Verification to  Multispeaker Text-To-Speech Synthesis](https://arxiv.org/pdf/1806.04558.pdf)

- 网络结构
![SV2TTS](https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/voice_sdks/SV2TTS.png)

#### 主要由三部分构成：
#### 声音特征编码器（speaker encoder）
提取说话者的声音特征信息。将说话者的语音嵌入编码为固定维度的向量，该向量表示了说话者的声音潜在特征。
编码器主要将参考语音信号嵌入编码到固定维度的向量空间，并以此为监督，使映射网络能生成具有相同特征的原始声音信号（梅尔频谱图）。
编码器的关键作用在于相似性度量，对于同一说话者的不同语音，其在嵌入向量空间中的向量距离（余弦夹角）应该尽可能小，而对不同说话者应该尽可能大。
此外，编码器还应具有抗噪能力和鲁棒性，能够不受具体语音内容和背景噪声的影响，提取出说话者声音的潜在特征信息。
这些要求和语音识别模型（speaker-discriminative）的要求不谋而合，因此可以进行迁移学习。
编码器主要由三层LSTM构成，输入是40通道数的对数梅尔频谱图，最后一层最后一帧cell对应的输出经过L2正则化处理后，即得到整个序列的嵌入向量表示。
实际推理时，任意长度的输入语音信号都会被800ms的窗口分割为多段，每段得到一个输出，最后将所有输出平均叠加，得到最终的嵌入向量。
这种方法和短时傅里叶变换（STFT）非常相似。
生成的嵌入空间向量可视化如下图：
![embedding](https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/voice_sdks/embedding.jpeg)

可以看到不同的说话者在嵌入空间中对应不同的聚类范围，可以轻易区分，并且不同性别的说话者分别位于两侧。
（然而合成语音和真实语音也比较容易区分开，合成语音离聚类中心的距离更远。这说明合成语音的真实度还不够。）

#### 序列到序列的映射合成网络（Tacotron 2）
基于Tacotron 2的映射网络，通过文本和声音特征编码器得到的向量来生成对数梅尔频谱图。
梅尔光谱图将谱图的频率标度Hz取对数，转换为梅尔标度，使得人耳对声音的敏感度与梅尔标度承线性正相关关系。
该网络独立于编码器网络的训练，以音频信号和对应的文本作为输入，音频信号首先经过预训练的编码器提取特征，然后再作为attention层的输入。
网络输出特征由窗口长度为50ms，步长为12.5ms序列构成，经过梅尔标度滤波器和对数动态范围压缩后，得到梅尔频谱图。
为了降低噪声数据的影响，还对该部分的损失函数额外添加了L1正则化。

输入梅尔频谱图与合成频谱图的对比示例如下：
![embedding](https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/voice_sdks/tacotron2.jpeg)
右图红线表示文本和频谱的对应关系。可以看到，用于参考监督的语音信号不需要与目标语音信号在文本上一致，这也是SV2TTS论文工作的一大特色。

关于梅尔频谱图，可以看这篇文章。
https://zhuanlan.zhihu.com/p/408265232

#### 语音合成网络 (WaveGlow)
WaveGlow:一种依靠流的从梅尔频谱图合成高质量语音的网络。它结合了Glow和WaveNet，生成的快、好、高质量的韵律，而且还不需要自动回归。
将梅尔频谱图（谱域）转化为时间序列声音波形图（时域），完成语音的合成。
需要注意的是，这三部分网络都是独立训练的，声音编码器网络主要对序列映射网络起到条件监督作用，保证生成的语音具有说话者的独特声音特征。

## 运行例子 - TTSExample
运行成功后，命令行应该看到下面的信息:
```text
...
[INFO ] - 文本: 基于给定音色将文本转为语音
[INFO ] - 给定音色: src/test/resources/biaobei-009502.mp3

# 生成特征向量：
[INFO ] - Speaker Embedding Shape: [256]
[INFO ] - Speaker Embedding: [0.06272025, 0.0, 0.24136968, ..., 0.027405139, 0.0, 0.07339379, 0.0]
[INFO ] - mel频谱数据 Shape: [80, 331]
[INFO ] - mel频谱数据: [-6.739388, -6.266942, -5.752069, ..., -10.643405, -10.558134, -10.5380535]
[INFO ] - 生成wav音频文件: build/output/audio.wav
```
文本 - "基于给定音色将文本转为语音" - 生成的语音效果：
[audio.wav](https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/voice_sdks/audio.wav)


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