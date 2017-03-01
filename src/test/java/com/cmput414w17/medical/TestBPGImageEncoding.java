package com.cmput414w17.medical;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import com.cmput414w17.medical.image.ImageUtils;
import com.cmput414w17.medical.image.UnsupportedFileTypeException;

public class TestBPGImageEncoding {

	@Test
	public void testJpegToBpgOnWindows() {
		testImageToBpgOnWindows(new File("test.jpg"));
	}

	@Test
	public void testPngToBpgOnWindows() {
		testImageToBpgOnWindows(new File("test.png"));
	}

	private void testImageToBpgOnWindows(File output) {
		Assume.assumeTrue(System.getProperty("os.name").toLowerCase().startsWith("win"));

		File dicomTestFile = new File(this.getClass().getResource("test.dcm").getFile());
		File bpgTestFile = new File("test.bpg");

		output.deleteOnExit();
		bpgTestFile.deleteOnExit();

		if (bpgTestFile.exists()) {
			bpgTestFile.delete();
		}

		Assert.assertFalse(bpgTestFile.exists());

		try {
			ImageUtils.convertDicomToImage(dicomTestFile, output);
		} catch (IOException | UnsupportedFileTypeException e) {
			e.printStackTrace();
			Assert.fail();
		}

		try {
			ImageUtils.convertImageToBpg(output, bpgTestFile);
		} catch (IOException | InterruptedException | UnsupportedFileTypeException e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertTrue(bpgTestFile.exists());
	}
}
