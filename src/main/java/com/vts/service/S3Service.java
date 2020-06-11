package com.vts.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

	@Value("${aws.accesskey}")
	private String accessKey;
	
	@Value("${aws.secretkey}")
	private String secretKey;
	
	@Value("${aws.bucketname}")
	private String bucketName;
	
	@Value("${directory.base}")
	private String BASE;
	
	private AmazonS3 s3client;
	
	@PostConstruct
	private void connectAWS() {
		
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		
		s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.US_EAST_2)
				  .build();
		
		// Note that bucket names across a region are shared between ALL AWS ACCOUNTS
		// Plain old doesBucketExist returns true EVEN WHEN credentials are wrong 
		if(!s3client.doesBucketExistV2(bucketName)) {
			s3client.createBucket(bucketName);
		}

	}
	
	public void createFolderWithinBucket(String folderName) {
		
		// create empty data to be able to create the folder
	    ObjectMetadata metadata = new ObjectMetadata();
	    metadata.setContentLength(0);
	    InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		
		PutObjectRequest req = new PutObjectRequest(bucketName, folderName + "/", emptyContent, metadata);
		
		s3client.putObject(req);
		
	}
	
	public void uploadChunk(String folderName, String fileName, File file) {
		s3client.putObject(bucketName, folderName + "/" + fileName, file);
	}
	
	// TODO Use this for testing
	public int getBucketSize() {
		return s3client.listObjects(bucketName).getObjectSummaries().size();
	}

	
}
