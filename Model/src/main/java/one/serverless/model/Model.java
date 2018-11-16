/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.model;

import one.serverless.core.FunctionContainer;
import one.serverless.core.annotation.AWSLambda;
import static one.serverless.core.annotation.AWSLambda.AWSLambdaMemory.*;

/**
 *
 * @author akrymskiy
 */
@AWSLambda(memory = M00256, iamRole = "LambdaBasicExecution")
public abstract class Model extends FunctionContainer<ModelConfig> {
//	@Override
//	protected Class<ModelConfig> getFunctionConfigClass() {
//		return ModelConfig.class;
//	}
}
