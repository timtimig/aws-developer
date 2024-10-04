package com.timigv.aws;

import com.timigv.aws.s3.CreateBucket;

public class Application {

    public static void main(String[] args) {
        new CreateBucket().run();
    }
}
