package com.cmput414w17.medical;

import java.io.File;
import java.io.IOException;

import org.dcm4che3.tool.dcm2jpg.Dcm2Jpg;
import org.junit.Assert;
import org.junit.Test;

public class TestDicomImageConversion {

	@Test
	public void testDicomToJpeg() {
		File dicomTestFile = new File(this.getClass().getResource("test.dcm").getFile());
		File jpegTestFileOutput = new File("test.jpg");
		jpegTestFileOutput.deleteOnExit();

		try {
			Dcm2Jpg dcm2Jpg = new Dcm2Jpg();
			dcm2Jpg.initImageWriter("JPEG", ".jpg", null, null, 1.0);
			dcm2Jpg.convert(dicomTestFile, jpegTestFileOutput);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertTrue(jpegTestFileOutput.exists() && !jpegTestFileOutput.isDirectory());
	}

}
