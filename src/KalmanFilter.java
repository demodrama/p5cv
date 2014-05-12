import Jama.Matrix;

/**
 * This work is licensed under a Creative Commons Attribution 3.0 License.
 * 
 * @author Ahmed Abdelkader
 */

public class KalmanFilter {
	private Matrix X;
	private Matrix X0;
	private Matrix F;
	private Matrix B;
	private Matrix U;
	private Matrix Q;
	private Matrix H;
	private Matrix R;
	private Matrix P;
	private Matrix P0;

	public void predict() {
		X0 = F.times(X).plus(B.times(U));
		P0 = F.times(P).times(F.transpose()).plus(Q);
	}

	public void correct(Matrix Z) {
		Matrix S = H.times(P0).times(H.transpose()).plus(R);

		Matrix K = P0.times(H.transpose()).times(S.inverse());

		X = X0.plus(K.times(Z.minus(H.times(X0))));

		Matrix I = Matrix.identity(P0.getRowDimension(),
				P0.getColumnDimension());
		P = (I.minus(K.times(H))).times(P0);

	}

	// getters and setters go here

	public Matrix getX() {
		return X;
	}

	public void setX(Matrix x) {
		X = x;
	}

	public Matrix getX0() {
		return X0;
	}

	public void setX0(Matrix x0) {
		X0 = x0;
	}

	public Matrix getF() {
		return F;
	}

	public void setF(Matrix f) {
		F = f;
	}

	public Matrix getB() {
		return B;
	}

	public void setB(Matrix b) {
		B = b;
	}

	public Matrix getU() {
		return U;
	}

	public void setU(Matrix u) {
		U = u;
	}

	protected Matrix getQ() {
		return Q;
	}

	protected void setQ(Matrix q) {
		Q = q;
	}

	public Matrix getH() {
		return H;
	}

	public void setH(Matrix h) {
		H = h;
	}

	public Matrix getR() {
		return R;
	}

	public void setR(Matrix r) {
		R = r;
	}

	public Matrix getP() {
		return P;
	}

	public void setP(Matrix p) {
		P = p;
	}

	public Matrix getP0() {
		return P0;
	}

	public void setP0(Matrix p0) {
		P0 = p0;
	}

}