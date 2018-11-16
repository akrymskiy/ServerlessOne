/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */
package one.serverless.util.xt;

/**
 * eXception Translator
 * @author akrymskiy
 */
public class XT {
	public static void rethrow(ThrowingRunnable codeToRun) {
		try {
			codeToRun.run();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static <T> T rethrow(ThrowingCallable<T> codeToCall) {	
		try {
			return codeToCall.call();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
