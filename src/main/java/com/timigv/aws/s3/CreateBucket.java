package com.timigv.aws.s3;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

public class CreateBucket {

    public void run() {
        try (var s3 = createS3Client()) {
            var request = CreateBucketRequest.builder().bucket("test").build();
            s3.createBucket(request);
        }
    }

    private S3Client createS3Client() {
        return S3Client.builder()
            .region(Region.EU_WEST_1)
            .build();
    }
}
