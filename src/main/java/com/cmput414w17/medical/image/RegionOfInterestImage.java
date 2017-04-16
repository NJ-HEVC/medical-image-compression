package com.cmput414w17.medical.image;

import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 * Describes an image in two components: the region of interest (typically, the
 * specimen), and the non-region of interest or otherwise the region that can be
 * disregarded.
 *
 * @author David Yee
 */
public class RegionOfInterestImage {
	private Mat original;

	private Mat roi;
	private Mat nonRoi;

	public RegionOfInterestImage(Mat image) {
		this.original = image;

		roi = OpenCvUtils.doBackgroundRemovalFloodFill(image, new Point(0, 0));
		nonRoi = OpenCvUtils.getFloodFilledRegion(image, roi);
	}

	public Mat getOriginal() {
		return original;
	}

	public Mat getRoi() {
		return roi;
	}

	public Mat getNonRoi() {
		return nonRoi;
	}

}
