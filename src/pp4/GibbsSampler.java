package pp4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Jama.Matrix;

public class GibbsSampler {
	private Indices corpus_params;

	
	public GibbsSampler (Indices corpus_params) {
		this.corpus_params = corpus_params;
	}

	public GibbsResults implementAlgorithm(double k, int n_iters) {
		double alpha = 50/k;
		double beta = 0.1;
		List<Integer> wn = corpus_params.getWn();
		List<Integer> zn = corpus_params.getZn();
		List<Integer> dn = corpus_params.getDn();
		int n_words = corpus_params.getN();
		List<Integer> pi_n = new ArrayList<Integer>();
		for (int m = 0; m < n_words; m++) {
			pi_n.add(m);
		}
		Set<Integer> vocabulary = new HashSet<Integer>(wn);
		Set<Integer> docs = new HashSet<Integer>(dn);
		double v = vocabulary.size();
		double d = docs.size();
		Matrix alpha1s = new Matrix((int) k, 1).transpose(); // K*1
		alpha1s.times(alpha);
		Matrix beta1s = new Matrix((int) v, 1).transpose(); // V*1
		beta1s.times(beta);
		Matrix cd = new Matrix((int) d, (int) k);
		Matrix ct = new Matrix((int) k, (int) v);
		Matrix p = new Matrix(1, (int) k);
		
		Collections.shuffle(pi_n);
//		for (int i = 0; i < n_iters; i++) {
//			for (int n = 0; n < n_words; n++) {
//				int index = pi_n.get(n);
//				String word = wn.get(index);
//				int topic = zn.get(index);
//				int doc = dn.get(index);
//				cd.set(doc, topic, -1);
//				ct.set(topic, arg1, arg2);
//			}
//		}
		
		return null;
	}
	
	public Indices getCorpus_params() {
		return corpus_params;
	}

	public void setCorpus_params(Indices corpus_params) {
		this.corpus_params = corpus_params;
	}
	
	
}
