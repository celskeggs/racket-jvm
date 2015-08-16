package java.lang;

import java.util.Objects;

public class StackTraceElement {
	private final String declaringClass;
	private final String methodName;
	private final String fileName;
	private final int lineNumber;

	public StackTraceElement(String declaringClass, String methodName, String fileName, int lineNumber) {
		if (declaringClass == null || methodName == null) {
			throw new NullPointerException();
		}
		this.declaringClass = declaringClass;
		this.methodName = methodName;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getClassName() {
		return declaringClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public boolean isNativeMethod() {
		return lineNumber == -2;
	}

	private boolean hasLineNumber() {
		return lineNumber >= 1;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StackTraceElement) {
			StackTraceElement s = (StackTraceElement) o;
			return lineNumber == s.lineNumber && Objects.equals(fileName, s.fileName) && methodName.equals(methodName) && declaringClass.equals(declaringClass);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return declaringClass + "." + methodName + "(" + (isNativeMethod() ? "Native Method" : fileName == null ? "Unknown Source" : hasLineNumber() ? fileName + ":" + lineNumber : fileName) + ")";
	}
}
