//Youssef Aboul-Naja  (Student#: 991489073)
//Hoa Nguyen  (Student#: 991043116)

import cern.colt.matrix.impl.*;
import cern.colt.matrix.*;
import cern.colt.matrix.linalg.*;

/**
 * This class is a Linear System Solver.  It was provided as a 
 * starter code.  This class uses the colt library.
 */ 
public class MatrixSolver{
  
	private Algebra a=new Algebra();
	private DoubleMatrix2D mapMatrix;
	  
	/**
	* This method uses backward mapping to find the x-coordinate
	* in texture image that relates to the point (r,c) in 
	* final image.
	*/ 
	public double texU(double r, double c){
		double [] x1={r, c, 1};
		DoubleMatrix1D x=new DenseDoubleMatrix1D(x1);
		DoubleMatrix1D y=a.mult(mapMatrix, x);
		return y.get(0)/y.get(2);
	}
  
	/**
	* This method uses backward mapping to find the y-coordinate
	* in texture image that relates to the point (r,c) in 
	* final image.
	*/
	public double texV(double r, double c){
		double [] x1={r, c, 1};
		DoubleMatrix1D x=new DenseDoubleMatrix1D(x1);
		DoubleMatrix1D y=a.mult(mapMatrix, x);
		return y.get(1)/y.get(2);
	}
    
	/**
	* Constructor.
	* Sets up a Linear System Solver.
	* Parameters [u1,v1,u2,v2,u3,v3,u4,v4] are from the texture
	* image; and they map to [r1,c1,r2,c2,r3,c3,r4,c4] in the 
	* final image.
	*/ 
	public MatrixSolver(int u1, int v1, int u2, int v2, int u3, int v3, int u4, int v4, 
						int r1, int c1, int r2, int c2, int r3, int c3, int r4, int c4){
		double [][] d1={
			{ -r1, -c1, -1,  0,   0,  0, u1*r1, u1*c1 },
			{ 0  ,   0,  0,-r1, -c1, -1, v1*r1, v1*c1 },
			{ -r2, -c2, -1,  0,   0,  0, u2*r2, u2*c2 },
			{ 0  ,   0,  0,-r2, -c2, -1, v2*r2, v2*c2 },
			{ -r3, -c3, -1,  0,   0,  0, u3*r3, u3*c3 },
			{ 0  ,   0,  0,-r3, -c3, -1, v3*r3, v3*c3 },
			{ -r4, -c4, -1,  0,   0,  0, u4*r4, u4*c4 },
			{ 0  ,   0,  0,-r4, -c4, -1, v4*r4, v4*c4 }
		};
		DoubleMatrix2D A=new DenseDoubleMatrix2D(d1);
		double [][] d2= { { -u1 }, { -v1 } , { -u2 } , { -v2 } , { -u3 } , { -v3 } , { -u4 } , { -v4 } };
		DoubleMatrix2D b=new DenseDoubleMatrix2D(d2);
		DoubleMatrix2D s=a.solve(A,b);
		double [][] t1={
			{ s.get(0,0), s.get(1,0), s.get(2,0) },
			{ s.get(3,0), s.get(4,0), s.get(5,0) },
			{ s.get(6,0), s.get(7,0), 1  },
      
		};
		mapMatrix=new DenseDoubleMatrix2D(t1);
	}
  
	/**
	 * returns a string representation of the resultant matrix
	 */ 
	public String toString(){ return mapMatrix.toString(); }
  
}