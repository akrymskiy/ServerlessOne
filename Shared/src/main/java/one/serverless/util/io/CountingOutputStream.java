/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author akrymskiy
 */
public class CountingOutputStream extends FilterOutputStream {

	private long bytesWritten = 0;
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
		bytesWritten += len;
	}

	@Override
	public void write(byte[] b) throws IOException {
		super.write(b);
		bytesWritten += b.length;
	}

	@Override
	public void write(int b) throws IOException {
		super.write(b);
		bytesWritten++;
	}
	
	public CountingOutputStream(OutputStream out) {
		super(out);
	}

	public long getBytesWritten() {
		return bytesWritten;
	}
	
	public Class<? extends OutputStream> getWrappedStreamClass() {
		return this.out.getClass();
	}
}
