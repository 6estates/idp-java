package com.sixestates.type;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.annotation.Contract;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@Getter
@Setter
@Contract
public class TaskInfo implements Serializable {

    private List<File> files ;

    private List<FileInfo> fileInfos;

    private String fileType;

    private String customer;

    private String customerParam;

    private String callback;

    private Boolean autoCallback;

    private int callbackMode;

    private boolean hitl;

    /**
     * 是否返回auth check内容，0：否，1：是
     */
    private int autoChecks;

    /**
     * 提取类型，1:Lite, 2:Regular, 3:Advance
     */
    private Integer extractMode;

    /**
     * 自定义返回field，默认全部
     */
    private String includingFieldCodes;

    /**
     * 1 普通提取的fileType是来6E定义的fileType  2 表示使用zero learning用户的定义的fileType
     */
    private Integer fileTypeFrom = 1;


    /**
     * 备注信息
     */
    private String remark;
}
