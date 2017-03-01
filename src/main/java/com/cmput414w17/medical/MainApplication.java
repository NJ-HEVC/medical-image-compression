package com.cmput414w17.medical;

import nu.pattern.OpenCV;

public class MainApplication {
	static {
		OpenCV.loadShared();
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {

	}
}
