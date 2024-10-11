package com.sixestates.type;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * @author kechen, 10/10/24.
 */
@Data
@Builder
@Getter
@Setter
public class DocAgentInfo implements Serializable {
    private String flowCode;

    @HttpKey(ignore = true)
    private File file ;

    @HttpKey(ignore = true)
    private FileInfo fileInfo;

    private String callback;

    private Boolean autoCallback;

    private Integer callbackMode;

    private String callbackQaCodes;

}
