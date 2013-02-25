
/*
 *    Utility functions
 *
 *    Author: Edward Alston Anthony
 *
 */

#include "utils.h"

/* Converts the RGB values of a pixel to grayscale */
double Intensity(pixelType p)
{
#ifdef USE_TRACEBACK
  Trace->Add(__FILE__, __LINE__);
#endif
  return ((p.r * 0.3) + (p.g * 0.59) + (p.b * 0.11));
}

/*  produces the output image based on the pair of images
 *  and a transformation matrix that takes the first image
 *  and transforms it to overlap with the second image
 */
Picture *DrawImage(Picture *I1, Picture *I2, Matrix *M, bool UseMultiresolutionSpline)
{
#ifdef USE_TRACEBACK
  Trace->Add(__FILE__, __LINE__);
#endif
  pixelType c, c1, c2;
  Picture *result = NULL;
  Picture *overlapI1 = NULL;
  Picture *overlapI2 = NULL;
  
  Matrix *mi = M->Inverse();
  mi->NormalizeBy(mi->Get(3, 3));

  int xMin = min((int) rint(M->Get(1, 3)), 0);
  int xMax = max((int) (rint(M->Get(1, 3)) + I1->GetWidth()), I2->GetWidth());
  int yMin = min((int) rint(M->Get(2, 3)), 0);
  int yMax = max((int) (rint(M->Get(2, 3)) + I1->GetHeight()), I2->GetHeight());

  int width = (int) (floor(xMax - xMin) - (xMax - xMin) % 8) + 8;
  int height = (int) (floor(yMax - yMin) - (yMax - yMin) % 8) + 8;

  /*  for use in the multiresolution splining
   *  to discover a suitable weighted average
   *  at the overlapping region
   */
  pointType Max1 = { 0, 0 }, Max2 = { 0, 0 }, overlapMax = { 0, 0 };
  pointType Min1 = { width, height }, Min2 = { width, height }, overlapMin = { width, height };

  result = new Picture(width, height);
  overlapI1 = new Picture(width, height);
  overlapI2 = new Picture(width, height);

  for (int x = xMin; x < xMax; x++) {
    for (int y = yMin; y < yMax; y++) {
      double D, xp, yp;
  
      memset((void *) &c, 0, sizeof(pixelType));

      D = (mi->Get(3, 1) * x) + (mi->Get(3, 2) * y) + 1;
      xp = ((mi->Get(1, 1) * x) + (mi->Get(1, 2) * y) + mi->Get(1, 3)) / D;
      yp = ((mi->Get(2, 1) * x) + (mi->Get(2, 2) * y) + mi->Get(2, 3)) / D; 

      if (I1->Inside((int) rint(xp), (int) rint(yp)) &&
          I2->Inside(x, y)) {
        try { c1 = I1->GetPixel((int) rint(xp), (int) rint(yp)); }
        catch (IndexOutOfBoundsException ex) {}

        try { c2 = I2->GetPixel(x, y); }
        catch (IndexOutOfBoundsException ex) {}

        if (Max1.x < x + (0 - xMin))
          Max1.x = x + (0 - xMin);
        if (Min1.x > x + (0 - xMin))
          Min1.x = x + (0 - xMin);
        if (Max1.y < y + (0 - yMin))
          Max1.y = y + (0 - yMin);
        if (Min1.y > y + (0 - yMin))
          Min1.y = y + (0 - yMin);

        if (overlapMax.x < x + (0 - xMin))
          overlapMax.x = x + (0 - xMin);
        if (overlapMin.x > x + (0 - xMin))
          overlapMin.x = x + (0 - xMin);
        if (overlapMax.y < y + (0 - yMin))
          overlapMax.y = y + (0 - yMin);
        if (overlapMin.y > y + (0 - yMin))
          overlapMin.y = y + (0 - yMin);

        if (Max2.x < x + (0 - xMin))
          Max2.x = x + (0 - xMin);
        if (Min2.x > x + (0 - xMin))
          Min2.x = x + (0 - xMin);
        if (Max2.y < y + (0 - yMin))
          Max2.y = y + (0 - yMin);
        if (Min2.y > y + (0 - yMin))
          Min2.y = y + (0 - yMin);

        /*  collect overlapping points on two separate image space
         *  to perform the multiresolution splining on the overlapping region
         */
        try { overlapI1->SetPixel(x + (0 - xMin), y + (0 - yMin), c1); }
        catch (IndexOutOfBoundsException ex) {}

        try { overlapI2->SetPixel(x + (0 - xMin), y + (0 - yMin), c2); }
        catch (IndexOutOfBoundsException ex) {}
      }
      else if (I1->Inside((int) rint(xp), (int) rint(yp))) {
        if (Max1.x < x + (0 - xMin))
          Max1.x = x + (0 - xMin);
        if (Min1.x > x + (0 - xMin))
          Min1.x = x + (0 - xMin);
        if (Max1.y < y + (0 - yMin))
          Max1.y = y + (0 - yMin);
        if (Min1.y > y + (0 - yMin))
          Min1.y = y + (0 - yMin);

        try { c = I1->GetPixel((int) rint(xp), (int) rint(yp)); }
        catch (IndexOutOfBoundsException ex) {}

        try { overlapI1->SetPixel(x + (0 - xMin), y + (0 - yMin), c); }
        catch (IndexOutOfBoundsException ex) {}
      }
      else if (I2->Inside(x, y)) {
        if (Max2.x < x + (0 - xMin))
          Max2.x = x + (0 - xMin);
        if (Min2.x > x + (0 - xMin))
          Min2.x = x + (0 - xMin);
        if (Max2.y < y + (0 - yMin))
          Max2.y = y + (0 - yMin);
        if (Min2.y > y + (0 - yMin))
          Min2.y = y + (0 - yMin);

        try { c = I2->GetPixel(x, y); }
        catch (IndexOutOfBoundsException ex) {}

        try { overlapI2->SetPixel(x + (0 - xMin), y + (0 - yMin), c); }
        catch (IndexOutOfBoundsException ex) {}
      }
      else
        memset((void *) &c, 0, sizeof(pixelType));

      if (!UseMultiresolutionSpline) {
        try { result->SetPixel(x + (0 - xMin), y + (0 - yMin), c); }
        catch (IndexOutOfBoundsException ex) {}
      }
    }  
  }

  if (UseMultiresolutionSpline) {
    Picture *temp = Combine(overlapI1, overlapI2, Max1, Min1, Max2, Min2, overlapMax, overlapMin);
    *result = *temp;
    delete temp;
  }
  else {
    /*  find the weights for the contribution of pixel
     *  intensity of the overlapping images and draw the
     *  the pixel of that overlapping region
     */

    pixelType c;
    int x1Max = 0, y1Max = 0;
    int x1Min = overlapI1->GetWidth();
    int y1Min = overlapI1->GetHeight();

    int x2Max = 0, y2Max = 0;
    int x2Min = overlapI2->GetWidth();
    int y2Min = overlapI2->GetHeight();

    for (int x = 0; x < overlapI1->GetWidth(); x++)
      for (int y = 0; y < overlapI1->GetHeight(); y++) {
        c = overlapI1->GetPixel(x, y);
        if ((c.r != 0) || (c.g != 0) || (c.b != 0)) {
          x1Min = min(x1Min, x);
          x1Max = max(x1Max, x);
          y1Min = min(y1Min, y);
          y1Max = max(y1Max, y);
        }

        c = overlapI2->GetPixel(x, y);
        if ((c.r != 0) || (c.g != 0) || (c.b != 0)) {
          x2Min = min(x2Min, x);
          x2Max = max(x2Max, x);
          y2Min = min(y2Min, y);
          y2Max = max(y2Max, y);
        }
      }

    for (int x = 0; x < overlapI1->GetWidth(); x++)
      for (int y = 0; y < overlapI1->GetHeight(); y++) {
        pixelType c1 = overlapI1->GetPixel(x, y);
        pixelType c2 = overlapI2->GetPixel(x, y);

        if (((c1.r != 0) || (c1.g != 0) || (c1.b != 0)) &&
            ((c2.r != 0) || (c2.g != 0) || (c2.b != 0))) {
          double w1, w2;

          if ((abs(y2Max - y1Max)) < abs((x2Max - x1Max)))
            w1 = 1.0 * (x - x1Min) / (x1Max - x1Min);
          else
            w1 = 1.0 * (y - y1Min) / (y1Max - y1Min);
          w2 = 1 - w1;

          c.r = (byte) (rint((w1 * c1.r)) + rint((w2 * c2.r)));
          c.g = (byte) (rint((w1 * c1.g)) + rint((w2 * c2.g)));
          c.b = (byte) (rint((w1 * c1.b)) + rint((w2 * c2.b)));

          try { result->SetPixel(x, y, c); }
          catch (IndexOutOfBoundsException ex) {}
        }
      }
  }

  delete mi;
  delete overlapI1;
  delete overlapI2;

  result->SetName("output.ppm");
  return result;
}

