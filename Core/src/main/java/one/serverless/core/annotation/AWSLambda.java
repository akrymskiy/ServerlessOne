/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author akrymskiy
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AWSLambda {
	public enum AWSLambdaMemory {
		DEFAULT(-1),
		M00128(128), M00192(192), M00256(256), M00320(320), M00384(384), M00448(448), 
		M00512(512), M00576(576), M00640(640), M00704(704), M00768(768), M00832(832), 
		M00896(896), M00960(960), M01024(1024), M01088(1088), M01152(1152), M01216(1216), 
		M01280(1280), M01344(1344), M01408(1408), M01472(1472), M01536(1536), M01600(1600), 
		M01664(1664), M01728(1728), M01792(1792), M01856(1856), M01920(1920), M01984(1984), 
		M02048(2048), M02112(2112), M02176(2176), M02240(2240), M02304(2304), M02368(2368), 
		M02432(2432), M02496(2496), M02560(2560), M02624(2624), M02688(2688), M02752(2752), 
		M02816(2816), M02880(2880), M02944(2944), M03008(3008);

		private final int memory;

		AWSLambdaMemory(int memory) {
			this.memory = memory;
		}

		public Integer getMemory() {
			return this.memory;
		}
	}
	
	public AWSLambdaMemory memory() default AWSLambdaMemory.DEFAULT;
	public String iamRole() default "";
	public Class<?> projectClass() default Object.class;
}
