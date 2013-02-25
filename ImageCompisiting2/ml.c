/*
     %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
   %%                                                            %%
 %%       Prof. Sclaroff's CS585 Image avd Video Processing       %%
%%                         Final Project                           %%
%%                    I M A G E   M O S A I C S                    %%
%%                                                                 %%
%%                    by Stanislav Rost                    %%
 %%                       ID:  31764117                           %%
  %%                                                             %%
   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
*/

#include "/usr/local/IT/matlab-5.2.1/extern/include/mex.h"
#include <math.h>

#define MAX_STEPS 100
#define ERROR_THRESHOLD 2000.0
#define ERROR_CHANGE_THRESHOLD 200.0

#define MAX(a, b) ( (a)>(b) ? (a) : (b))
#define MIN(a, b) ( (a)<(b) ? (a) : (b))

/* ML.C

   by Stanislav Rost (C) 1998

   MATLAB 5.2 MEX file for Marquardt-Levenberg
   
   Takes in the following arguments:
   image1 - image which will be "warped"
   gradX - gradient image dX/dI for image 2
   gradY - gradient image dY/dI for image 2
   image2 - image onto which the current image is being warped
   trans - original estimate of the transform matrix

   Returns the following arguments:
   trans - the version of the transform matrix which 
           results in minimized error.

*/

/* Multiplies 3x3 matrix by the 3x1 vector, stores the result
   in result, normalizes result so that it is homogeneous */
void matMult(double *mat, double *vec, double *result)
{
  result[0] = vec[0]*mat[0] + 
    vec[1]*mat[3] + mat[6];
  result[1] = vec[0]*mat[1] + 
    vec[1]*mat[4] + mat[7];  
  result[2] = vec[0]*mat[2] + 
    vec[1]*mat[5] + mat[8];  

  result[0] /= result[2];
  result[1] /= result[2];
  result[2] = 1;

}

/* Multiplies the pixel location x,y
   by the transfrom matrix mat and stores
   the new location in storeX, storeY */
void transPixel(double *mat, 
		double x, double y, double *storeX, double *storeY)
{
  double vec1[3], vec2[3];

  vec1[0] = x;
  vec1[1] = y;
  vec1[2] = 1;

  matMult(mat, vec1, vec2);

  *storeX = vec2[0];
  *storeY = vec2[1];
}

void printMatrix3x3(char *name, double *m)
{
  int i,j;

  mexPrintf("Matrix: %s\n", name);
  for (i=0;i<3;i++)
    {
      for (j=0;j<3;j++)
	{
	  mexPrintf("%f ", m[i + j*3]);
	}
      mexPrintf("\n");
    }
}

void printMatrix8x8(char *name, double *m)
{
  int i,j;

  mexPrintf("Matrix: %s\n", name);
  for (i=0;i<8;i++)
    {
      for (j=0;j<8;j++)
	{
	  mexPrintf("%f ", m[i + j*8]);
	}
      mexPrintf("\n");
    }
}

/* Normalizes the 3x3 matrix
   (divides all values by the value at (3,3))
*/
void matNormalize3x3(double *m)
{
  int i,j;
  double normalizeBy;

  normalizeBy = m[2 + 2*3];

  for (i=0;i<3;i++)
    for (j=0;j<3;j++)
      m[i + j*3] /= normalizeBy;
}

/* Finds the inverse of a 3x3 matrix 
   using MATLAB
*/
void matInverse3x3(double *source, double *dest)
{
  mxArray *in[1], *out[1];
  double *allocMem;

  in[0] = mxCreateDoubleMatrix(3, 3, mxREAL);
  allocMem = (double *)mxCalloc(sizeof(double), 9);
  memcpy(allocMem, source, sizeof(double)*9);
  mxSetPr(in[0], allocMem);
  mexCallMATLAB(1, out, 1, in, "inv");
  memcpy(dest, mxGetPr(out[0]), sizeof(double)*9);

  /* mxDestroyArray(out[0]); */
  /* mxFree(allocMem); */
}

