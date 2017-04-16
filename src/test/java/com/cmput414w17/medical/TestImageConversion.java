package com.cmput414w17.medical;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;

import com.cmput414w17.medical.image.ImageUtils;

public class TestImageConversion {

	@Test
	public void testJpegToPng() throws IOException {
		jpegToImage("png");
	}

	@Test
	public void testJpegToJpeg2000() throws IOException {
		jpegToImage("jpeg2000");
	}

	@Test
	public void testJpegToJpegLossy() throws IOException {
		// Compression quality defaults to 0.75
		jpegToImage("jpeg");
	}

	@Test
	public void testJpegToJpegLossless() throws IOException {
		File input = new File(getClass().getResource("test2.jpg").getFile());
		File output = new File("test2lossless.jpeg");

		output.deleteOnExit();

		if (output.exists())
			output.delete();

		ImageUtils.convertImageToFormat(ImageIO.read(input), output, "jpeg", 1.0f);
	}

	private void jpegToImage(String format) throws IOException {
		File input = new File(getClass().getResource("test2.jpg").getFile());
		BufferedImage bufferedImage = ImageIO.read(input);
		File output = new File(FilenameUtils.removeExtension(input.getName()) + "." + format);

		output.deleteOnExit();

		if (output.exists())
			output.delete();

		ImageUtils.convertImageToFormat(bufferedImage, output, format, 0.75f);

		if (!output.exists())
			Assert.fail(String.format("%s output file not found!", output.getName()));
	}
}
