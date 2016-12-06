package pp4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import Jama.Matrix;

public class GibbsSampler {
	private Indices corpus_params;
	private int k;

	
	public GibbsSampler (Indices corpus_params, int k) {
		this.corpus_params = corpus_params;
		this.k = k;
	}

	public GibbsResults implementAlgorithm(int n_iters) {
		double alpha = 50/(double)k;
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
		int v = vocabulary.size();
		double d = docs.size();
		Matrix alpha1s = new Matrix(k+1, 1).transpose(); // K*1
		alpha1s.times(alpha);
		Matrix beta1s = new Matrix (v, 1).transpose(); // V*1
		beta1s.times(beta);
		Matrix cd = initializeCd();
		Matrix ct = initializeCt();
		Matrix p = new Matrix(1, k+1);
		
		Collections.shuffle(pi_n);
		for (int i = 0; i < n_iters; i++) {
			for (int n = 0; n < n_words; n++) {
				int index = pi_n.get(n);
				int word = wn.get(index);
				int topic = zn.get(index);
				int doc = dn.get(index);
				double cd_old = cd.get(doc, topic);
				cd.set(doc, topic, (cd_old - 1));
				double ct_old = ct.get(topic, word); 
				ct.set(topic, word, (ct_old - 1));
				p = calculateP(doc, word, v, cd, ct);
				p = normalizeP(p);
				topic = sampleFromP(p);
				zn.set(index, topic);
				
				double cd_now = cd.get(doc, topic);
				cd.set(doc, topic, (cd_now + 1));
				double ct_now = ct.get(topic, word); 
				ct.set(topic, word, (ct_now + 1));
				
			}
		}
		
		GibbsResults gr = new GibbsResults(zn, cd, ct);
//		System.out.println("--------");
//		System.out.println(Arrays.deepToString(p.getArray()));
//		System.out.println("--------");
		return gr;
	}
	
	public Matrix initializeCd() {

		List<Integer> dn = corpus_params.getDn();
		List<Integer> zn = corpus_params.getZn();
		Set<Integer> docs = new HashSet<Integer>(dn);
		int d = docs.size();
		Matrix cd = new Matrix(d+1, k+1);
		for(int i = 0; i < dn.size(); i++) {
			int doc = dn.get(i);
			int topic = zn.get(i);
			double old = cd.get(doc, topic);
			cd.set(doc, topic, (old + 1));
		}
		return cd;
	}
	
	public Matrix initializeCt() {
		List<Integer> zn = corpus_params.getZn();
		List<Integer> wn = corpus_params.getWn();
		Set<Integer> vocabulary = new HashSet<Integer>(wn);
		int v = vocabulary.size();
		Matrix ct = new Matrix(k+1, v);
		for (int i = 0; i < wn.size(); i++) {
			int word = wn.get(i);
			int topic = zn.get(i);
			double old = ct.get(topic, word);
			ct.set(topic, word, (old + 1));
		}
		return ct;
	}
	
	public double calcCtRowJSum(int k, Matrix ct) {
		int ct_column = ct.getColumnDimension();
		double ct_row_j_sum = 0;
		for (int j = 0; j < ct_column; j++) {
			ct_row_j_sum += ct.get(k, j);
		}
		return ct_row_j_sum;
	}
	
	
	public Matrix calculateP(int doc, int word, int v, Matrix cd, Matrix ct) {
		double alpha = 50/(double)k;
		double beta = 0.1;
		Matrix p = new Matrix(1, k+1);
		int cd_column = cd.getColumnDimension();
		double cd_row_l_sum = 0;
		
		for (int l = 0; l < cd_column; l++) {
			cd_row_l_sum += cd.get(doc, l);
		}
		
		for (int i = 1; i < k + 1; i++) {
			double temp1 = (ct.get(i, word) + beta) / (v * beta + calcCtRowJSum(i, ct));
			double temp2 = (cd.get(doc, i) + alpha) / ((double) k * alpha + cd_row_l_sum);
			double p_k = temp1 * temp2;
			p.set(0, i, p_k);
		}
		return p;
		
	}
	
	public Matrix normalizeP(Matrix p) {
		double denominator = 0;
		int p_column = p.getColumnDimension();
		for (int i = 0; i < p_column; i++) {
			denominator += p.get(0, i);
		}
		for (int i = 0; i < p_column; i++) {
			double normalized = p.get(0, i)/denominator;
			p.set(0, i, normalized);
		}
		return p;
	}
	
	public int sampleFromP(Matrix p) {
		HashMap<Integer, double[]> topic_intervals = new HashMap<Integer, double[]>();
		double lower = 0;
		int topic = 0;
		int p_column = p.getColumnDimension();
		for (int i = 1; i < p_column; i++) {
			double upper = lower + p.get(0, i);
			double[] lower_upper = new double[] {lower, upper};
			topic_intervals.put(i, lower_upper);
			lower = upper;
		}
		double predict = Math.random();
		for (Map.Entry<Integer, double[]> entry : topic_intervals.entrySet()) {
		    int key = entry.getKey();
		    double[] lower_upper = entry.getValue();
		    double lower_bound = lower_upper[0];
		    double upper_bound = lower_upper[1];
		    if (predict > lower_bound && predict <= upper_bound){
		    		topic = key;
		    		break;
		    }
		}
		return topic;
	}
	
	public Matrix formDocTopicRepresentation(Matrix cd) {
		int row = cd.getRowDimension();
		int column = cd.getColumnDimension();
		double alpha = 50/(double)k;
		Matrix output = new Matrix(row, column);
		double cd_row_l_sum = 0;
		
		for (int doc = 1; doc < row; doc++) {
			for (int l = 0; l < column; l++) {
				cd_row_l_sum += cd.get(doc, l);
			}
			for (int topic = 1; topic < column; topic++) {
				double temp = (cd.get(doc, topic) + alpha) / ((double) k * alpha + cd_row_l_sum);
				output.set(doc, topic, temp);
			}
		}
		return output;
	}
	
	public Indices getCorpus_params() {
		return corpus_params;
	}

	public void setCorpus_params(Indices corpus_params) {
		this.corpus_params = corpus_params;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}
	
	
}