/*  The registration algorithm
 *  using the Levenberg-Marquadt minimization algorithm 
 */
Matrix *Register(Picture *I1, Picture *I2, pointType InitialPoints[2][4])
{
#ifdef USE_TRACEBACK
  Trace->Add(__FILE__, __LINE__);
#endif

  Matrix u1(8, 8);
  Matrix u2(8, 1);
  Matrix m(8, 1);

  double x[4][2];
  double y[4][2];

  for (int i = 0; i < 4; i++) {
    x[i][0] = (double) InitialPoints[0][i].x;
    y[i][0] = (double) InitialPoints[0][i].y;
    x[i][1] = (double) InitialPoints[1][i].x;
    y[i][1] = (double) InitialPoints[1][i].y;
  }

  for (int i = 0; i < 4; i++) {
    u1.Set((2*i)+1, 1, x[i][0]);
    u1.Set((2*i)+1, 2, y[i][0]);
    u1.Set((2*i)+1, 3, 1);
    u1.Set((2*i)+1, 4, 0);
    u1.Set((2*i)+1, 5, 0);
    u1.Set((2*i)+1, 6, 0);
    u1.Set((2*i)+1, 7, -(x[i][0] * x[i][1]));
    u1.Set((2*i)+1, 8, -(y[i][0] * x[i][1]));

    u1.Set((2*i)+2, 1, 0);
    u1.Set((2*i)+2, 2, 0);
    u1.Set((2*i)+2, 3, 0);
    u1.Set((2*i)+2, 4, x[i][0]);
    u1.Set((2*i)+2, 5, y[i][0]);
    u1.Set((2*i)+2, 6, 1);
    u1.Set((2*i)+2, 7, -(x[i][0] * y[i][1]));
    u1.Set((2*i)+2, 8, -(y[i][0] * x[i][1]));

    u2.Set((2*i)+1, 1, x[i][1]);
    u2.Set((2*i)+2, 1, y[i][1]);
  }
  m = *u1.Inverse() * u2;
  cout << "Initial m = \n" << m << endl;

  Matrix pt(3, 1);
  Matrix m2(3, 3);
  m2.Set(1, 1, m.Get(1, 1));
  m2.Set(1, 2, m.Get(2, 1));
  m2.Set(1, 3, m.Get(3, 1));
  m2.Set(2, 1, m.Get(4, 1));
  m2.Set(2, 2, m.Get(5, 1));
  m2.Set(2, 3, m.Get(6, 1));
  m2.Set(3, 1, m.Get(7, 1));
  m2.Set(3, 2, m.Get(8, 1));
  m2.Set(3, 3, 1);

  for (int i = 0; i < 4; i++) {
    pt.Set(1, 1, x[i][0]);
    pt.Set(2, 1, y[i][0]);
    pt.Set(3, 1, 1);
    pt = m2 * pt;
    pt.Set(1, 1, pt.Get(1, 1) / pt.Get(3, 1));
    pt.Set(2, 1, pt.Get(2, 1) / pt.Get(3, 1));
    pt.Set(3, 1, 1);
  }

#ifndef BYPASS_MINIMIZATION
  Matrix A(8, 8);
  Matrix b(8, 1);
  Matrix dm(8, 1);
  double lambda = 0.0001;
  double eThreshold = 10;
  double E = 1000;
  double lastE = 0.0;
  double totalError = 0.0;
  int totalOverlap = 0;
  int iteration = 0;

  /* Continue iterating as long as the sum of the error in intensity
   * between corresponding pixels is greater than the threshold
   */
  while ((E > eThreshold) &&
         (iteration < MAXIMUM_ITERATIONS)) {
    double xp, yp, D;

    /* Initialize the 8x8 Hessian matrix, A,
     * and the weighted gradient vector b
     * to 0 in all cells
     */
    A.LoadZero();
    b.LoadZero();
    dm.LoadZero();
    totalError = 0.0;
    totalOverlap = 0;

    /* for each pixel i at location (x_i, y_i) */
    for (int y = 0; y < I1->GetHeight(); y++) {
      for (int x = 0; x < I1->GetWidth(); x++) {
        /* a) compute its corresponding position in the other image (x', y')
         *    using x' = (m_0x + m_1y + m_2) / D, y' = (m_3x + m_3y + m_5) / D
         *    where D = m_6x + m_7y +1
         */
        D = (m.Get(7, 1) * x) + (m.Get(8, 1) * y) + 1;
        xp = ((m.Get(1, 1) * x) + (m.Get(2, 1) * y) + m.Get(3, 1)) / D;
        yp = ((m.Get(4, 1) * x) + (m.Get(5, 1) * y) + m.Get(6, 1)) / D;

        /* we're only interested in those pixels that are within the other image */
        if (I2->Inside((int) floor(xp), (int) floor(yp)) &&
            I2->Inside((int) ceil(xp), (int) ceil(yp))) {
          totalOverlap++;

          /* b) compute the error in intensity between the corresponding pixels
           *    e = I'(x', y') - I(x, y)
           *    and intensity gradient (dI' / dx', dI' / dy')
           *    using bilinear interpolation on I
           */
          double I, Ip, err;
          double x_l = floor(xp);
          double x_r = x_l + 1;
          double y_d = floor(yp);
          double y_u = y_d + 1;

          double dx = xp - x_l;
          double dy = yp - y_d;
          double Iy_u, Iy_d, Ix_l, Ix_r;

          try {
            Iy_u = ((Intensity(I2->GetPixel((int) x_r, (int) y_u)) -
                     Intensity(I2->GetPixel((int) x_l, (int) y_u))) * dx) +
                   Intensity(I2->GetPixel((int) x_l, (int) y_u));

            Iy_d = ((Intensity(I2->GetPixel((int) x_r, (int) y_d)) -
                     Intensity(I2->GetPixel((int) x_l, (int) y_d))) * dx) +
                   Intensity(I2->GetPixel((int) x_l, (int) y_d));

            Ix_l = ((Intensity(I2->GetPixel((int) x_l, (int) y_u)) -
                     Intensity(I2->GetPixel((int) x_l, (int) y_d))) * dy) +
                   Intensity(I2->GetPixel((int) x_l, (int) y_d));

            Ix_r = ((Intensity(I2->GetPixel((int) x_r, (int) y_u)) -
                     Intensity(I2->GetPixel((int) x_r, (int) y_d))) * dy) +
                   Intensity(I2->GetPixel((int) x_r, (int) y_d));

            try {
              I = Intensity(I1->GetPixel(x, y));
              Ip = ((Iy_u - Iy_d) * dy) + Iy_d;
              err = Ip - I;
              totalError += (err * err);
            }
            catch (IndexOutOfBoundsException ex) {}
          }
          catch (IndexOutOfBoundsException ex) {}

          double dI_dxp = Ix_r - Ix_l;
          double dI_dyp = Iy_u - Iy_d;

          /* c) Compute the partial derivatives of e_i with respect to m_k using
           *    de / dm_k = ((dI' / dx') * (dx' / dm_k)) + ((dI' / dy') * (dy' / dm_k))
           */
          double de_dm[8];
          de_dm[0] = dI_dxp * x / D;
          de_dm[1] = dI_dxp * y / D;
          de_dm[2] = dI_dxp / D;
          de_dm[3] = dI_dyp * x / D;
          de_dm[4] = dI_dyp * y / D;
          de_dm[5] = dI_dyp / D;
          de_dm[6] = -(x / D) * ((dI_dxp * xp) + (dI_dyp * yp));
          de_dm[7] = -(y / D) * ((dI_dxp * xp) + (dI_dyp * yp));

          /* d) Add pixel's contribution to A and b using
           *    a_kl = summation((de / dm_k) * (de / dm_l))
           *    b_k = - summation(e * (de / dm_k))
           */
          for (int k = 0; k < 8; k++) {
            for (int l = 0; l < 8; l++)
              A.Set(k+1, l+1, A.Get(k+1, l+1) + (de_dm[k] * de_dm[l]));
            b.Set(k+1, 1, b.Get(k+1, 1) + (err * de_dm[k]));
          }
        }
      }
    }

    /* Divide the sum of the squared intensity error by the number of overlapping pixels */
    E = totalError / totalOverlap;
    cout << "Total error: " << E << ", overlapping pixels: " << totalOverlap << endl;

    /* 2) Solve the system of equations (A + lambda*I)dm = b,
     *    and update the motion estimate m^t+1 = m^t + dm
     *    therefore, dm = inv(A + lambda*I) * b
     */

    /* Normalize the matrices A, and b
     * by the number of pixels in the overlap region
     */
    for (int i = 1; i < 9; i++) {
      for (int j = 1; j < 9; j++)
        A.Set(i, j, A.Get(i, j) / totalOverlap);
      b.Set(i, 1, -1 *  b.Get(i, 1) / totalOverlap);
    }

    Matrix I(8, 8);
    I.LoadIdentity();
    dm = *(A + (I * lambda)).Inverse() * b;
    m += dm;

    /* 3) Check the total error in intensity between corresponding pixels
     *    had decreased, if not, increment lambda and compute a new dm
     */
    if (iteration > 0) {       /* if not first iteration */
      double diff = E - lastE;
      lastE = E;

      /* if error increased, increment lambda */
      if (diff > 0) {
        /* do not update m with the motion estimate */
        m -= dm;
        lambda = lambda * 10;
      }

      /* if error doesn't change much, stop iterating */
      else if (fabs(diff) < SMALLEST_ERROR_CHANGE) {
        cout << "No change in error, terminating loop" << endl;
        iteration = MAXIMUM_ITERATIONS;
      }

      /* if error decreased, decrement lambda */
      else {
        if (fabs(diff) < 5) {
          if (lambda < MAXIMUM_LAMBDA_VALUE)
            lambda = lambda * 10;
        }
        else
          if (lambda > MINIMUM_LAMBDA_VALUE)
            lambda = lambda / 10;
      }
    }
    else
      lastE = E;

    cout << "Iteration: " << iteration << ", lambda: " << lambda << endl;
    iteration++;
    cout << "m = \n" << m << endl;
  }
#endif

  Matrix *M = new Matrix(3, 3);
  M->Set(1, 1, m.Get(1, 1));
  M->Set(1, 2, m.Get(2, 1));
  M->Set(1, 3, m.Get(3, 1));
  M->Set(2, 1, m.Get(4, 1));
  M->Set(2, 2, m.Get(5, 1));
  M->Set(2, 3, m.Get(6, 1));
  M->Set(3, 1, m.Get(7, 1));
  M->Set(3, 2, m.Get(8, 1));
  M->Set(3, 3, 1);

  return M;
}

