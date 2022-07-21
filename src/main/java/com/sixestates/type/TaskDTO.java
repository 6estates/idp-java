package com.sixestates.type;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Builder
@Data
public class TaskDTO implements Serializable {

    private String data;

    private int errorCode;

    private String message;

    private int status;
}