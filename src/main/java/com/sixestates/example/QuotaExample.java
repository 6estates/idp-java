package com.sixestates.example;

import com.sixestates.Idp;
import com.sixestates.rest.v1.quota.QuotaFetcher;
import com.sixestates.type.IdpResponse;

import static com.sixestates.example.Example.TOKEN;

/**
 * @author yec
 * @description
 * @Data 2026/1/2
 */
public class QuotaExample {

    public static void main(String[] args) {
        Idp.init(TOKEN);
        try {
            IdpResponse<QuotaFetcher.QuotaData> response = QuotaFetcher.fetchQuota();

            if (response.isSuccessful()) {
                QuotaFetcher.QuotaData quotaData = response.getData();

                System.out.println("剩余配额: " + quotaData.getQuota());
                System.out.println("整数形式: " + quotaData.getQuotaAsInteger());

                if (quotaData.hasQuota()) {
                    System.out.println("有可用配额");
                } else {
                    System.out.println("配额已耗尽");
                }
            }
        } catch (Exception e) {
            System.err.println("查询配额失败: " + e.getMessage());
        }
    }
}
