package com.cmput414w17.medical.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.dcm4che3.tool.dcm2jpg.Dcm2Jpg;

/**
 * A series of image processing utility functions.
 * 
 * @author David Yee
 *
 */
public class ImageUtils {

	private static Logger LOGGER = Logger.getLogger(ImageUtils.class);

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
	 * @param lossless
	 *            True if lossless output is desired; otherwise, false for lossy
	 *            output.
	 * @throws IOException
	 *             Thrown if there was an I/O error while encoding the image to
	 *             the BPG format.
	 * @throws InterruptedException
	 *             Thrown if there was a problem performing a call to the BPG
	 *             encoder for the respective operating system.
	 * @throws UnsupportedFileTypeException
	 *             Thrown if the input image is not a JPEG or PNG.
	 */
	public static void convertImageToBpg(File image, File output, boolean lossless)
			throws IOException, InterruptedException, UnsupportedFileTypeException {
		String fileType = Files.probeContentType(image.toPath());
		if (!(fileType.equals("image/png") || fileType.equals("image/jpeg"))) {
			throw new UnsupportedFileTypeException(String.format(
					"The input image had a mimetype of %s but only PNG and JPEG images are supported for BPG encoding!",
					fileType));
		}

		if (SystemUtils.IS_OS_WINDOWS) {
			// Runs on Windows only!
			Process p = Runtime.getRuntime().exec(String.format("bin/bpg/win64/bpgenc.exe -o %s %s",
					output.getAbsolutePath(), image.getAbsolutePath()));
			p.waitFor();
		} else if (SystemUtils.IS_OS_LINUX) {
			Process p = Runtime.getRuntime()
					.exec(String.format("bpgenc -o %s %s", output.getAbsolutePath(), image.getAbsolutePath()));
			p.waitFor();
		} else {
			throw new UnsupportedOperationException("This operating system is not supported for BPG conversion!");
		}
	}

	/**
	 * Converts a given image into a PNG image.
	 * 
	 * @param image
	 *            The input image file.
	 * @param output
	 *            The PNG output image file.
	 * @throws IOException
	 *             Thrown if the input image could not be read or if the output
	 *             PNG file could not be written.
	 */
	public static void convertImageToPng(File image, File output) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(image);
		convertImageToPng(bufferedImage, output);
	}

	/**
	 * Converts a given image into a PNG image.
	 * 
	 * @param bufferedImage
	 *            The input image.
	 * @param output
	 *            The PNG output image file.
	 * @throws IOException
	 *             Thrown if the input image could not be read or if the output
	 *             PNG file could not be written.
	 */
	public static void convertImageToPng(BufferedImage bufferedImage, File output) throws IOException {
		convertImageToFormat(bufferedImage, output, "png");
	}

	/**
	 * Convert a given image into a given output format.
	 * 
	 * @param bufferedImage
	 *            The input image.
	 * @param output
	 *            The output image file.
	 * @param format
	 *            The format to convert the input image into.
	 * @throws IOException
	 *             Thrown if the input image could not be read or if the output
	 *             file could not be written.
	 */
	public static void convertImageToFormat(BufferedImage bufferedImage, File output, String format)
			throws IOException {
		ImageIO.write(bufferedImage, format, output);
	}

	public static void convertImageToJpeg2000(File image, File output) {

	}

	/**
	 * Organizes all the files in the given directory. Ignores sub-directories.
	 * 
	 * @param directory
	 *            The directory containing images.
	 * @throws IOException
	 *             Thrown if there was a problem reading the image or creating
	 *             new sub-directories.
	 */
	public static void organizeInput(File directory) throws IOException {

		final class ImageDimension {
			int width;
			int height;

			private ImageDimension(int width, int height) {
				this.width = width;
				this.height = height;
			}

			@Override
			public String toString() {
				return String.format("%dx%d", width, height);
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + height;
				result = prime * result + width;
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				ImageDimension other = (ImageDimension) obj;
				if (height != other.height)
					return false;
				if (width != other.width)
					return false;
				return true;
			}

		}

		File[] fList = directory.listFiles();

		if (fList == null)
			throw new IllegalArgumentException("The provided file path is not a valid directory!");

		// Organize the files into a map as per their image dimensions
		Map<ImageDimension, List<File>> imageDimensions = new HashMap<>();

		for (final File file : fList) {
			String imgName = file.getName();

			if (file.isDirectory()) {
				LOGGER.warn(String.format("Skipping files in directory '%s'", file.getName()));
				continue;
			}

			try {
				BufferedImage img = ImageIO.read(new File(directory, imgName));

				if (img != null) {
					int width = img.getWidth();
					int height = img.getHeight();

					ImageDimension imageMeta = new ImageDimension(width, height);

					if (imageDimensions.containsKey(imageMeta)) {
						imageDimensions.get(imageMeta).add(file);
					} else {
						List<File> filesList = new ArrayList<>();
						filesList.add(file);
						imageDimensions.put(imageMeta, filesList);
					}

					String folderName = width + "x" + height;
					File resFolder = new File("input/" + folderName);
					boolean exists = resFolder.exists();
					if (exists == false) {
						resFolder.mkdir();
						File imgFile = new File(resFolder + "/" + file.getName());
						file.renameTo(imgFile);
					} else {
						File imgFile = new File(resFolder + "/" + file.getName());
						file.renameTo(imgFile);
					}
				}

			} catch (IOException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
				throw e;
			}
		}

		// Iterate through each file, organized by their respective dimensions
		LOGGER.info("Image Size Distribution");
		for (Map.Entry<ImageDimension, List<File>> entry : imageDimensions.entrySet()) {
			ImageDimension imageDimension = entry.getKey();
			List<File> files = entry.getValue();

			Path targetDir = Paths.get(new File(directory, imageDimension.toString()).toURI());
			try {
				Files.createDirectories(targetDir);
			} catch (IOException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
				throw e;
			}

			for (File file : files) {
				File outputFile = new File(targetDir.toFile(), file.getName());
				Files.move(file.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}

			LOGGER.info(String.format("%s: %d", imageDimension.toString(), files.size()));
		}
	}

}
