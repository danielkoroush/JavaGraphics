import cern.colt.matrix.impl.*;
import cern.colt.matrix.*;
import cern.colt.matrix.linalg.*;

// assuming that colt.jar is in the same directory...
// javac -classpath .:colt.jar TextureMap.java 
// java -cp .:colt.jar TextureMap

public class TextureMap {

	private Algebra a=new Algebra();
	public DoubleMatrix2D mapMatrix;

	public static void main(String [] args){
		demo( // Identity
			130,79,179,88,127,136,176,151,
			12,8,60,17,8,65,57,79
		);
		/* demo( // Scale
			0,0,10,0,10,10,0,10,
			0,0,20,0,20,20,0,20
		);
		demo( // Translate
			0,0,10,0,10,10,0,10,
			10,0,20,0,20,10,10,10
		);
		demo( // Shear
			0,0,10,0,10,10,0,10,
			10,0,20,0,10,10,0,10
		);
		demo( // Rotate 90
			0,0,10,0,10,10,0,10,
			10,0,10,10,0,10,0,0
		); */
	
	}
	public static void demo( 
		int u1, int v1, int u2, int v2, int u3, int v3, int u4, int v4, 
		int r1, int c1, int r2, int c2, int r3, int c3, int r4, int c4
	){
		System.out.println("-------------------");
		TextureMap t=new TextureMap (
			u1,  v1,  u2,  v2,  u3,  v3,  u4,  v4, 
			r1,  c1,  r2,  c2,  r3,  c3,  r4,  c4
		);
		System.out.println(t);
		System.out.println("Applying the above matrix to (-1,3) results in ("+ t.texU(-1,3)+","+ t.texV(-1,3)+")");
	}

	public double texU(double r, double c){
		double [] x1={r, c, 1};
		DoubleMatrix1D x=new DenseDoubleMatrix1D(x1);
		DoubleMatrix1D y=a.mult(mapMatrix, x);
		return y.get(0)/y.get(2);
	}

	public double texV(double r, double c){
		double [] x1={r, c, 1};
		DoubleMatrix1D x=new DenseDoubleMatrix1D(x1);
		DoubleMatrix1D y=a.mult(mapMatrix, x);
		return y.get(1)/y.get(2);
	}

	public TextureMap(
		int u1, int v1, int u2, int v2, int u3, int v3, int u4, int v4, 
		int r1, int c1, int r2, int c2, int r3, int c3, int r4, int c4
	){
		/*
		e1:=-r1* a11 - c1* a12 - a13 + u1* r1* a31 + u1* c1* a32=-u1;
		e2:=-r1* a21 - c1* a22 - a23 + v1* r1* a31 + v1* c1* a32=-v1;
		e3:=-r2* a11 - c2* a12 - a13 + u2* r2* a31 + u2* c2* a32=-u2;
		e4:=-r2* a21 - c2* a22 - a23 + v2* r2* a31 + v2* c2* a32=-v2;
		e5:=-r3* a11 - c3* a12 - a13 + u3* r3* a31 + u3* c3* a32=-u3;
		e6:=-r3* a21 - c3* a22 - a23 + v3* r3* a31 + v3* c3* a32=-v3;
		e7:=-r4* a11 - c4* a12 - a13 + u4* r4* a31 + u4* c4* a32=-u4;
		e8:=-r4* a21 - c4* a22 - a23 + v4* r4* a31 + v4* c4* a32=-v4;
		*/

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
	public String toString(){ return mapMatrix.toString(); }
}
