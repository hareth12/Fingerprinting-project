package helperClasses;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 * Binarizer - attempts to binarize a fingerprint image. Requires further implementation to binarize correctly. 
 *
 */
public class Binarizer {
	public BufferedImage fingerprint;
	public ArrayList<double[][]> blocks;
	
	public Binarizer(BufferedImage fingerprint)
	{
		this.fingerprint = fingerprint;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("images/Chris2.jpg"));
		} catch (IOException e) {
		}
		Binarizer binarizer = new Binarizer(img);
		binarizer.imrotate();
		System.out.println("Binarization started");
		binarizer.rgb2gray();
		//Identify ridge-like regions and normalise image
		int blksze = 32;
		double thresh = 0.1;
		blksze = 32; thresh = 0.1;
		RidgeSegmentReturn rsr = binarizer.ridgesement(binarizer.fingerprint, blksze, thresh);
		//binarizer.fingerprint = rsr.getNormim();
		//Determine ridge orientations
		RidgeOrientReturn ror = binarizer.ridgeorient(rsr.getNormim(), 1, 7, 7);
		binarizer.fingerprint = ror.getOrientim();
		/*
		//Determine ridge frequency values across the image
		[freq, medfreq] = ridgefreq(rsr.getNormim(), rsr.getmask(), ror.getOrientim(), blksze, 11, 3, 15);

		freq = medfreq.*rsr.getMask();

		//Now apply filters to enhance the ridge pattern
		newim = ridgefilter(rsr.getNormim(), ror.getOrientim(), freq, 1, 0.5, 1);


		//Binarise, ridge/valley threshold is 0(for machine images)
		binim  = newim > 0;
		show(binim);*/
		try {
		    ImageIO.write(binarizer.fingerprint, "jpg", new File("images/out.jpg"));
		} catch (IOException e) {
			System.out.println("Error writing binarized image");
		}
		System.out.println("Binarization completed");
	}
	
	private void imrotate() {
		/*Rotate image 90 degrees */
	    AffineTransform transform = new AffineTransform();
	    transform.rotate(Math.PI/2, fingerprint.getWidth()/2, fingerprint.getHeight()/2);
	    AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
	    fingerprint = op.filter(fingerprint, null);
		return;
	}
	private void rgb2gray()
	{
		BufferedImage gray = new BufferedImage(fingerprint.getWidth(), fingerprint.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        // convert the original colored image to grayscale
        ColorConvertOp op = new ColorConvertOp(fingerprint.getColorModel().getColorSpace(),
        		gray.getColorModel().getColorSpace(),null);
        fingerprint = op.filter(fingerprint,gray);
        return;
	}
	
	/*% RIDGESEGMENT - Normalises fingerprint image and segments ridge region
	%
	% Function identifies ridge regions of a fingerprint image and returns a
	% mask identifying this region.  It also normalises the intesity values of
	% the image so that the ridge regions have zero mean, unit standard
	% deviation.
	%
	% This function breaks the image up into blocks of size blksze x blksze and
	% evaluates the standard deviation in each region.  If the standard
	% deviation is above the threshold it is deemed part of the fingerprint.
	% Note that the image is normalised to have zero mean, unit standard
	% deviation prior to performing this process so that the threshold you
	% specify is relative to a unit standard deviation.
	%
	% Usage:   [normim, mask, maskind] = ridgesegment(im, blksze, thresh)
	%
	% Arguments:   im     - Fingerprint image to be segmented.
	%              blksze - Block size over which the the standard
	%                       deviation is determined (try a value of 16).
	%              thresh - Threshold of standard deviation to decide if a
	%                       block is a ridge region (Try a value 0.1 - 0.2)
	%
	% Returns:     normim - Image where the ridge regions are renormalised to
	%                       have zero mean, unit standard deviation.
	%              mask   - Mask indicating ridge-like regions of the image, 
	%                       0 for non ridge regions, 1 for ridge regions.
	%              maskind - Vector of indices of locations within the mask. 
	%
	% Suggested values for a 500dpi fingerprint image:
	%
	%   [normim, mask, maskind] = ridgesegment(im, 16, 0.1)
	%
	% See also: RIDGEORIENT, RIDGEFREQ, RIDGEFILTER */
	public RidgeSegmentReturn ridgesement(BufferedImage im, int blksze, double thresh){
		im = normalise(im,0,1);  // normalise to have zero mean, unit std dev
	    
	    String fun = "std(x(:))*ones(size(x))";
	    int[][] vals = getPixelValues(im);
	    double[] stddevim = blkproc(vals, blksze);
	    int[] mask = new int[stddevim.length]; 
	    for(int x = 0; x < mask.length; x++)
	    {
	    	if(stddevim[x] > thresh)
	    		mask[x] = 1;
	    	else
	    		mask[x] = 0;
	    }

	    ArrayList<Integer> maskind = new ArrayList<Integer>();
	    for(int x = 0; x < mask.length; x++)
	    {
	    	if(mask[x] > 0)
	    		maskind.add(x);
	    }
	    // Renormalise image so that the *ridge regions* have zero mean, unit standard deviation.
	    /*im = im - mean(im(maskind));
	    normim = im/std(im(maskind));
	    RidgeSegmentReturn rsr = new RidgeSegmentReturn(normim, mask);*/
	    RidgeSegmentReturn rsr = new RidgeSegmentReturn(im, mask);
	    return rsr;
	}
	/**
	 * Break image into blocks, return standard deviation of each block
	 * @param vals
	 * @param width
	 * @param height
	 * @return
	 */
	public double[] blkproc(int[][] vals, int blksze){
		double[][] temp = new double[vals.length][vals[0].length];
		for(int x = 0; x < vals.length; x++)
		{
			for(int y = 0; y < vals[x].length; y++)
			{
				temp[x][y] = (double)vals[x][y];
			}
		}
		StandardDeviation sd = new StandardDeviation();
		double numBlocks = (vals.length * vals[0].length) / (blksze*blksze);
		blocks = new ArrayList<double[][]>();
		int curx = 0;
		int cury = 0;
		int totalWidth = temp.length;
		int totalHeight = temp[0].length;
		int nblocks = 0;
		int thisBlockWidth = 0;
		int thisBlockHeight = 0;
		if(curx + blksze >= totalWidth && cury + blksze >= totalHeight) //one blksze X blksze block is bigger or equal to image size 
		{
			blocks.add(temp);
		}
		else //at least one full block in image
		{
			while(nblocks < numBlocks)
			{
				thisBlockWidth = 0;
				thisBlockHeight = 0;
				if(totalWidth < (blksze + curx)) //block too wide
				{
					thisBlockWidth = totalWidth - curx;
				}
				else
				{
					thisBlockWidth = blksze;
				}
				if(totalHeight < (blksze + cury))//block too tall
				{
					thisBlockHeight = totalWidth - cury;
				}
				else
				{
					thisBlockHeight = blksze;
				}
				//Get a block
				double[][] block = new double[thisBlockWidth][thisBlockHeight];
				for(int x = 0; x < thisBlockWidth -1; x++)
				{
					for(int y = 0; y < thisBlockHeight - 1; y++)
					{
						block[x][y] = temp[x][y];
					}
				}
				blocks.add(block);
				curx = curx + thisBlockWidth;
				if(curx == totalWidth)
				{
					curx = 0;
					cury = cury+ thisBlockHeight;
				}
				if(cury == totalHeight)
				{
					break;
				}
			}
		}
		double[] results = new double[blocks.size()];
		int resultPos = 0;
		for(double[][] currBlock : blocks)
		{
			int blockWidth = currBlock.length;
			int blockHeight = currBlock[0].length;
			double[] blockContents = new double[blockWidth * blockHeight];
			int pos = 0;
			for(int xpos = 0; xpos < blockWidth; xpos++)
			{
				for(int ypos = 0; ypos < blockHeight; ypos++)
				{
					blockContents[pos] = currBlock[xpos][ypos];
					pos++;
				}
			}
			sd.clear();
			results[resultPos] = sd.evaluate(blockContents);
			resultPos++;
		}
		return results;
	}
	private class RidgeSegmentReturn{
		BufferedImage normim;
		int[] mask;
		public RidgeSegmentReturn(BufferedImage normim, int[] mask){
			this.normim = normim;
			this.mask = Arrays.copyOf(mask, mask.length);
		}
		public BufferedImage getNormim(){
			return this.normim;
		}
		public int[] getmask(){
			return this.mask;
		}
	}
	private static int[][] getPixelValues(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[][] result = new int[width][height];

		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
		    	result[col][row] = image.getRGB(col, row);
		    }
		}
		return result;
	}
	BufferedImage normalise(BufferedImage im, int reqmean, int reqvar){
			   // Normalise to desired mean and variance
				int[][] pixelValues = getPixelValues(im);
				int sum = 0;
				for(int x = 0; x < pixelValues.length; x++)
				{
					for(int y = 0; y < pixelValues[x].length; y++)
					{
						sum += pixelValues[x][y];
					}
				}
				int mean = sum / (pixelValues.length * pixelValues[0].length);
				for(int x = 0; x < pixelValues.length; x++)
				{
					for(int y = 0; y < pixelValues[x].length; y++)
					{
						pixelValues[x][y] = pixelValues[x][y] - mean;
					}
				}
				  
				int[] values = new int[pixelValues.length * pixelValues[0].length];
				int z = 0;
				for(int x = 0; x < pixelValues.length; x++)
				{
					for(int y = 0; y < pixelValues[x].length; y++)
					{
						values[z] = (pixelValues[x][y] - mean) * (pixelValues[x][y] - mean);
						z++;
					}
				}
				int valuesSum = 0;
				for(int x = 0; x < values.length; x++)
				{
					valuesSum += values[x];
				}
				double stddev = Math.sqrt(valuesSum / values.length);
				for(int x = 0; x < pixelValues.length; x++)
				{
					for(int y = 0; y < pixelValues[x].length; y++)
					{
						pixelValues[x][y] = (int) (pixelValues[x][y] - stddev);
					}
				}
				for(int x = 0; x < pixelValues.length; x++)
				{
					for(int y = 0; y < pixelValues[x].length; y++)
					{
						pixelValues[x][y] = (int) (reqmean + (pixelValues[x][y] * Math.sqrt(reqvar)));
					}
				}
				for(int x = 0; x < pixelValues.length; x++)
				{
					for(int y = 0; y < pixelValues[x].length; y++)
					{
						im.setRGB(x, y, pixelValues[x][y]);
					}
				}
		    	return im;
	}
	/*% RIDGEORIENT - Estimates the local orientation of ridges in a fingerprint
	%
	% Usage:  [orientim, reliability] = ridgeorientation(im, gradientsigma,...
	%                                             blocksigma, ...
	%                                             orientsmoothsigma)
	%
	% Arguments:  im                - A normalised input image.
	%             gradientsigma     - Sigma of the derivative of Gaussian
	%                                 used to compute image gradients.
	%             blocksigma        - Sigma of the Gaussian weighting used to
	%                                 sum the gradient moments.
	%             orientsmoothsigma - Sigma of the Gaussian used to smooth
	%                                 the final orientation vector field.
	% 
	% Returns:    orientim          - The orientation image in radians.
	%                                 Orientation values are +ve clockwise
	%                                 and give the direction *along* the
	%                                 ridges.
	%             reliability       - Measure of the reliability of the
	%                                 orientation measure.  This is a value
	%                                 between 0 and 1. I think a value above
	%                                 about 0.5 can be considered 'reliable'.
	%
	%
	% With a fingerprint image at a 'standard' resolution of 500dpi suggested
	% parameter values might be:
	%
	%    [orientim, reliability] = ridgeorient(im, 1, 3, 3);
	%
	% See also: RIDGESEGMENT, RIDGEFREQ, RIDGEFILTER
	*/

	public RidgeOrientReturn ridgeorient(BufferedImage im, int gradientsigma, int blocksigma, int orientsmoothsigma){
	     
		int rows = im.getHeight();
		int cols = im.getWidth();
	    
	    //% Calculate image gradients.
	    double sze = Math.floor(6*gradientsigma);   
	    if(sze % 2 == 0)
	    	sze = sze + 1;
	    GaussianFilter gf = new GaussianFilter((int)sze);//% Generate Gaussian filter.
	    BufferedImage gradientIm = null;
	    gradientIm = gf.filter( im, gradientIm );
	    //[fx,fy] = gradient(f);                        //% Gradient of Gaussian.
	    
	    //Gx = filter2(fx, im); //% Gradient of the image in x
	    //Gy = filter2(fy, im); //% ... and y
	    
	   // % Estimate the local ridge orientation at each point by finding the
	    //% principal axis of variation in the image gradients.
	   
	    //Gxx = Gx.^2;       //% Covariance data for the image gradients
	    //Gxy = Gx.*Gy;
	    //Gyy = Gy.^2;
	    
	    //% Now smooth the covariance data to perform a weighted summation of the
	   // % data.
	    sze = Math.floor(6*blocksigma);  
	    if(sze % 2 == 0)
	    	sze = sze + 1;  
	    
	    gf = new GaussianFilter((int)sze);//% Generate Gaussian filter.
	    BufferedImage blockIm = null;
	    blockIm = gf.filter( gradientIm, blockIm );
	    
	    //Gxx = filter2(f, Gxx); 
	    //Gxy = 2*filter2(f, Gxy);
	   // Gyy = filter2(f, Gyy);
	    
	   // % Analytic solution of principal direction
	    //denom = sqrt(Gxy.^2 + (Gxx - Gyy).^2) + eps;
	    //sin2theta = Gxy./denom;            //% Sine and cosine of doubled angles
	    //cos2theta = (Gxx-Gyy)./denom;
	       
	    sze = Math.floor(6*orientsmoothsigma);  
	    if(sze % 2 == 0)
	    	sze = sze + 1;   
	    
	    gf = new GaussianFilter((int)sze);//% Generate Gaussian filter.
	    BufferedImage orientsmoothIm = null;
	    orientsmoothIm = gf.filter( blockIm, orientsmoothIm );
	    
	    //cos2theta = filter2(f, cos2theta); //% Smoothed sine and cosine of
	    //sin2theta = filter2(f, sin2theta); //% doubled angles
	    
	    //orientim = pi/2 + atan2(sin2theta,cos2theta)/2;

	    //% Calculate 'reliability' of orientation data.  Here we calculate the
	    //% area moment of inertia about the orientation axis found (this will
	    //% be the minimum inertia) and an axis  perpendicular (which will be
	   // % the maximum inertia).  The reliability measure is given by
	    //% 1.0-min_inertia/max_inertia.  The reasoning being that if the ratio
	    //% of the minimum to maximum inertia is close to one we have little
	   // % orientation information.
	    
	    //Imin = (Gyy+Gxx)/2 - (Gxx-Gyy).*cos2theta/2 - Gxy.*sin2theta/2;
	    //Imax = Gyy+Gxx - Imin;
	    
	    //reliability = 1 - Imin./(Imax+.001);
	    
	   //% Finally mask reliability to exclude regions where the denominator
	    //% in the orientation calculation above was small.  Here I have set
	    //% the value to 0.001, adjust this if you feel the need 
	    //reliability = reliability.*(denom>.001);
	    return (new RidgeOrientReturn(orientsmoothIm, 0.0));
    }
	private class RidgeOrientReturn{
		BufferedImage orientim;
		double reliability;
		public RidgeOrientReturn(BufferedImage orientim, double reliability){
			//this.orientim = Arrays.copyOf(orientim, orientim.length);
			this.orientim = orientim;
			this.reliability = reliability;
		}
		public BufferedImage getOrientim(){
			return this.orientim;
		}
		public double getReliability(){
			return this.reliability;
		}
	}
}

