package saliencydetector;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import steerablepyramid.SteerablePyramid;

public class SubBandSaliencyDetector extends SaliencyDetector{

	public SubBandSaliencyDetector(int r, int c) {
		super(r, c);
		// TODO Auto-generated constructor stub
	}

	public SubBandSaliencyDetector(Mat input)
	{
		super(input);
	}
	
	public SubBandSaliencyDetector(Mat input, Mat output)
	{
		super(input, output);
	}
	
	@Override
	public void run()
	{
		SteerablePyramid pyr=new SteerablePyramid(img, SteerablePyramid.maxPyrHt(img.size(), new Size(15, 15))-1, "sp3Filters", "reflect1");
		sal=new Mat(img.height()*img.width(), pyr.pind.size()-1, CvType.CV_32F);
		for (int b=0; b<pyr.pind.size()-1; b++)
		{
		}
	}
}
