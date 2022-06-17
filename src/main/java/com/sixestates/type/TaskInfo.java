package com.sixestates.type;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.annotation.Contract;
import java.io.InputStream;
import java.io.Serializable;

@Data
@Builder
@Getter
@Setter
@Contract
public class TaskInfo implements Serializable {

    private String fileName ;

    private String filePath;

    private InputStream inputStream;

    private String fileType;

    private String customer;

    private String customerParam;

    private String callback;

    private int callbackMode;

    private boolean hitl;
}