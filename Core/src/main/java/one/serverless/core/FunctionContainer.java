/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.core;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import one.serverless.core.annotation.AWSLambda;
import one.serverless.core.exception.PassThroughException;
import one.serverless.core.local.AWSLocalContext;
import one.serverless.util.CollectionUtils;
import one.serverless.util.MiscUtils;
import one.serverless.util.aws.S3;
import one.serverless.util.xt.XT;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringSubstitutor;

/**
 *
 * @author akrymskiy
 * @param <C>
 */
public abstract class FunctionContainer<C extends FunctionConfig> {
	// Constants
	public static final Pattern AWS_ARN_PATTERN = Pattern.compile("arn:aws:lambda:([^:]*):([^:]*):function:([^:]*):*([^:]*)");
	public static final String AWS_CONFIG_PATH_TEMPLATE = "lambda/${projectName}/${functionAlias}.json";
	
	// Static variables
	// Instance/call counter (per alias)
	protected static final Map<String, AtomicLong> AWS_LAMBDA_INVOKE_COUNTER = new HashMap<>();

	// Static config holder (concrete config from the implementing class)(per alias)
	private static final Map<String, Object> CACHED_CONFIG = new HashMap<>();
	
	public static final ObjectMapper OBJECT_MAPPER =
		new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
			.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
	
	private static Method awsLambdaMethod;
	private static Class<?> awsLambdaMethodParamClass;
	private static Class<?> awsLambdaMethodReturnParamClass;
	private static AWSLambda awsLambdaAnnotation;
	private static Class<?> projectClass;
	
	// Caching config
	private static String configBucket = null;
	private static String configEncKey = null;
	
	// Instance config
	protected C config;
	private String configString;
	
	// Instance variables
	protected Region awsLambdaRegion;
	protected String awsLambdaName;
	protected String awsLambdaAlias;
	
	protected FunctionLog log;
	
	/**
	 * Returns the type for configuration class
	 * Needed to deal with Java Generics type erasure
	 * @return type for config class
	 */
	//protected abstract Class<C> getFunctionConfigClass();
	
	// Dynamically computed parameter class 
	private final Class<C> configClass;
	// Indicator of whether the implementing class is parametrized and uses config
	private final boolean configurable;
	
	/**
	 * Computes location for the static config bucket
	 * Can override with hardcoded value or custom logic
	 * @return
	 */
	protected String getFunctionConfigStorageBucket() {
		return Optional
			.ofNullable(configBucket)
			.orElse(MiscUtils.getPropOrEnv("CONFIG_BUCKET"));
	}
	
	/**
	 * Computes the config encryption key to use (optional)
	 * Can override with hardcoded value or custom logic
	 * @return
	 */
	protected String getFunctionConfigEncryptionKey() {
		return Optional
			.ofNullable(configEncKey)
			.orElse(MiscUtils.getPropOrEnv("CONFIG_ENC_KEY"));
	}
	
	protected String getFunctionConfigPath() {
		return getFunctionConfigPath(AWS_CONFIG_PATH_TEMPLATE);
	}
	//"${bucket}/lambda/${class}/${alias}.json";
	protected String getFunctionConfigPath(String template) {
		return StringSubstitutor.replace(
			template,
			CollectionUtils
				.pairsToMap(
					Pair.of("projectName", this.awsLambdaName.replaceAll("_.*", "")), // ToDo: TEMP plugin project class here
					Pair.of("functionAlias", this.awsLambdaAlias)));
	}

	@SuppressWarnings("unchecked")
	public FunctionContainer() {
		// Defeat Erasure! - ToDo: Optimize this with static string var
		// Find parametrized FunctionContainer and retrieve generic type
		Optional<String> configClassString =
			StreamSupport
				.stream(
					ClassUtils
						.hierarchy(this.getClass())
						.spliterator(),
					false)
				.filter(x ->
					Optional
						.ofNullable(x.getSuperclass())
						.map(FunctionContainer.class::equals)
						.orElse(false))
				.map(Class::getGenericSuperclass)
				.filter(x -> ParameterizedType.class.isAssignableFrom(x.getClass()))
				.map(ParameterizedType.class::cast)
				.map(x -> x.getActualTypeArguments()[0].getTypeName())
				.findFirst();
		
		configurable = configClassString.isPresent();
		
		configClass = 
			XT.rethrow(() ->
				(Class<C>)Class.forName(
					configClassString
						.orElse("java.lang.Object")));
	}
	
