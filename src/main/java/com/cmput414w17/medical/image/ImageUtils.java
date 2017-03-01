package com.cmput414w17.medical.image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.dcm4che3.tool.dcm2jpg.Dcm2Jpg;

/**
 * A series of image processing utility functions.
 * 
 * @author David Yee
 *
 */
public class ImageUtils {

	/**
	 * Converts a DICOM (.dcm) image into the JPEG or PNG format.
	 * 
	 * @param dicom
	 *            The DICOM input image.
	 * @param output
	 *            The file to output the JPEG or PNG encoded image.
	 * @throws IOException
	 *             Thrown if there was an I/O error while encoding the image to
	 *             the JPEG or PNG format.
	 * @throws UnsupportedFileTypeException
	 *             Thrown if the input image is not a JPEG or PNG.
	 */
	public static void convertDicomToImage(File dicom, File output) throws IOException, UnsupportedFileTypeException {
		String fileType = Files.probeContentType(output.toPath());
		boolean isPng = fileType.equals("image/png");
		boolean isJpeg = fileType.equals("image/jpeg");
		if (!(isPng || isJpeg)) {
			throw new UnsupportedFileTypeException(String.format(
					"The output image had a mimetype of %s but only PNG and JPEG images are supported for BPG encoding!",
					fileType));
		}

		Dcm2Jpg dcm2Jpg = new Dcm2Jpg();

		final double quality = 1.0;
		assert (quality >= 0.0 && quality <= 1.0);

		if (isJpeg) {
			dcm2Jpg.initImageWriter("JPEG", ".jpg", null, null, quality);
		} else if (isPng) {
			// PNG is lossless
			dcm2Jpg.initImageWriter("PNG", ".png", null, null, null);
		}

		dcm2Jpg.convert(dicom, output);
	}

	/**
	 * Converts a given JPEG or PNG file into BPG.
	 * 
	 * @param image
	 *            The JPEG or PNG input image.
	 * @param output
	 *            The file to output the BPG encoded image.
	 * @throws IOException
	 *             Thrown if there was an I/O error while encoding the image to
	 *             the BPG format.
	 * @throws InterruptedException
	 *             Thrown if there was a problem performing a call to the BPG
	 *             encoder for the respective operating system.
	 * @throws UnsupportedFileTypeException
	 *             Thrown if the input image is not a JPEG or PNG.
	 */
	public static void convertImageToBpg(File image, File output)
			throws IOException, InterruptedException, UnsupportedFileTypeException {
		String fileType = Files.probeContentType(image.toPath());
		if (!(fileType.equals("image/png") || fileType.equals("image/jpeg"))) {
			throw new UnsupportedFileTypeException(String.format(
					"The input image had a mimetype of %s but only PNG and JPEG images are supported for BPG encoding!",
					fileType));
		}

		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			// Runs on Windows only!
			Process p = Runtime.getRuntime().exec(String.format("bin/bpg/win64/bpgenc.exe -o %s %s",
					output.getAbsolutePath(), image.getAbsolutePath()));
			p.waitFor();
		} else {
			throw new UnsupportedOperationException("This operating system is not supported for BPG conversion!");
		}
	}

}
