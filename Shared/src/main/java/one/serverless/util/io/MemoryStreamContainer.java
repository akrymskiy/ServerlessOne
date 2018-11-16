/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.util.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author akrymskiy
 */
public class MemoryStreamContainer {
	// In-Memory stream
	protected final ByteArrayOutputStream byteOutputStream;
	
	// Wrapping stream
	protected final CountingOutputStream wrapperOutputStream;
	
	public MemoryStreamContainer() throws IOException {
		this(BZip2CompressorOutputStream.class);
	}
	
	public MemoryStreamContainer(int initSize) throws IOException {
		this(BZip2CompressorOutputStream.class, initSize);
	}
	
	public MemoryStreamContainer(Class<? extends OutputStream> wrapperOutputStreamClazz) throws IOException {
		this(wrapperOutputStreamClazz, 128);
	}
	
	public MemoryStreamContainer(Class<? extends OutputStream> wrapperOutputStreamClazz, int initSize) throws IOException {
		this.byteOutputStream = new ByteArrayOutputStream(initSize);
		OutputStream wrapperOutputStream;
		
		if (wrapperOutputStreamClazz.equals(BZip2CompressorOutputStream.class)) {
			wrapperOutputStream = new BZip2CompressorOutputStream(byteOutputStream);
		} else if (wrapperOutputStreamClazz.equals(GZIPOutputStream.class)) {
			wrapperOutputStream = new GZIPOutputStream(byteOutputStream);
		} else {
			wrapperOutputStream = new BufferedOutputStream(byteOutputStream);
		}
		
		this.wrapperOutputStream = new CountingOutputStream(wrapperOutputStream);
	}

	public OutputStream getOutputStream() {
		return wrapperOutputStream;
	}
	
	public int getBufferSize() {
		return byteOutputStream.size();
	}
	
	public long getBytesWritten() {
		return wrapperOutputStream.getBytesWritten();
	}
	
	public InputStream getInputStream() {
		
		return byteOutputStream.toByteArrayInputStream();
	}
	
	public String getRecommendedExtension() {
		if (wrapperOutputStream.getWrappedStreamClass().equals(BZip2CompressorOutputStream.class))
			return "bz2";
		else if (wrapperOutputStream.getWrappedStreamClass().equals(GZIPOutputStream.class))
			return "gz";
		else
			return null;
	}
	
	public String getRecommendedDotExtension() {
		return Optional
			.ofNullable(getRecommendedExtension())
			.map(x -> "." + x)
			.orElse(StringUtils.EMPTY);
	}
}
