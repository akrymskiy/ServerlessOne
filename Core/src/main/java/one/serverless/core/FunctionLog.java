/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.core;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import static one.serverless.core.FunctionContainer.OBJECT_MAPPER;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author akrymskiy
 */
public class FunctionLog {
	private final LambdaLogger awsLambdaLogger;
	private final String awsLambdaAlias;
	private final long invokeNumber;
	private final String awsLambdaRequestId;
	private final ObjectWriter jsonLogWriter;
	
	public FunctionLog(LambdaLogger awsLambdaLogger, String awsLambdaAlias, long invokeNumber, String awsLambdaRequestId, boolean prettyPrint) {
        this.awsLambdaLogger = awsLambdaLogger;
        this.invokeNumber = invokeNumber;
        this.awsLambdaAlias = awsLambdaAlias;
        this.awsLambdaRequestId = awsLambdaRequestId;
		this.jsonLogWriter = prettyPrint ? OBJECT_MAPPER.writer(new DefaultPrettyPrinter() {
				private static final long serialVersionUID = 1L;
				{
					// customize JSON array output
					_arrayIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
				}
			}) : OBJECT_MAPPER.writer();
		
    }
	
	@SafeVarargs
    public final void log(String severity, String message, Pair<String, Object>... extraPayload) {
		// Prepare JSON for logging
		ObjectNode logObjectNode =
			OBJECT_MAPPER
				.getNodeFactory()
				.objectNode()
				.put("severity", severity)
				.put("invokeNumber", invokeNumber)
				.put("alias", awsLambdaAlias)
				.put("message", message)
				.put("requestId", awsLambdaRequestId);
		
		// Add extra payload if passed
        if (extraPayload.length > 0)
            logObjectNode.set("extra",
				OBJECT_MAPPER
					.valueToTree(
						Arrays
							.stream(extraPayload)
							//.sequential()
							.map(extraPair ->
								(extraPair.getValue() != null) && (extraPair.getValue() instanceof Throwable) ?
									// Special handling for Throwables
									Pair.of(
										extraPair.getKey(),
										new LinkedHashMap<String, Object>() {
											static final long serialVersionUID = -1;
											final Throwable ex = Throwable.class.cast(extraPair.getValue());
											{
												// Throwable info
												put("class", ex.getClass().getName());
												if (ex.getMessage() != null)
													put("message", ex.getMessage().split(StringUtils.LF));
												put("stackTrace",
													Arrays
														.stream(ex.getStackTrace())
														.map(stackFrame -> stackFrame.toString())
														.collect(Collectors.toList()));
												
												// Causing throwable info
												if (ex.getCause() != null) {
													Throwable exCause = ex.getCause();
													put("causeClass", exCause.getClass().getName());
													if (exCause.getMessage() != null)
														put("causeMessage", exCause.getMessage().split(StringUtils.LF));
													put("causeStackTrace",
														Arrays
															.stream(exCause.getStackTrace())
															.map(stackFrame -> stackFrame.toString())
															.collect(Collectors.toList()));
												}
											}}) :
									// Default pass-through
									extraPair)
							.collect(
								LinkedHashMap::new,
								(map, pair) -> map.put(pair.getKey(), pair.getValue()),
								HashMap::putAll)));
		try {
            synchronized (this) {
				awsLambdaLogger.log(jsonLogWriter.writeValueAsString(logObjectNode));
			}
            
        } catch (JsonProcessingException ex) {
            awsLambdaLogger
				.log(
                    String.format(
						"{\"severity\":\"FATAL\",\"message\":\"Unable to generate JSON log message\",\"exceptionRaw\":\"%s\"}",
                        ex.getMessage()));
			
            throw new RuntimeException("Unable to generate JSON log message", ex);
        }	
	}
	
	@SafeVarargs
	public final void info(String message, Pair<String, Object>... extraPayload) {
		this.log("INFO", message, extraPayload);
	}

	@SafeVarargs
	public final void warn(String message, Pair<String, Object>... extraPayload) {
		this.log("WARN", message, extraPayload);
	}

	@SafeVarargs
	public final void error(String message, Pair<String, Object>... extraPayload) {
		this.log("ERROR", message, extraPayload);
	}

	@SafeVarargs
	public final void trace(String message, Pair<String, Object>... extraPayload) {
		this.log("TRACE", message, extraPayload);
	}

	@SafeVarargs
	public final void fatal(String message, Pair<String, Object>... extraPayload) {
		this.log("FATAL", message, extraPayload);
	}
}
