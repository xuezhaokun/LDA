package pp4;

import java.util.List;

import Jama.Matrix;

public class GibbsResults {
	private List<Integer> zn;
	private Matrix cd;
	private Matrix ct;
	
	public GibbsResults (List<Integer> zn, Matrix cd, Matrix ct) {
		this.zn = zn;
		this.cd = cd;
		this.ct = ct;
	}

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
