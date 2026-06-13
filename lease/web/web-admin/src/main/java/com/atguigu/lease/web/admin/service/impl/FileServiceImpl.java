package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.minio.MinioProperties;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.web.admin.service.FileService;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties properties;

    @Override
    public String upload(MultipartFile file) {

        try {
            // 判断存储桶是否存在，如果不存在则创建存储桶
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucketName()).build());
            if (!bucketExists){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucketName()).build());
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(properties.getBucketName()).config(createBucketPolicyConfig(properties.getBucketName())).build());
            }

            //为上传文件创建一个独有的名字，防止文件覆盖
            String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            //上传文件到存储桶中
            minioClient.putObject(PutObjectArgs.builder()
                    .contentType(file.getContentType())
                    .bucket(properties.getBucketName())
                    .object(filename)
                    .stream(file.getInputStream(),file.getSize(),-1)
                    .build());
            //返回文件的访问地址
            return String.join("/", properties.getEndpoint(),properties.getBucketName(),filename);
        } catch (Exception e) {
            log.error("上传文件失败");
            throw new LeaseException(ResultCodeEnum.FILE_UPLOAD_ERROR);
        }
    }

    private String createBucketPolicyConfig(String bucketName) {
        // 创建存储桶的策略配置 ，访问权限
        return """
            {
              "Statement" : [ {
                "Action" : "s3:GetObject",
                "Effect" : "Allow",
                "Principal" : "*",
                "Resource" : "arn:aws:s3:::%s/*"
              } ],
              "Version" : "2012-10-17"
            }
            """.formatted(bucketName);
    }
}
