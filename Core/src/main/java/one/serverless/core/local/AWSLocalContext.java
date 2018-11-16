/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.core.local;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.time.Instant;
import java.util.UUID;
import one.serverless.core.FunctionContainer;

/**
 *
 * @author akrymskiy
 */
public class AWSLocalContext implements Context {

	protected final String functionName;
    protected final String alias;
    protected final String requestId = UUID.randomUUID().toString();
	private final Instant startTime = Instant.now();
	
	public AWSLocalContext(String functionName) {
		this(functionName, "Default");
	}
	
	public AWSLocalContext(String functionName, String alias) {
		this.functionName = functionName;
		this.alias = alias;
	}
	
	// TODO
//	public AWSLocalContext(Class<? extends FunctionContainer<?>> functionClass, String alias) {
//		functionClass;
//	}
	
	@Override
	public String getAwsRequestId() {
		return "00000000-0000-0000-0000-000000000000";
	}

	@Override
	public String getLogGroupName() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getLogStreamName() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getFunctionName() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getFunctionVersion() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
    public String getInvokedFunctionArn() {
        return String.format(
                "arn:aws:lambda:us-east-1:000000000000:function:%s:%s",
                functionName,
                alias);
    }

	@Override
	public CognitoIdentity getIdentity() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ClientContext getClientContext() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getRemainingTimeInMillis() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getMemoryLimitInMB() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public LambdaLogger getLogger() {
		return new AWSLocalLogger();
	}
}
