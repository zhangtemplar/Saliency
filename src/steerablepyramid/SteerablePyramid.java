package steerablepyramid;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class SteerablePyramid {
	// class info
	private static final String TAG="SaliencyDetector:SteerablePyramid:SteerablePyramid";
	// filter
	private SpFilters filter;
	
	// output
	public ArrayList<Mat> pyr;
	public ArrayList<Size> pind;
	public Mat steermtx;
	public Scalar harmonics;
	
	public static int maxPyrHt(Size imsz, Size filtersz)
	{
		if ((int)imsz.height==1 || (int)imsz.width==1)
		{
			/**
			 * 1D image and 1D filter
			 */
			double imsz1=imsz.height*imsz.width;
			double filtersz1=filtersz.height*filtersz.width;
			if (imsz1<filtersz1)
			{
				return 0;
			}
			else
			{
				return 1+maxPyrHt(new Size(Math.floor(imsz.height/2), Math.floor(imsz.width/2)), filtersz);
			}
		}
		else if ((int)filtersz.height==1 || (int)filtersz.width==1)
		{
			/**
			 * 2D image and 1D filter
			 */
			double tmp=Math.max(filtersz.height, filtersz.width);
			filtersz.height=tmp;
			filtersz.width=tmp;
		}
		/**
		 * 2D image and 2D filter
		 */
		if (Math.min(imsz.height, imsz.width)<Math.max(filtersz.height, filtersz.width))
		{
			return 0;
		}
		else
		{
			return 1+maxPyrHt(new Size(Math.floor(imsz.height/2), Math.floor(imsz.width/2)), filtersz);
		}
	}
	
	/**
	 * the constructor
	 */
	public SteerablePyramid(Mat img, int ht, String filtfile, String edges)
	{
		assert(img.type()==CvType.CV_32F);
		
		/**
		 * check the filter type
		 */
		if (filtfile.equalsIgnoreCase("sp3filters"))
		{
			filter=SpFilters.Sp3Filters;
		}
		else
		{
			// filer type not yet implemented
			System.err.println(TAG+":SteerablePyramid:"+filtfile+" is not available");
			assert(false);
		}
		steermtx=filter.mtx;
		harmonics=filter.harmonics;
		
		/**
		 * check the levels of pyramid
		 */
		int max_ht=maxPyrHt(img.size(), new Size(filter.lo0filt.height(), 1));
		
		if (ht>max_ht)
		{
			System.err.println(TAG+"Cannot build pyramid higher than "+max_ht+" levels.");
			assert(false);
		}
		ht=max_ht;
		
		/**
		 * apply the filtering
		 */
		Mat hi0=new Mat(img.height(), img.width(), img.type());
		Imgproc.filter2D(img, hi0, -1, filter.hi0filt);
		Mat lo0=new Mat(img.height(), img.width(), img.type());
		Imgproc.filter2D(img, lo0, -1, filter.lo0filt);
		pyr.add(hi0);
		pind.add(hi0.size());
		
		/**
		 * for the other levels
		 */
		buildSpyr(filter.lo0filt, ht, filter.lofilt, filter.bfilts, edges, pyr, pind);
	}
	
	private void buildSpyr(Mat lo0, int ht, Mat lofilt, Mat bfilts, String edges, ArrayList<Mat> pyr, ArrayList<Size> pind)
	{
		if (ht<=0)
		{
			pyr.add(lo0);
			pind.add(lo0.size());
		}
		else
		{
			int bfiltsz=(int) Math.round(Math.sqrt(bfilts.height()));
			/*Mat bands=new Mat(lo0.height()*lo0.width(), bfilts.width(), CvType.CV_32F);
			Mat bind=new Mat(bfilts.width(), 2, CvType.CV_32F);*/
			for (int b=0; b<bfilts.width(); b++)
			{
				Mat filt=bfilts.col(b);
				filt.reshape(bfiltsz);
				Mat band=new Mat(filt.height(), filt.width(), filt.type());
				Imgproc.filter2D(lo0, band, -1, filt);
				pyr.add(band);
				pind.add(band.size());
			}
			Mat band=new Mat(lo0.height(), lo0.width(), lo0.type());
			Imgproc.filter2D(lo0, band, -1, lofilt);
			Mat lo=new Mat();
			Imgproc.resize(band, lo, new Size(), 0.5, 0.5, Imgproc.INTER_NEAREST);
			buildSpyr(lo, ht-1, lofilt, bfilts, edges, pyr, pind);
		}
	}
}