/* reduces an image, as described in the Burt-Adelson paper */
Picture *Reduce(Picture *src)
{
#ifdef USE_TRACEBACK
  Trace->Add(__FILE__, __LINE__);
#endif
  int Width = (src->GetWidth() / 2);
  int Height = (src->GetHeight() / 2);

  cout << "Reducing image to " << Width << "x" << Height << endl;
  Picture *result = new Picture(Width, Height);

  /* weights as suggested in the Burt-Adelson's paper */
  double weight[5] = { 0.05, 0.25, 0.4, 0.25, 0.05 };

  intensityType g0, g1;

  for (int j = 0; j < Height; j++)
    for (int i = 0; i < Width; i++) {
      memset((void *) &g1, 0, sizeof(intensityType));

      for (int m = -2; m < 3; m++)
        for (int n = -2; n < 3; n++) {
          int x = (2 * i) + m;
          int y = (2 * j) + n;

          /* for boundary conditions, use a reflection across the edge node */
          if (x >= src->GetWidth())
            x = x - abs(Width - (x + 1));
          if (y >= src->GetHeight())
            y = y - abs(src->GetHeight() - (y + 1));
          if (x < 0)
            x = abs(x);
          if (y < 0)
            y = abs(y);

          int mp = m + 2;
          int np = n + 2;
          if (src->Inside(x, y)) {
            try {
              g0 = src->GetPixelIntensity(x, y);
              g1.r = (int) rint((weight[mp] * weight[np] * g0.r) + g1.r);
              g1.g = (int) rint((weight[mp] * weight[np] * g0.g) + g1.g);
              g1.b = (int) rint((weight[mp] * weight[np] * g0.b) + g1.b);
            }
            catch (IndexOutOfBoundsException ex) {}
          }
        }
      result->SetPixelIntensity(i, j, g1);
    }

  return result;
}

