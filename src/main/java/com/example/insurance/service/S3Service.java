package com.example.insurance.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.insurance.component.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3Service {
    private final AmazonS3 amazonS3;
    private final Utils utils;
    @Autowired
    public S3Service(AmazonS3 amazonS3, Utils utils) {
        this.amazonS3 = amazonS3;
        this.utils = utils;
    }

    public String uploadFileToS3(String bucketName,MultipartFile file) throws IOException {
        String imageUrl;
        String fileName = utils.generateFileName(file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        try{
            amazonS3.putObject(new PutObjectRequest(bucketName,fileName,file.getInputStream(), metadata));
            imageUrl = "https://s3-hcm-r1.longvan.net" + "/" + bucketName + "/" + fileName;
        }
        catch (AmazonServiceException e)
        {
            throw new IllegalStateException("Failed to upload the file", e);
        }
        return imageUrl;
    }

}
