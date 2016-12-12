package pp4;

import Jama.Matrix;

/**
 * the class defines update method for w
 * @author zhaokunxue
 *
 */
public class UpdateMethod {
	/**
	 * newton update for w
	 * @param phi_matrix data matrix
	 * @param t_matrix label matrix
	 * @param r R
	 * @param y y
	 * @param oldW old w
	 * @return new w updated by newton method
	 */
	public static Matrix updateNewtonW(Matrix phi_matrix, Matrix t_matrix, Matrix r, Matrix y, Matrix oldW) {
		int dimension = phi_matrix.getColumnDimension();
		double alpha = 0.1;
		Matrix identity = Matrix.identity(dimension, dimension).times(alpha);
		Matrix hessian = identity.plus(phi_matrix.transpose().times(r).times(phi_matrix));
		Matrix error_part1 = phi_matrix.transpose().times(y.minus(t_matrix));
		Matrix error_part2 = oldW.times(alpha);
		Matrix error_gradient = error_part1.plus(error_part2);
		Matrix hessian_inverse = ClassificationAlg.ludecompForInvert(hessian);
		Matrix update_part = hessian_inverse.times(error_gradient);
		Matrix newW = oldW.minus(update_part);
		return newW;
	}
	
	/**
	 * gradient update for w
	 * @param phi_matrix data matrix
	 * @param t_matrix label matrix
	 * @param y y 
	 * @param oldW old w
	 * @return new w updated by gradient
	 */
	public static Matrix updateGradientW(Matrix phi_matrix, Matrix t_matrix, Matrix y, Matrix oldW) {
		double alpha = 0.1;
		double eta = Math.pow(10, -3);
		Matrix diff = y.minus(t_matrix);
		Matrix part1 = phi_matrix.transpose().times(diff);
		Matrix part2 = oldW.times(alpha);
		Matrix temp = part1.plus(part2);
		Matrix update = temp.times(eta);
		Matrix newW = oldW.minus(update);
		return newW;
	}
	
}
