package me.aias.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 数据信息对象
 * Data Info Object
 *
 */
@Data
@ApiModel(value = "DataInfo", description = "Data Info Object")
public class DataInfo {
    @ApiModelProperty(value = "id", name = "id")
    private Long id;

    @ApiModelProperty(value = "score", name = "score")
    private Float score;

    @ApiModelProperty(value = "uuid", name = "uuid")
    private String uuid;

    @ApiModelProperty(value = "preName", name = "preName")
    private String preName;

    @ApiModelProperty(value = "fullPath", name = "fullPath")
    private String fullPath;

    @ApiModelProperty(value = "relativePath", name = "relativePath")
    private String relativePath;

    @ApiModelProperty(value = "createTime", name = "createTime")
    private Date createTime;
}
