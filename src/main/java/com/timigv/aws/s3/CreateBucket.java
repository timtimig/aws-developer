package com.timigv.aws.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.util.Optional;

public class CreateBucket {

    public static final Logger LOGGER = LoggerFactory.getLogger(CreateBucket.class);
    public static final String TEST_BUCKET_NAME = "tim-igv-test";

    public void run() {
        try (var s3 = createS3Client()) {
            if (!bucketExist(s3)) {
                createBucket(s3);
            }
        }
    }

    private boolean bucketExist(S3Client s3) {
        boolean exist = false;
        try {
            var headBucketRequest = HeadBucketRequest.builder().bucket(TEST_BUCKET_NAME).build();
            var headBucketResponse = s3.headBucket(headBucketRequest);
            if (headBucketResponse.sdkHttpResponse().statusCode() == 200) {
                LOGGER.info("Bucket " + TEST_BUCKET_NAME + " already exist");
                exist = true;
            }
        } catch (AwsServiceException e) {
            switch (e.statusCode()) {
                case 404:
                    LOGGER.info("No " + TEST_BUCKET_NAME + " bucket existing");
                case 400:
                    LOGGER.warn("Attempted to access a bucket from a Region other than where it exists");
                case 403:
                    LOGGER.warn("Permission errors in accessing bucket");
            }
        }
        return exist;
    }

    private void createBucket(S3Client s3) {
        try {
            var createRequest = CreateBucketRequest.builder().bucket(TEST_BUCKET_NAME).build();
            s3.createBucket(createRequest);

            var headBucketRequest = HeadBucketRequest.builder().bucket(TEST_BUCKET_NAME).build();
            var waiter = s3.waiter();
            var bucketExistsWaiterResponse = waiter.waitUntilBucketExists(headBucketRequest);
            bucketExistsWaiterResponse.matched().response().ifPresent(System.out::println);
        } catch (S3Exception e) {
            LOGGER.error(e.awsErrorDetails().errorMessage());
        }
    }

    private S3Client createS3Client() {
        return S3Client.builder()
                .region(Region.EU_WEST_1)
                .credentialsProvider(ProfileCredentialsProvider.create("admin"))
                .build();
    }
}
