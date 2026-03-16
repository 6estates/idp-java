package com.sixestates.rest.v1;

import com.sixestates.Idp;
import com.sixestates.type.*;
import com.sixestates.utils.OauthUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author kechen, 11/10/24.
 */
public class DocAgentApiTest {
    @Before
    public void init() {
        OauthDTO oauthDTO = OauthUtils.getIDPAuthorization("bgjanadh97", "l1ieume6qytdqyfjzqwn");
        String authorization = oauthDTO.getData().getValue();
        Idp.initAuthorization(authorization);
    }

    @Test
    public void testSubmit() {
        DocAgentInfo docAgentInfo = DocAgentInfo.builder()
                .file(new File("/Users/6e/Downloads/DBS+SGD+Jul+2024+(Masked).pdf"))
                .flowCode("DAG16")
                .build();
        TaskDTO taskDTO = DocAgentApi.submit(docAgentInfo);
        Assert.assertNotNull(taskDTO);
    }

    @Test
    public void testGetStatus() {
        TaskResult<DocAgentStatusInfo> taskResult = DocAgentApi.getStatus("DAG226");
        Assert.assertNotNull(taskResult);
    }

    @Test
    public void testExport() {
        FileInfo fileInfo = DocAgentApi.export("DAG226");
        Assert.assertNotNull(fileInfo);
    }
}