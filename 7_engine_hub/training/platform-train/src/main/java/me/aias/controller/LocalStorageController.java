package me.aias.controller;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.aias.common.exception.BadRequestException;
import me.aias.config.FileProperties;
import me.aias.domain.LocalStorage;
import me.aias.domain.ResultBean;
import me.aias.service.LocalStorageService;
import me.aias.utils.FileUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * @author Calvin
 * @date 2021-06-20
 **/
@RestController
@RequiredArgsConstructor
@Api(tags = "Storage Management")
@RequestMapping("/api/localStorage")
public class LocalStorageController {

    private final LocalStorageService localStorageService;
    private final FileProperties properties;

    @ApiOperation("Get File List")
    @GetMapping("/list")
    public ResultBean getContact() {
        List<LocalStorage> result = localStorageService.getStorageList();
        return ResultBean.success().add("result", result);
    }

    @ApiOperation("Upload File")
    @PostMapping("/file")
    public ResultBean create(@RequestParam("file") MultipartFile multipartFile) {
        FileUtil.checkSize(properties.getMaxSize(), multipartFile.getSize());
        String suffix = FileUtil.getExtensionName(multipartFile.getOriginalFilename());
        String type = FileUtil.getFileType(suffix);
        File file = FileUtil.upload(multipartFile, properties.getPath().getPath() + type + File.separator);
        if (ObjectUtil.isNull(file)) {
            throw new BadRequestException("Upload failed");
        }
        try {
            LocalStorage localStorage = new LocalStorage(
                    file.getName(),
                    FileUtil.getFileNameNoEx(multipartFile.getOriginalFilename()),
                    suffix,
                    file.getPath(),
                    type,
                    FileUtil.getSize(multipartFile.getSize())
            );

            localStorageService.addStorageFile(localStorage);
        } catch (Exception e) {
            FileUtil.del(file);
            throw e;
        }
        return ResultBean.success();
    }

    @ApiOperation("Delete File")
    @DeleteMapping
    public ResultBean delete(@RequestBody LocalStorage localStorage) {
        LocalStorage storage = localStorageService.findById(localStorage.getId());
        FileUtil.del(storage.getPath());
        localStorageService.delete(localStorage.getId());
        return ResultBean.success();
    }

    @PostMapping("/features")
    @ApiOperation("Extract Features")
    public ResponseEntity<Object> extractFeatures(@RequestBody Long[] ids) {
        Long[] idArr = ids;
//        localStorageService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}