/* expands an image, as described in the Burt-Adelson paper */
Picture *Expand(Picture *src)
{
#ifdef USE_TRACEBACK
  Trace->Add(__FILE__, __LINE__);
#endif
  int Width = (src->GetWidth()) * 2;
  int Height = (src->GetHeight()) * 2;

  /* weights as suggested in the Burt-Adelson's paper */
  double weight[5] = { 0.05, 0.25, 0.4, 0.25, 0.05 };

  cout << "Expanding image to " << Width << "x" << Height << endl;
  Picture *result = new Picture(Width, Height);

  intensityType g0, g1;

  for (int j = 0; j < Height; j++)
    for (int i = 0; i < Width; i++) {
      memset((void *) &g1, 0, sizeof(intensityType));

      for (int m = -1; m < 2; m++)
        for (int n = -1; n < 2; n++) {
          int x = (i + m) / 2;
          int y = (j + n) / 2;

          /* for boundary conditions, use a reflection across the edge node */
          if (x >= src->GetWidth())
            x = x - abs(Width - (x + 1));
          if (y >= src->GetHeight())
            y = y - abs(src->GetHeight() - (y + 1));
          if (x < 0)
            x = abs(x);
          if (y < 0)
            y = abs(y);

          int mp = (m + 2);
          int np = (n + 2);
          if (src->Inside(x, y)) {
            try {
              g0 = src->GetPixelIntensity(x, y);
              g1.r = (int) rint(weight[mp] * weight[np] * g0.r) + g1.r;
              g1.g = (int) rint(weight[mp] * weight[np] * g0.g) + g1.g;
              g1.b = (int) rint(weight[mp] * weight[np] * g0.b) + g1.b;
            }
            catch (IndexOutOfBoundsException ex) {}
          }
        }
      result->SetPixelIntensity(i, j, g1);
    }

  return result;
}

