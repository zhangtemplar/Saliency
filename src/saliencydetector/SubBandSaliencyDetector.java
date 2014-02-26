package saliencydetector;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import steerablepyramid.SteerablePyramid;

public class SubBandSaliencyDetector extends SaliencyDetector{
	private SteerablePyramid pyr;
	private ArrayList<Mat> bands;
	
	public SubBandSaliencyDetector(int r, int c) {
		super(r, c);
		// TODO Auto-generated constructor stub
	}

	public SubBandSaliencyDetector(Mat input)
	{
		super(input);
	}
	
	private SubBandSaliencyDetector(Mat input, Mat output)
	{
		super(input, output);
	}
	
	public ArrayList<Mat> getSubBands()
	{
		return bands;
	}
	
	public ArrayList<Mat> getPyramid()
	{
		return pyr.pyr;
	}
	
	@Override
	public void run()
	{
		pyr=new SteerablePyramid(img, SteerablePyramid.maxPyrHt(img.size(), new Size(15, 15))-1, "sp3Filters", "reflect1");
		bands=new ArrayList<Mat>(pyr.pyr.size());
		/*for (Mat x: pyr.pyr)
		{
			Core.absdiff(x, new Scalar(0), x);
			Imgproc.GaussianBlur(x, x, new Size(6, 6), 2, 2, Imgproc.BORDER_REFLECT);
			Scalar sumX=Core.sumElems(x);
			Core.divide(x, sumX, x);
			Mat xResized=new Mat();
			Imgproc.resize(x, xResized, img.size());
			Core.add(sal, xResized, sal);
		}*/
		// make it parallel
		ArrayList<SubBand> threads=new ArrayList<SubBand>(pyr.pyr.size());
		for (int i=0; i<pyr.pyr.size(); i++)
		{
			Mat band=new Mat(pyr.pyr.get(i).size(), CvType.CV_32F);
			bands.add(band);
			SubBand thread=new SubBand(pyr.pyr.get(i), sal, bands.get(i));
			threads.add(thread);
			thread.start();
		}
		// wait to finish
		for (int i=0; i<threads.size(); i++)
		{
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class SubBand extends Thread
	{
		private Mat img;
		private Mat sal;
		private Mat tmp;
		
		public SubBand(Mat input, Mat output, Mat intemedia)
		{
			assert(input.height()==intemedia.height() && input.width()==intemedia.height());
			img=input;
			sal=output;
			tmp=intemedia;
		}
		public void run()
		{
			Core.absdiff(img, new Scalar(0), tmp);
			Imgproc.GaussianBlur(tmp, tmp, new Size(6, 6), 2, 2, Imgproc.BORDER_REFLECT);
			Scalar sumX=Core.sumElems(tmp);
			Core.divide(tmp, sumX, tmp);
			if (sal.height()!=tmp.height() || sal.width()!=tmp.width())
			{
				Mat resized=new Mat();
				Imgproc.resize(tmp, resized, sal.size());
				Core.add(sal, resized, sal);
			}
			else
			{
				Core.add(sal, tmp, sal);
			}
		}
	}
}
