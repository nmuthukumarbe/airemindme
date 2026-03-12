/**
 * 
 */
package com.server.realsync.mvc.controllers;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * 
 */
public class CheckBucket {

    public static void main(String[] args) {
        String endpoint = "https://blr1.digitaloceanspaces.com";
        String accessKeyId = "DO00V4VEY68EZENYK6XF";
        String secretAccessKey = "59lsL4S8kKyWsC2vjygcq2LAeu4Vck4s69i6skPaCGI";
        String bucketName = "aitest";
        
        
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "blr1"))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        try {
            if (s3Client.doesBucketExistV2(bucketName)) {
                System.out.println("Bucket exists and is accessible.");
            } else {
                System.out.println("Bucket does not exist.");
            }
        } catch (Exception e) {
            System.out.println("Error checking bucket: " + e.getMessage());
        }
    }
}