/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.util.io;

import java.io.ByteArrayInputStream;

/**
 *
 * @author akrymskiy
 */
public class ByteArrayOutputStream extends java.io.ByteArrayOutputStream {

	/**
	 * Method to expose internal byte buffer
	 * without making a copy like toByteArray() method does
	 * @return ByteArrayInputStream instance based on internal byte buffer of this ByteArrayOutputStream 
	 */
	public ByteArrayInputStream toByteArrayInputStream() {
		return new ByteArrayInputStream(buf, 0, count);
	}
	
	public ByteArrayOutputStream() {
		this(128);
	}
	
	public ByteArrayOutputStream(int initSize) {
		super(initSize);
	}
}
