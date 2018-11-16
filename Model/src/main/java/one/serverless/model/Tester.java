/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package one.serverless.model;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 *
 * @author avk
 */
public class Tester {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		//AWSS - export/2018/10/29/6cdd7b1fed52a28fb7b305d48ee0b023
		
		
		for (int i = 1; i < 47; i ++) {
			//System.out.printf("public static final int M%1$05d = %1$d;\n", i);
			System.out.printf("M%1$05d(%1$d), ", (i + 1) * 64);
			if (i % 6 == 0)
				System.out.println("");
		}
		if (true)
			return;
		Stream
			.of(
				System.getProperty("CONFIG_S3BUCKET"),
				System.getenv("CONFIG_S3BUCKET"))
			.filter(Objects::nonNull)
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Unable to get config storage location"));
	}
	
}
