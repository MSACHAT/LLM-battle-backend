package com.example.llm_rating.service.impl;

import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.exceptions.ClientException;
import com.example.llm_rating.config.OssConfig;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@AllArgsConstructor
public class FileServiceImpl {


    private final OssConfig ossClient;

    @Async
    public CompletableFuture<PutObjectResult> uploadFile(String bucketName, String objectName, String filePath) throws ClientException {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new File(filePath));
        PutObjectResult Result = ossClient.ossClient().putObject(putObjectRequest);
        return CompletableFuture.completedFuture(Result);
    }



}
