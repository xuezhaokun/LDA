package pp4;

import java.util.List;

public class Indices {
	private List<String> wn;
	private List<String> dn;
	private List<Integer> zn;
	private int n;
	
	public Indices(List<String> wn, List<String> dn, List<Integer> zn, int n) {
		this.wn = wn;
		this.dn = dn;
		this.zn = zn;
		this.n = n;
	}
	public List<String> getWn() {
		return wn;
	}
	public void setWn(List<String> wn) {
		this.wn = wn;
	}
	public List<String> getDn() {
		return dn;
	}
	public void setDn(List<String> dn) {
		this.dn = dn;
	}
	public List<Integer> getZn() {
		return zn;
	}
	public void setZn(List<Integer> zn) {
		this.zn = zn;
	}
	
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}	
	
}
