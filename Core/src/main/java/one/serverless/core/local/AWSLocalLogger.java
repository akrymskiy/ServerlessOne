/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */
package one.serverless.core.local;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 *
 * @author akrymskiy
 */
public class AWSLocalLogger implements LambdaLogger {
	@Override
	public void log(String message) {
		System.out.println(message);
	}

	@Override
	public void log(byte[] message) {
		log(new String(message));
	}	
}
