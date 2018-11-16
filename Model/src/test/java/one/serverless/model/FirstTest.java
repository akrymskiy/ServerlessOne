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
public class FirstTest {
	@Test
	public void testMain() throws InstantiationException, IllegalAccessException, IOException {
		Class<? extends FunctionContainer<? extends FunctionConfig>> lambdaClazz = First.class;

		lambdaClazz
			.newInstance()
			.awsLambdaHandler(
				new ByteArrayInputStream("{\"noOp\": true}".getBytes()),
				null,
				new AWSLocalContext("Model_First"));
	}
}
