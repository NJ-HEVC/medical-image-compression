package com.cmput414w17.medical;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import com.cmput414w17.medical.image.ImageUtils;
import com.cmput414w17.medical.image.UnsupportedFileTypeException;

public class TestBPGImageEncoding {

	@Test
	public void testJpegToBpgOnWindows() {
		Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
		testImageToBpg(new File("test.jpg"));
	}

	@Test
	public void testJpegToBpgOnLinux() {
		Assume.assumeTrue(SystemUtils.IS_OS_LINUX);
		testImageToBpg(new File("test.jpg"));
	}

	@Test
	public void testPngToBpgOnWindows() {
		Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
		testImageToBpg(new File("test.png"));
	}

	@Test
	public void testPngToBpgOnLinux() {
		Assume.assumeTrue(SystemUtils.IS_OS_LINUX);
		testImageToBpg(new File("test.png"));
	}

	private void testImageToBpg(File output) {
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
			ImageUtils.convertImageToBpg(output, bpgTestFile, true);
		} catch (IOException | InterruptedException | UnsupportedFileTypeException e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertTrue(bpgTestFile.exists());
	}
}
