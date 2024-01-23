package com.example.insurance.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {



    @Bean
    public AmazonS3 amazonS3()
    {
        AWSCredentials awsCredentials = new BasicAWSCredentials("FKN6BT7K0LQ1YBE81MQX", "k4F0ibxy3Th40eoaxCNQaXqC7MXFtr8QV81V504E");
        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("https://s3-hcm-r1.longvan.net","ap-southeast-1"))
//                .withRegion(Regions.DEFAULT_REGION)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

}
