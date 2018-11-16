/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.util.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3Encryption;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.ObjectMetadata;

/**
 *
 * @author akrymskiy
 */
public class S3 {
	public static final ClientConfiguration DEFAULT_AWS_CLIENT_CONFIGURATION =
		new ClientConfiguration()
            .withMaxErrorRetry(6)
            .withProtocol(Protocol.HTTP);
	
	public static final ClientConfiguration DEFAULT_AWS_CLIENT_CONFIGURATION_HTTPS =
		new ClientConfiguration()
            .withMaxErrorRetry(6)
            .withProtocol(Protocol.HTTPS);
	
	public static AmazonS3 getAmazonS3Client(Region region, ClientConfiguration clientConfiguration) {
		return AmazonS3ClientBuilder
			.standard()
			.withClientConfiguration(clientConfiguration)
			.withRegion(region.getName())
			.build();
	}
	
	public static AmazonS3 getAmazonS3Client(Region region) {
		return getAmazonS3Client(region, DEFAULT_AWS_CLIENT_CONFIGURATION);
	}
	
	public static AmazonS3Encryption getAmazonS3EncryptionClient(Region region, String encryptionKey, ClientConfiguration clientConfiguration) {
		return AmazonS3EncryptionClientBuilder
			.standard()
			.withCredentials(new DefaultAWSCredentialsProviderChain())
			.withEncryptionMaterials(new KMSEncryptionMaterialsProvider(encryptionKey))
			.withClientConfiguration(clientConfiguration)
			.withRegion(region.getName())
			.build();
	}
	
	public static AmazonS3Encryption getAmazonS3EncryptionClient(Region region, String encryptionKey) {
		return getAmazonS3EncryptionClient(region, encryptionKey, DEFAULT_AWS_CLIENT_CONFIGURATION);
	}
	
	public static ObjectMetadata makeObjectMetadata(long contentLength) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(contentLength);
		
		return objectMetadata;
	}
}
