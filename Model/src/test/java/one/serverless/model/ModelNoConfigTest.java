/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package one.serverless.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import one.serverless.core.FunctionConfig;
import one.serverless.core.FunctionContainer;
import one.serverless.core.local.AWSLocalContext;
import org.junit.Test;

/**
 *
 * @author akrymskiy
 */
public class ModelNoConfigTest {
	@Test
	public void testMain() throws InstantiationException, IllegalAccessException, IOException {
		Class<ModelNoConfig> lambdaClazz = ModelNoConfig.class;

		lambdaClazz
			.newInstance()
			.awsLambdaHandler(
				new ByteArrayInputStream("{}".getBytes()),
				null,
				new AWSLocalContext("Model_ModelNoConfig"));
	}
}
