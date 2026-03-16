package com.sixestates.type;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.annotation.Contract;

import java.io.File;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@Contract
public class FAASTaskInfo implements Serializable {

    @HttpKey(ignore = true)
    private List<File> files ;


    @HttpKey(ignore = true)
    private List<FileInfo> fileInfos;

    private String customerType;

    private String countryId;

    private String regionId;

    private Integer informationType;

    private String cifNumber;

    private String borrowerName;

    private Float loanAmount;

    private String applicationNumber;

    private String applicationDate;

    private String currency;

    private Integer rateDateType;

    private Integer rateFrom;

    private String rateDate;

    private Boolean automatic;

    private Integer hitlType;

    private String industryType;

    private String industryBiCode;

    private String ebitdaRatio;

    private String relatedParties;

    private String supplierBuyer;

    private String checkAccountStr;

    private String callbackUrl;

    private Boolean autoCallback;

    private Integer callbackMode;

}
