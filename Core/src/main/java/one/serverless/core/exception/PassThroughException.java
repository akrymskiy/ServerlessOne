/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.core.exception;

/**
 *
 * @author akrymskiy
 */
public class PassThroughException extends RuntimeException {

	public PassThroughException(String message) {
		super(message);
	}

	public PassThroughException(String message, Throwable cause) {
		super(message, cause);
	}

	public PassThroughException(Throwable cause) {
		super(cause);
	}
}