	/**
	 *
	 * @param input InputStream containing payload passed to the function
	 * @param output OutputStream for output
	 * @param context AWS Lambda context object
	 */
	public final synchronized void awsLambdaHandler(InputStream input, OutputStream output, Context context) {
		// Local vars
		boolean noOp = false;
		boolean refreshConfig = false;
			
		// Parse ARN
		Matcher arnMatcher = AWS_ARN_PATTERN.matcher(context.getInvokedFunctionArn());
		if (arnMatcher.matches()) {
			awsLambdaRegion = Region.getRegion(Regions.fromName(arnMatcher.group(1)));
			awsLambdaName = arnMatcher.group(3);
			awsLambdaAlias = StringUtils.defaultIfEmpty(arnMatcher.group(4), "Default");
		} else {
			// Fatal
			throw new RuntimeException("Bad ARN");
		}
		
		if (awsLambdaMethod == null)
			findAWSLambdaMethod();
		
		// Initialize log
		this.log = new FunctionLog(
			context.getLogger(),
			awsLambdaAlias,
			AWS_LAMBDA_INVOKE_COUNTER.computeIfAbsent(awsLambdaAlias, key -> new AtomicLong()).incrementAndGet(),
			context.getAwsRequestId(),
			context.getClass().equals(AWSLocalContext.class));
		
//		log.trace("Env", 
//			Pair.of("getFunctionConfigStorageBucket", getFunctionConfigStorageBucket()),
//			Pair.of("CONFIG_BUCKET", System.getProperty("CONFIG_BUCKET")));
//		
//		log.trace("Annotation",
//			Pair.of("awsLambdaAnnotation.annotationType()", awsLambdaAnnotation.annotationType()),
//			Pair.of("awsLambdaAnnotation.iamRole", awsLambdaAnnotation.iamRole()),
//			Pair.of("projectClass", projectClass.getSimpleName()),
//			Pair.of("awsLambdaAnnotation.memory", awsLambdaAnnotation.memory()));

		// If the param is not explicitly specified - assuming that input contains JSON to be mapped into config
		if (input != null && awsLambdaMethodParamClass == null) {
			try (InputStream in = input) {
				if (configurable) {
					configString = IOUtils.toString(in);
					config = OBJECT_MAPPER.readValue(configString, configClass);
					JsonNode altConfig = OBJECT_MAPPER.readTree(configString);

					if (altConfig.hasNonNull("noOp"))
						noOp = altConfig.get("noOp").asBoolean();

					if (altConfig.hasNonNull("refreshConfig"))
						refreshConfig = altConfig.get("refreshConfig").asBoolean();
				} else {
					config = null;
					JsonNode altConfig = OBJECT_MAPPER.readTree(in);

					if (altConfig.hasNonNull("noOp"))
						noOp = altConfig.get("noOp").asBoolean();

					if (altConfig.hasNonNull("refreshConfig"))
						noOp = altConfig.get("refreshConfig").asBoolean();
				}

			} catch (IOException ex) {
				throw new RuntimeException("Could not read payload InputStream to JSON", ex);
			}
		} //else if (InputStream.class.isAssignableFrom(awsLambdaMethodParamClass)) {
				// Input is a raw InputStream to be consumed by the application
			//}
//			else {
//				// Parse input as JSON to be passed to the function code ToDo!!! var!
//				try (InputStream in = input) {
//					OBJECT_MAPPER.readValue(in, awsLambdaMethodParamClass);
//				} catch (IOException ex) {
//					throw new RuntimeException(
//						String.format(
//							"Could not read payload InputStream as JSON coercing into %s",
//							awsLambdaMethodParamClass.getSimpleName()),
//						ex);
//				}

		if (configurable && getFunctionConfigStorageBucket() != null && (refreshConfig || !CACHED_CONFIG.containsKey(awsLambdaAlias))) {
			// Try to load config
			AmazonS3 s3Client;
			
			// Create S3 client based on whether an encryption key was specified or not
			if (getFunctionConfigEncryptionKey() != null)
				s3Client = S3.getAmazonS3EncryptionClient(awsLambdaRegion, getFunctionConfigEncryptionKey());
			else
				s3Client = S3.getAmazonS3Client(awsLambdaRegion);
			
			final String s3Key = getFunctionConfigPath();
			final String s3Bucket = getFunctionConfigStorageBucket();
			
			try (InputStream configIn = s3Client
							.getObject(s3Bucket, s3Key)
							.getObjectContent()) {
				CACHED_CONFIG
					.put(
						awsLambdaAlias,
						OBJECT_MAPPER
							.readValue(configIn, configClass));
				
				log.log(
					"CNFG",
					"Read config from S3",
					Pair.of("bucket", s3Bucket),
					Pair.of("key", s3Key),
					Pair.of("refreshConfig", refreshConfig));
			} catch (AmazonS3Exception | IOException ex) {
				log.fatal(
					"Could not read config from S3",
					Pair.of("bucket", s3Bucket),
					Pair.of("key", s3Key),
					Pair.of("refreshConfig", refreshConfig),
					Pair.of("exception", ex));
				throw new RuntimeException("Could not read config from S3", ex);
			}
		}

		// NOOP
		if (noOp) {
			log.log("NOOP", "No Operation");
			return;
		}
		
		// Merge config
		if (CACHED_CONFIG.containsKey(awsLambdaAlias))
			try {
				config =
					OBJECT_MAPPER
						.readerForUpdating(
							// Make a copy of the config to merge
							OBJECT_MAPPER
								.readValue(
									OBJECT_MAPPER
										.writeValueAsBytes(CACHED_CONFIG.get(awsLambdaAlias)),
									configClass))
						.readValue(configString);
			} catch (IOException ex) {
				throw new RuntimeException("Could not merge config", ex);
			}

		// Dynamic invoke
		long startMillis = System.currentTimeMillis();
		log.log("START", "Lambda Code");
		
		try {
			Object invokeResult;
			
			if (awsLambdaMethodParamClass == null)
				invokeResult = awsLambdaMethod.invoke(this);
			else if (InputStream.class.isAssignableFrom(awsLambdaMethodParamClass))
				invokeResult = awsLambdaMethod.invoke(this, new Object[]{input});
			else
				try (InputStream in = input) {
					invokeResult = awsLambdaMethod
						.invoke(this,
							new Object[]{OBJECT_MAPPER.readValue(in, awsLambdaMethodParamClass)});
				} catch (IOException ex) {
					throw new RuntimeException(
						String.format(
							"Could not read payload InputStream as JSON coercing into %s",
							awsLambdaMethodParamClass.getSimpleName()),
						ex);
				}
			
			// Lambda return value
			if (invokeResult != null && output != null) {
				// ToDo: figure out how to work a raw stream - probably 2nd param to invoke, or make stream accessible to function like config
				//if (OutputStream.class.isAssignableFrom(output.getClass()))
				try {
					OBJECT_MAPPER.writeValue(output, invokeResult);
				} catch (IOException ex) {
					throw new RuntimeException(
						String.format(
							"Could not write output of the function to OutputStream as JSON converting from %s",
							awsLambdaMethodReturnParamClass.getSimpleName()),
						ex);
				} 
			}
		} catch (IllegalAccessException ex) {
			// Fatal - no access
			if (context instanceof AWSLocalContext) {
				throw new RuntimeException(ex);
			} else {
				log.error("Failed attempting to execute",
					Pair.of("exception", ex),
					Pair.of("duration",
						DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - startMillis)));
			}
		} catch (InvocationTargetException ex) {
			// Exception from inside
			if (context instanceof AWSLocalContext || ex.getCause() instanceof PassThroughException) {
				throw new RuntimeException(ex.getCause());
			}
			log.error("Failed during execution",
				Pair.of("exception", ex.getCause()),
				Pair.of("duration",
					DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - startMillis)));
		}
		
		log.log("END", "Lambda Code",
			Pair.of("duration",
				DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - startMillis)));
	}

	private void findAWSLambdaMethod() {
		// Find entry-point
		List<Method> annotatedMethods = Arrays
			.stream(this.getClass().getMethods())
			.filter(method -> method.isAnnotationPresent(AWSLambda.class))
			.collect(Collectors.toList());
		
		if (annotatedMethods.isEmpty())
			throw new RuntimeException("Did not find a method marked with @AWSLambda annotation!");
		
		if (annotatedMethods.size() > 1)
			throw new RuntimeException("Found multiple methods marked with @AWSLambda annotation!");
		
		// Cache handler info
		awsLambdaMethod = annotatedMethods.get(0);
		awsLambdaMethodParamClass = awsLambdaMethod.getParameterCount() > 0 ? awsLambdaMethod.getParameterTypes()[0] : null;
		awsLambdaMethodReturnParamClass = awsLambdaMethod.getReturnType();
		
		// Merge annotations starting with method and going up through container class hierarchy
		awsLambdaAnnotation =
			StreamSupport
			.stream(
				ClassUtils
					.hierarchy(this.getClass())
					.spliterator(), false)
			.filter(clazz -> clazz.isAnnotationPresent(AWSLambda.class))
			.map(clazz -> clazz.getAnnotation(AWSLambda.class))
			.reduce(awsLambdaMethod.getAnnotation(AWSLambda.class), (a, b) -> new AWSLambda() {
				@Override
				public AWSLambda.AWSLambdaMemory memory() {
					return a.memory() != AWSLambda.AWSLambdaMemory.DEFAULT ? a.memory() : b.memory();
				}

				@Override
				public String iamRole() {
					return StringUtils.defaultIfEmpty(a.iamRole(), b.iamRole());
				}
				
				@Override
				public Class<?> projectClass() {
					return a.projectClass().equals(Object.class) ? b.projectClass() : a.projectClass();
				}

				@Override
				public Class<? extends Annotation> annotationType() {
					return a.annotationType();
				}
			});
		
		// Determine project class - explicitly set on annotation or the first class extending FunctionContainer
		if (awsLambdaAnnotation.projectClass().equals(Object.class))
			projectClass = StreamSupport
				.stream(
					ClassUtils
						.hierarchy(this.getClass())
						.spliterator(), false)
				.filter(x -> Optional.ofNullable(x.getSuperclass()).map(y -> y.equals(FunctionContainer.class)).orElse(false))
				.findFirst()
				.get();
		else
			projectClass = awsLambdaAnnotation.projectClass();
	}
}
