/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.model;

import one.serverless.core.annotation.AWSLambda;

/**
 *
 * @author akrymskiy
 */
public class First extends Model {
	@AWSLambda
	public void main() {
		System.out.println("----First----");
		System.out.println(config.myProp);
	}
}
