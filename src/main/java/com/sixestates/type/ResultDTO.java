package com.sixestates.type;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
public class ResultDTO implements Serializable {

    private String taskStatus;

    private String respJson;
}