/*  returns the Laplacian representation,
 *  given two input images, as described in
 *  the Burt-Adelson paper
 */
Picture *Laplacian(Picture *g1, Picture *g0)
{
#ifdef USE_TRACEBACK
  Trace->Add(__FILE__, __LINE__);
#endif
  cout << "Deriving Laplacian image\n";
  int width = min(g1->GetWidth(), g0->GetWidth());
  int height = min(g1->GetHeight(), g0->GetHeight());

  Picture *result = new Picture(width, height);
  intensityType c0, c1, color;

  for (int j = 0; j < height; j++)
    for (int i = 0; i < width; i++) {
      try {
        c0 = g0->GetPixelIntensity(i, j);
        c1 = g1->GetPixelIntensity(i, j);
      }
      catch (IndexOutOfBoundsException ex) {}

      color.r = c0.r - c1.r;
      color.g = c0.g - c1.g;
      color.b = c0.b - c1.b;

      result->SetPixelIntensity(i, j, color);
    }
  return result;
}

/*  constructs a Gaussian pyramid, given an input image,
 *  as described in the Burt-Adelson paper
 */
pyramidType *GaussianPyramid(Picture *src)
{
#ifdef USE_TRACEBACK
  Trace->Add(__FILE__, __LINE__);
#endif
  pyramidType *result = new pyramidType;

  cout << "Computing Gaussian Pyramid\n";
  result->Images = new Picture[(int) ceil(log(min(src->GetWidth(), src->GetHeight()))) + 2](0, 0);
  result->Images[0] = *src;
  result->Levels = 1;

  result->Images[result->Levels] = *Reduce(src);
  while ((result->Images[result->Levels].GetWidth() > 8) &&
         (result->Images[result->Levels].GetHeight() > 8)) {
    result->Levels++;
    result->Images[result->Levels] = *Reduce(&result->Images[result->Levels - 1]);
  }
  result->Levels++;

  return result;
}

