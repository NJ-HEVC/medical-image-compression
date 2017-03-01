package com.cmput414w17.medical;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.cmput414w17.medical.image.ImageUtils;
import com.cmput414w17.medical.image.UnsupportedFileTypeException;

public class TestDicomImageConversion {

	@Test
	public void testDicomToJpeg() {
		testDicomToImage(new File("test.jpeg"));
	}

	@Test
	public void testDicomToPng() {
		testDicomToImage(new File("test.png"));
	}

	private void testDicomToImage(File output) {
		File dicomTestFile = new File(this.getClass().getResource("test.dcm").getFile());
		output.deleteOnExit();

		if (output.exists()) {
			output.delete();
		}

		try {
			ImageUtils.convertDicomToImage(dicomTestFile, output);
		} catch (IOException | UnsupportedFileTypeException e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertTrue(output.exists() && !output.isDirectory());
	}

}
