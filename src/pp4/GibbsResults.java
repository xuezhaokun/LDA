package pp4;

import java.util.List;

import Jama.Matrix;
/**
 * Class for holding Gibbs Sampling results
 * @author Zhaokun Xue
 *
 */
public class GibbsResults {
	private List<Integer> zn;
	private Matrix cd;
	private Matrix ct;
	
	/**
	 * constructor for the class
	 * @param zn topics zn
	 * @param cd doc matrix cd
	 * @param ct topic matrix ct
	 */
	public GibbsResults (List<Integer> zn, Matrix cd, Matrix ct) {
		this.zn = zn;
		this.cd = cd;
		this.ct = ct;
	}

	/**
	 * getters and setters 
	 * 
	 */
	public List<Integer> getZn() {
		return zn;
	}

	public void setZn(List<Integer> zn) {
		this.zn = zn;
	}

	public Matrix getCd() {
		return cd;
	}

	public void setCd(Matrix cd) {
		this.cd = cd;
	}

	public Matrix getCt() {
		return ct;
	}

	public void setCt(Matrix ct) {
		this.ct = ct;
	}
	
	
}