/*  constructs a Laplacian pyramid, given a Gaussian pyramid
 *  as described in the Burt-Adelson paper
 */
pyramidType *LaplacianPyramid(pyramidType *gaussianPyramid)
{
#ifdef USE_TRACEBACK
  Trace->Add(__FILE__, __LINE__);
#endif
  cout << "Computing Laplacian Pyramid\n";
  pyramidType *result = new pyramidType;
  Matrix M(3, 3);
  M.Set(1, 1, 1);
  M.Set(1, 2, 0);
  M.Set(1, 3, 0);

  M.Set(2, 1, 0);
  M.Set(2, 2, 1);
  M.Set(2, 3, 0);

  M.Set(3, 1, 0);
  M.Set(3, 2, 0);
  M.Set(3, 3, 1);

  result->Images = new Picture[(int) ceil(log(min(gaussianPyramid->Images[0].GetWidth(),
                                                  gaussianPyramid->Images[0].GetHeight()))) + 2](0, 0);
  result->Images[0] = *Laplacian(Expand(&gaussianPyramid->Images[1]),
                                 &gaussianPyramid->Images[0]);
  M.Set(1, 1, 1.0 * gaussianPyramid->Images[0].GetWidth() / result->Images[0].GetWidth());
  M.Set(2, 2, 1.0 * gaussianPyramid->Images[0].GetHeight() / result->Images[0].GetHeight());

  /* warp the image to the right dimensions */
  if ((M.Get(1, 1) != 1) ||
      (M.Get(2, 2) != 1)) {
    result->Images[0].Warp(&M, true);
  }

  result->Levels = 1;
  for (int i = 2; i < gaussianPyramid->Levels; i++) {
    result->Images[i-1] = *Laplacian(Expand(&gaussianPyramid->Images[i]), &gaussianPyramid->Images[i-1]);
    M.Set(1, 1, 1.0 * gaussianPyramid->Images[i-1].GetWidth() / result->Images[i-1].GetWidth());
    M.Set(2, 2, 1.0 * gaussianPyramid->Images[i-1].GetHeight() / result->Images[i-1].GetHeight());

    /* warp the image to the right dimensions */
    if ((M.Get(1, 1) != 1) ||
        (M.Get(2, 2) != 1)) {
      result->Images[i-1].Warp(&M, true);
    }
    result->Levels++;
  }
  result->Images[gaussianPyramid->Levels - 1] = gaussianPyramid->Images[gaussianPyramid->Levels - 1];
  result->Levels++;

  return result;
}