/* Finds the inverse of an 8x8 matrix */
void matInverse8x8(double *source, double *dest)
{
  mxArray *in[1], *out[1];
  double *allocMem;

  in[0] = mxCreateDoubleMatrix(8, 8, mxREAL);
  allocMem = (double *)mxCalloc(sizeof(double), 64);
  memcpy(allocMem, source, sizeof(double)*64);
  mxSetPr(in[0], allocMem);
  mexCallMATLAB(1, out, 1, in, "inv");
  memcpy(dest, mxGetPr(out[0]), sizeof(double)*64);

  /* mxDestroyArray(out[0]); */
  /* mxFree(allocMem); */
}

void mexFunction(
    int           nlhs,           /* number of expected outputs */
    mxArray       *plhs[],        /* array of pointers to output arguments */
    int           nrhs,           /* number of inputs */
#if !defined(V4_COMPAT)
    const mxArray *prhs[]         /* array of pointers to input arguments */
#else
    mxArray *prhs[]         /* array of pointers to input arguments */
#endif
)
{

  /* Position in the image 1 */
  signed int x, y;

  /* Position in the image 2 which is x,y * transfrom matrix */
  double origX, origY;

  /* Location in the second image */
  double imPrimeX, imPrimeY;

  /* Pixel values in the second and first images */
  double imPrimePixel, imPixel;

  /* Colors at the corners of the pixel
     neighborhood in bilinear interplation */
  double colorLU, colorRU, colorLL, colorRL;

  /* Pixel neighborhood coordinates for
     bilinear interpolation */
  int upperY, lowerY, upperX, lowerX;

  /* Floating point differences between the pixel's
     actual location and pixel integer locations
     for bilinear interpolation */
  double dx1, dx2, dy1, dy2;


  /* Bilinearly interpolated values of the image gradient */
  double biGradX, biGradY;

  /* Widths and heights of the images */
  double width1, height1, width2, height2;

  /* Pointers to data arrays */
  double *image1, *transform;
  double *image2, *gradX, *gradY;

  /* Loop variables */
  int k, l;

  /* Marquardt-Levenberg variables */

  /* Error accumulator */
  double e;
  /* dE/dM_i's */
  double DeDm[8];
  /* A matrix */
  double a[64];
  /* B matrix */
  double b[8];
  double identity[64];

  /* Denominator of the DeDm's */
  double d;

  /* Counter of how many pixels overlap */
  int numOverlap;

  /* Lambda - initially set to 1e-4 */
  double lambda = 1e-4;

  /* Storage for the inverse of (A-lambda*I) */
  double inverse[64];

  /* Delta m's */
  double deltam[8];

  /* Error */
  double error = -1;
  double prevError = -1;

  /* Conts the number of iteration steps */
  int stepCounter = 0;

  /* Flag which means that we have 
     reverted to the old M */
  int revertedFlag = 0;

  /* Step One - Error Checking */
  if (nrhs != 5)
    {
      mexErrMsgTxt("ml requires 5 arguments (image1, gradX, gradY, image2, trans)");
    }

  if (nlhs!=1)
    mexErrMsgTxt("ml requires one output argument.");
 
  /* Get values from the passed-in variables */
  height1 = mxGetM(prhs[0]);
  width1 = mxGetN(prhs[0]);
  height2 = mxGetM(prhs[3]);
  width2 = mxGetN(prhs[3]);

  image1 = (double *)mxGetPr(prhs[0]);
  image2 = (double *)mxGetPr(prhs[3]);
  gradX = (double *)mxGetPr(prhs[1]);
  gradY = (double *)mxGetPr(prhs[2]);
  transform = (double *)mxGetPr(prhs[4]);

  /* Start the minimization loop */
  while ((error == -1 || error > ERROR_THRESHOLD) && 
	 stepCounter < MAX_STEPS)
    {

#ifdef DEBUG      
      mexPrintf("*********************\n");
      mexPrintf("       SETTING UP\n");
      mexPrintf("*********************\n");      
#endif

      /* Reset a, b, error, counter of
         overlapping pixels */
      for (x=0;x<8;x++)
	{
	  b[x] = 0.0;
	}

      for (x=0;x<64;x++)
	{
	  a[x] = 0.0;
	}
      
      numOverlap = 0;

      error = 0;

#ifdef DEBUG
      mexPrintf("*********************\n");
      mexPrintf("  STARTING MAIN LOOP\n");
      mexPrintf("*********************\n");     
#endif

      /* Go across the pixels in the first image */
      for (x=0; x<width1; x++)
	{
	  for (y = 0; y<height1; y++)
	    {

	      /* Find out the location of the corresponding
		 pixel in the second image */
	      transPixel(transform, (double)x, (double)y, &origX, &origY);

      	      imPrimeX = origX;
	      imPrimeY = origY;

	      if (imPrimeX < 0 || imPrimeX > (width2-1) || 
		  imPrimeY < 0 || imPrimeY > (height2-1))
		{
		  /* The pixel is outside the image and
		     hence there is no overlap */
		  imPrimePixel = -1;
		}
	      else
		{	      
		  /* There's overlap -- figure out
		     the color and gradient values
		     in the second image using 
		     bilinear interpolation.
		  */

		  /* Find out the integer locations
		     of pixels */
		  upperY = (int)ceil(imPrimeY);
		  lowerY = (int)floor(imPrimeY);
		  upperX = (int)ceil(imPrimeX);
		  lowerX = (int)floor(imPrimeX);
		  
		  /* Find out differences in 
		     floating-point values and integer
		     lcoations of pixels */
		  dx1 = upperX - imPrimeX;
		  dx2 = imPrimeX - lowerX;
		  dy1 = upperY - imPrimeY;
		  dy2 = imPrimeY - lowerY;

		  /* Find out the intensity values
		     at the neighboring pixels */
		  colorLU = image2[upperY + lowerX*(int)height2];
		  colorLL = image2[lowerY + lowerX*(int)height2];
		  colorRU = image2[upperY + upperX*(int)height2];
		  colorRL = image2[lowerY + upperX*(int)height2];

		  /* Find out the color of the pixel */
		  imPrimePixel =
		    /* Upper portion */
		    (dx2*colorRU + dx1*colorLU)*dy2 +
		    /* Lower portion */
		    (dx2*colorRL + dx1*colorLL)*dy1;
		  
		  /* Do the same for the gradient images */
		  colorLU = gradX[upperY + lowerX*(int)height2];
		  colorLL = gradX[lowerY + lowerX*(int)height2];
		  colorRU = gradX[upperY + upperX*(int)height2];
		  colorRL = gradX[lowerY + upperX*(int)height2];	      

		  biGradX = 		    
		    /* Upper portion */
		    (dx2*colorRU + dx1*colorLU)*dy2 +
		    /* Lower portion */
		    (dx2*colorRL + dx1*colorLL)*dy1;

		  colorLU = gradY[upperY + lowerX*(int)height2];
		  colorLL = gradY[lowerY + lowerX*(int)height2];
		  colorRU = gradY[upperY + upperX*(int)height2];
		  colorRL = gradY[lowerY + upperX*(int)height2];	

		  biGradY = 		    
		    /* Upper portion */
		    (dx2*colorRU + dx1*colorLU)*dy2 +
		    /* Lower portion */
		    (dx2*colorRL + dx1*colorLL)*dy1;	  
		  
		}     
	      		  
	      /* Find out the image pixel value in the first
		 image */

	      imPixel = image1[y + x*(int)height1];

#ifdef DEBUG
	      if (imPixel < -1 || imPixel > 255)
		{
		  mexPrintf("%f\n", imPixel);
		  mexErrMsgTxt("imPixel is improper.");
		}
#endif	    
  
	      /* Check if there's overlap-- (-1) denotes a 
	         pixel which does not belong to an image 
	      */
	      if (imPixel != -1 && imPrimePixel != -1)
		{
		  /* Do the Marquardt-Levenberg */
	    
		  e = imPrimePixel - imPixel;  
		  error += e*e;

		  d = transform[2]*x + transform[5]*y + 1;
		  
		  DeDm[0] = biGradX * x / d;
		  DeDm[1] = biGradX * y / d;
		  DeDm[2] = biGradX / d;
		  DeDm[3] = biGradY * x / d;
		  DeDm[4] = biGradY * y / d;
		  DeDm[5] = biGradY / d;
		  DeDm[6] = -x/d*
		    (imPrimeX*biGradX +
		     imPrimeY*biGradY);
		  DeDm[7] = -y/d*
		    (imPrimeX*biGradX +
		     imPrimeY*biGradY);
		  
		  /* Calculate contributions to a and b */
		  for (k=0;k<8;k++)
		    {
		      
		      b[k] += e*DeDm[k];
		      
		      for (l=0;l<8;l++)
			{
			  a[l + k*8] += DeDm[k]*DeDm[l];
			}
		    }
		      
		  /* Increment the overlapping pixels counter */
		  numOverlap ++;
		  
		}
	    }
	}

#ifdef DEBUG
      mexPrintf("FINAL STAGE.\n");
#endif      

      /* Normalize the a and b matrices by the counter
         of overlapping pixels */
      for (x=0;x<8;x++)
	for (y=0;y<8;y++)
	  {
	    a[x*8+y] /= (double)numOverlap;
	  }
      
      for (x=0;x<8;x++)
	{
	  /* Note the minus because we were supposed
	     to take the negative of the sum */
	  b[x] /= -(double)numOverlap;
	}

#ifdef DEBUG
      for (x=0;x<8;x++)
	mexPrintf("B[%d]: %f\n", x, b[x]);

      mexPrintf("NumOverlap: %d\n", numOverlap);
#endif

      /* Calculate lambda+identity */
      for (x=0;x<64;x++)
	{
	  identity[x] = 0;
	}
      
      identity[0] = lambda;
      identity[9] = lambda;
      identity[18] = lambda;
      identity[27] = lambda;
      identity[36] = lambda;
      identity[45] = lambda;
      identity[54] = lambda;
      identity[63] = lambda;

#ifdef DEBUG
      printMatrix8x8("A", a);
      printMatrix8x8("Indentity", identity);
#endif      

      /* Sum lambda matrix and a matrix */
      for (x=0;x<8;x++)
	for (y=0;y<8;y++)
	  a[y + x*8] += identity[y + x*8];

#ifdef DEBUG
      printMatrix8x8("A+Identity", a);
#endif

      /* Find inverse by calling MATLAB */
      matInverse8x8(a, inverse);

#ifdef DEBUG
      mexPrintf("Inverse found.\n");
      printMatrix8x8("Inverse", inverse);
#endif

      /* Zero in delta ms */
      for (x=0;x<8;x++)
	deltam[x] = 0;
      
      /* Find delta m's by multiplying
         the inverse and the b vector (yes, that's
         what this loop does)
      */
      for (x=0; x<8; x++)
	for (y=0; y<8; y++)
	  deltam[y] += inverse[y + x*8]*b[x];

#ifdef DEBUG
      for (x=0;x<8;x++)
	mexPrintf("Deltam[%d]: %f\n", x, deltam[x]);
#endif      

      /* Adjust m */
      transform[0] += deltam[0];
      transform[3] += deltam[1];
      transform[6] += deltam[2];
      transform[1] += deltam[3];
      transform[4] += deltam[4];
      transform[7] += deltam[5];
      transform[2] += deltam[6];
      transform[5] += deltam[7];

      matNormalize3x3(transform);

#ifdef DEBUG
      printMatrix3x3("new transform", transform);
#endif

      stepCounter++;

      mexPrintf("Error:  %f\n", error);

      /* If the error hasn't changed much, end the
	 algorithm--local minimum reached */
      if (prevError != -1 && abs(prevError - error)<ERROR_CHANGE_THRESHOLD
	  && !revertedFlag )
	break;

      /* If error has increased since last iteration
	 scale lambda up */
      if (prevError != -1 && error >= prevError )
	{
	  lambda = lambda * 10;

	  /* Revert to old m */
	  transform[0] -= deltam[0];
	  transform[3] -= deltam[1];
	  transform[6] -= deltam[2];
	  transform[1] -= deltam[3];
	  transform[4] -= deltam[4];
	  transform[7] -= deltam[5];
	  transform[2] -= deltam[6];
	  transform[5] -= deltam[7];

	  revertedFlag = 1;

	}
      else
	{
	  lambda = lambda / 10;

	  revertedFlag = 0;
	  
	}


      prevError = error;

#ifdef DEBUG
      mexEvalString("pause");
#endif
    }

  /* Return the results to MATLAB */
  plhs[0] = mxCreateDoubleMatrix( 3, 3, mxREAL);
  mxSetPr(plhs[0], (double *)transform);

}
