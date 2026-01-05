package com.sixestates.enums;

/**
 * On Process/Finished/Failed
 */
public enum DocAgentStatusForApiEnum {
    ON_PROCESS(10, "On Process"),
    FAILED(-1, "Failed"),
    FINISHED(11, "Finished");

    public int code;
    public String name;

    DocAgentStatusForApiEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
    public static DocAgentStatusForApiEnum convertFromDocAgentStatusCode(Integer docAgentStatus) {
        for (DocAgentStatusForApiEnum status : DocAgentStatusForApiEnum.values()) {
            if (status.code == docAgentStatus) {
                return status;
            }
        }
        return ON_PROCESS;
    }
}
