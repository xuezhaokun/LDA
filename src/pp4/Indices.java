package pp4;

import java.util.List;
import java.util.Set;

public class Indices {
	private List<Integer> wn;
	private List<Integer> dn;
	private List<Integer> zn;
	private int n;
	private List<String> vocabulary;
	
	public Indices(List<Integer> wn, List<Integer> dn, List<Integer> zn, int n, List<String> vocabulary) {
		this.wn = wn;
		this.dn = dn;
		this.zn = zn;
		this.n = n;
		this.vocabulary = vocabulary;
	}

	public List<Integer> getWn() {
		return wn;
	}
	public void setWn(List<Integer> wn) {
		this.wn = wn;
	}
	public List<Integer> getDn() {
		return dn;
	}
	public void setDn(List<Integer> dn) {
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

	public List<String> getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(List<String> vocabulary) {
		this.vocabulary = vocabulary;
	}	
	
}
