### 官网：
[官网链接](http://www.aias.top/)

### 下载模型，放置于models目录
- 链接: https://pan.baidu.com/s/1dbWkbO0N5jsk-sruJo7XJQ?pwd=s1qv

### 智慧工地检测SDK
支持检测的类别：
- person （人体）
- head   （没戴安全帽）
- helmet （戴安全帽）

### SDK功能
工地安全检测，给出检测框和置信度。
- 提供三个模型：
- 小模型（yolov5s 29.7M）
- 中模型（yolov5m 86.8M）
- 大模型（yolov5l 190.8M）

#### 运行小模型例子 - Yolov5sExample
- 测试图片效果（只显示安全帽检测，过滤了其它类别的显示，具体看代码）
![small](https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/sec_sdks/images/helmet_head_person_s.jpeg)

#### 运行中模型例子 - Yolov5mExample
- 测试图片效果
![medium](https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/sec_sdks/images/helmet_head_person_m.jpeg)

#### 运行大模型例子 - Yolov5lExample
- 测试图片效果
![large](https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/sec_sdks/images/helmet_head_person_l.jpeg)


运行成功后，命令行应该看到下面的信息:
```text
[INFO ] - [
	class: "helmet", probability: 0.89502, bounds: [x=0.956, y=0.525, width=0.044, height=0.067]
	class: "helmet", probability: 0.85951, bounds: [x=0.237, y=0.439, width=0.036, height=0.046]
	class: "helmet", probability: 0.81705, bounds: [x=0.901, y=0.378, width=0.036, height=0.052]
	class: "helmet", probability: 0.80817, bounds: [x=0.250, y=0.399, width=0.029, height=0.040]
	class: "helmet", probability: 0.80528, bounds: [x=0.771, y=0.336, width=0.029, height=0.043]
]
```


### 开源算法
#### 1. sdk使用的开源算法
- [Smart_Construction](https://github.com/PeterH0323/Smart_Construction)

#### 2. 模型如何导出 ?
- [how_to_convert_your_model_to_torchscript](http://docs.djl.ai/docs/pytorch/how_to_convert_your_model_to_torchscript.html)

- 导出模型（pytorch 模型特殊，CPU&GPU模型不通用。所以CPU，GPU需要分别导出）
- torch.device('cpu')
- torch.device('gpu')
```text
"""Exports a YOLOv5 *.pt model to ONNX and TorchScript formats

Usage:
    $ export PYTHONPATH="$PWD" && python models/export.py --weights ./weights/yolov5s.pt --img 640 --batch 1
"""

import argparse

import torch

from utils.google_utils import attempt_download

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--weights', type=str, default='./helmet_head_person_s.pt', help='weights path')
    parser.add_argument('--img-size', nargs='+', type=int, default=[640, 640], help='image size')
    parser.add_argument('--batch-size', type=int, default=1, help='batch size')
    parser.add_argument('--augment', action='store_true', help='augmented inference')
    opt = parser.parse_args()
    opt.img_size *= 2 if len(opt.img_size) == 1 else 1  # expand
    print(opt)

    # Input
    img = torch.zeros((opt.batch_size, 3, *opt.img_size))  # image size(1,3,320,192) iDetection

    # Load PyTorch model
    attempt_download(opt.weights)
    model = torch.load(opt.weights, map_location=torch.device('cpu'))['model'].float()
    model.eval()
    model.model[-1].export = False  # set Detect() layer export=True
    if img.ndimension() == 3:
        img = img.unsqueeze(0)
    y = model(img)  # dry run

    # TorchScript export
    try:
        print('\nStarting TorchScript export with torch %s...' % torch.__version__)
        f = opt.weights.replace('.pt', '.torchscript.pt')  # filename
        ts = torch.jit.trace(model, img)
        ts.save(f)
        print('TorchScript export success, saved as %s' % f)
    except Exception as e:
        print('TorchScript export failure: %s' % e)

    # # ONNX export
    # try:
    #     import onnx
    #
    #     print('\nStarting ONNX export with onnx %s...' % onnx.__version__)
    #     f = opt.weights.replace('.pt', '.onnx')  # filename
    #     model.fuse()  # only for ONNX
    #     torch.onnx.export(model, img, f, verbose=False, opset_version=12, input_names=['images'],
    #                       output_names=['classes', 'boxes'] if y is None else ['output'])
    #
    #     # Checks
    #     onnx_model = onnx.load(f)  # load onnx model
    #     onnx.checker.check_model(onnx_model)  # check onnx model
    #     print(onnx.helper.printable_graph(onnx_model.graph))  # print a human readable model
    #     print('ONNX export success, saved as %s' % f)
    # except Exception as e:
    #     print('ONNX export failure: %s' % e)
    #
    # # CoreML export
    # try:
    #     import coremltools as ct
    #
    #     print('\nStarting CoreML export with coremltools %s...' % ct.__version__)
    #     # convert model from torchscript and apply pixel scaling as per detect.py
    #     model = ct.convert(ts, inputs=[ct.ImageType(name='images', shape=img.shape, scale=1 / 255.0, bias=[0, 0, 0])])
    #     f = opt.weights.replace('.pt', '.mlmodel')  # filename
    #     model.save(f)
    #     print('CoreML export success, saved as %s' % f)
    # except Exception as e:
    #     print('CoreML export failure: %s' % e)

    # Finish
    print('\nExport complete. Visualize with https://github.com/lutzroeder/netron.')

```

### 其它帮助信息
http://aias.top/guides.html

### Git地址：   
[Github链接](https://github.com/mymagicpower/AIAS)    
[Gitee链接](https://gitee.com/mymagicpower/AIAS)   


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