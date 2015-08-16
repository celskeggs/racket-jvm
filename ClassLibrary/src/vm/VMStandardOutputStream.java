package vm;

import java.io.IOException;
import java.io.OutputStream;

public class VMStandardOutputStream extends OutputStream {

	private final boolean isError;
	
	public VMStandardOutputStream(boolean isError) {
		this.isError = isError;
	}

	public void write(int b) throws IOException {
		write(new byte[] { (byte) b });
	}

	public void write(byte[] b) throws IOException {
		if (isError) {
			VMSandbox.writeStandardError(b);
		} else {
			VMSandbox.writeStandardOutput(b);
		}
	}

	public void write(byte[] b, int start, int count) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		}
		if (start < 0 || count < 0 || start + count > b.length) {
			throw new IndexOutOfBoundsException();
		}
		if (start == 0 && count == b.length) {
			write(b);
		} else {
			byte[] out = new byte[count];
			for (int i = 0; i < count; i++) {
				out[i] = b[start + i];
			}
			write(out);
		}
	}
}