/*  Collapses a Laplacian pyramid to reconstruct the
 *  original input image
 */
Picture *Collapse(pyramidType *LaplacianPyramid)
{
  cout << "Collapsing Laplacian Pyramid\n";
  Picture *result = NULL;
  Picture *temp = NULL;
  Matrix M(3, 3);
  M.Set(1, 1, 1);
  M.Set(1, 2, 0);
  M.Set(1, 3, 0);

  M.Set(2, 1, 0);
  M.Set(2, 2, 1);
  M.Set(2, 3, 0);

  M.Set(3, 1, 0);
  M.Set(3, 2, 0);
  M.Set(3, 3, 1);

  result = Expand(&LaplacianPyramid->Images[LaplacianPyramid->Levels - 1]);
  M.Set(1, 1, 1.0 * LaplacianPyramid->Images[LaplacianPyramid->Levels - 2].GetWidth() / result->GetWidth());
  M.Set(2, 2, 1.0 * LaplacianPyramid->Images[LaplacianPyramid->Levels - 2].GetHeight() / result->GetHeight());

  /* warp the image to the right dimensions */
  if ((M.Get(1, 1) != 1) ||
      (M.Get(2, 2) != 1))
    result->Warp(&M, true);
  *result = *result + LaplacianPyramid->Images[LaplacianPyramid->Levels - 2];

  for (int i = LaplacianPyramid->Levels - 3; i >= 0; i--) {
    try {
      temp = Expand(result);
      M.Set(1, 1, 1.0 * LaplacianPyramid->Images[i].GetWidth() / temp->GetWidth());
      M.Set(2, 2, 1.0 * LaplacianPyramid->Images[i].GetHeight() / temp->GetHeight());

      /* warp the image to the right dimensions */
      if ((M.Get(1, 1) != 1) ||
          (M.Get(2, 2) != 1))
        temp->Warp(&M, true);
      *temp = *temp + LaplacianPyramid->Images[i];
      *result = *temp;
    }
    catch (IncompatibleDimensionsException ex) {}
  }
  return result;
}

/*  Perform the Multiresolution splining method
 *  as described in the Burt-Adelson paper
 */
