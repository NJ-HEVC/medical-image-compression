package com.cmput414w17.medical;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

import com.cmput414w17.medical.image.ImageUtils;
import com.cmput414w17.medical.image.OpenCvUtils;
import com.cmput414w17.medical.image.UnsupportedFileTypeException;

import nu.pattern.OpenCV;

public class TestImageProcessing {

	static {
		OpenCV.loadShared();
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
	}

	@Test
	public void testFloodFill() throws IOException, UnsupportedFileTypeException {
		File testFile = new File(getClass().getResource("test2.jpg").getFile());
		File output = new File("floodfill.jpg");

		//output.deleteOnExit();

		//if (output.exists())
			//output.delete();

		Mat frame = Imgcodecs.imread(testFile.getAbsolutePath());

		frame = OpenCvUtils.doBackgroundRemovalFloodFill(frame, new Point(0, 0));

		Imgcodecs.imwrite(output.getAbsolutePath(), frame);

		if (!output.exists())
			Assert.fail(String.format("%s output file not found!", output.getName()));
	}
	@Test
	public void testOrganizor() throws IOException{
		ImageUtils.organizeInput();
	}

}
