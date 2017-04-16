package com.cmput414w17.medical;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

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

		// Based on code written by
		// bobince (http://stackoverflow.com/users/18936/bobince)
		// From http://stackoverflow.com/a/7619091/2557554 and licensed under
		// CC-BY-SA 3.0 (https://creativecommons.org/licenses/by-sa/3.0/deed.en)
		ImageWriter writer = (ImageWriter) ImageIO.getImageWritersByFormatName("jpeg").next();
		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(1);
		writer.setOutput(ImageIO.createImageOutputStream(output));
		writer.write(null, new IIOImage(ImageIO.read(input), null, null), param);
	}

	private void jpegToImage(String format) throws IOException {
		File input = new File(getClass().getResource("test2.jpg").getFile());
		BufferedImage bufferedImage = ImageIO.read(input);
		File output = new File(FilenameUtils.removeExtension(input.getName()) + "." + format);

		output.deleteOnExit();

		if (output.exists())
			output.delete();

		ImageUtils.convertImageToFormat(bufferedImage, output, format);

		if (!output.exists())
			Assert.fail(String.format("%s output file not found!", output.getName()));
	}
}