Picture *Combine(Picture *I1, Picture *I2,
                 pointType Max1, pointType Min1,
                 pointType Max2, pointType Min2,
                 pointType overlapMax, pointType overlapMin)
{
  pyramidType *LA = LaplacianPyramid(GaussianPyramid(I1));
  pyramidType *LB = LaplacianPyramid(GaussianPyramid(I2));
  pyramidType *LS = new pyramidType;

  pixelType c1, c2, c;
  bool I1left = Max2.x > Max1.x;
  bool I2left = Max1.x > Max2.x;
  bool I1top = Max1.y > Max2.y;
  bool I2top = Max2.y > Max1.y;
  int dx = abs(min((int) rint(Max1.x), (int) rint(Max2.x)) - max((int) rint(Min1.x), (int) rint(Min2.x)));
  int dy = abs(min((int) rint(Max1.y), (int) rint(Max2.y)) - max((int) rint(Min1.y), (int) rint(Min2.y)));

  if (I1left) cout << "I1 is at the left" << endl;
  if (I2left) cout << "I2 is at the left" << endl;
  if (I1top) cout << "I1 is at the top" << endl;
  if (I2top) cout << "I2 is at the top" << endl;

  cout << "Combining Laplacian Pyramids\n";

  LS->Images = new Picture[LA->Levels](0,0);
  LS->Levels = LA->Levels;
  for (int i = 0; i < LS->Levels; i++) {
    int width = LA->Images[i].GetWidth();
    int height = LA->Images[i].GetHeight();

    LS->Images[i] = LA->Images[i];
    LS->Images[i].Clear();

    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++) {
        try { c1 = LA->Images[i].GetPixel(x, y); }
        catch (IndexOutOfBoundsException ex) {}

        try { c2 = LB->Images[i].GetPixel(x, y); }
        catch (IndexOutOfBoundsException ex) {}

        if ((c1.r == 0) && (c1.g == 0) && (c1.b == 0)) {	/* the first image doesn't overlap here, use the second */
          try { LS->Images[i].SetPixel(x, y, c2); }
          catch (IndexOutOfBoundsException ex) { cerr << "1:"; }
        }
        else if ((c2.r == 0) && (c2.g == 0) && (c2.b == 0)) {	/* the second image doesn't overlap here, use the first */
          try { LS->Images[i].SetPixel(x, y, c1); }
          catch (IndexOutOfBoundsException ex) { cerr << "2:";}
        }
        else {	/* we are at the overlapping region */
          double wx, wy;

          /*  blending from left to right, top to bottom,
           *  depending on which image is the left/top image
           */
          wx = -1;
          wy = -1;
          if (I1left)
            wx = (1.0 * abs((int) rint(overlapMax.x) - x) / dx);
          if (I2left)
            wx = 1 - (1.0 * abs((int) rint(overlapMax.x) - x) / dx);
          if (I1top)
            wy = 1.0 * abs((int) rint(overlapMax.y) - y) / dy;
          if (I2top)
            wy = 1 - (1.0 * abs((int) rint(overlapMax.y) - y) / dy);

          /* normalize the weights, if it exceeds the range -1.0 to 1.0 */
          if (wx > 1.0) wx = 1.0;
          if (wx < -1.0) wx = -1.0;
          if (wy > 1.0) wy = 1.0;
          if (wy < -1.0) wy = -1.0;

          if (wx == -1) {	/* if neither image is to the left of the other */
            c.r = (byte) ((wy * c1.r) + ((1-wy) * c2.r));
            c.g = (byte) ((wy * c1.g) + ((1-wy) * c2.g));
            c.b = (byte) ((wy * c1.b) + ((1-wy) * c2.b));
          }
          else if (wy == -1) {	/* if neither image is at the top of the other */
            c.r = (byte) ((wx * c1.r) + ((1-wx) * c2.r));
            c.g = (byte) ((wx * c1.g) + ((1-wx) * c2.g));
            c.b = (byte) ((wx * c1.b) + ((1-wx) * c2.b));
          }
          else {
            c.r = (byte) ((wx * wy * c1.r) + ((1-wx) * (1-wy) * c2.r));
            c.g = (byte) ((wx * wy * c1.g) + ((1-wx) * (1-wy) * c2.g));
            c.b = (byte) ((wx * wy * c1.b) + ((1-wx) * (1-wy) * c2.b));
          }

          try { LS->Images[i].SetPixel(x, y, c); }
          catch (IndexOutOfBoundsException ex) {cerr << "3:";}
        }
      }

    cout << "Summing up laplacian layer " << i << endl;
    overlapMax.x /= 2;
    overlapMax.y /= 2;
    overlapMin.x /= 2;
    overlapMin.y /= 2;
    dx /= 2;
    dy /= 2;
  }

  return Collapse(LS);
}
