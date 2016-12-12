package pp4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import Jama.Matrix;

/**
 * class to implement LDA method
 * @author Zhaokun Xue
 *
 */
public class LDA {
	/**
	 * The function parses an input file to a list of strings
	 * @param filename input file name
	 * @return a list of words
	 * @throws IOException input/output exception
	 */
	public static List<String> readData(String filename) throws IOException {

        List<String> words = new ArrayList<String>();
		// try to open and read the file
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
            String fileRead = br.readLine();
            // parse file content by space and add each token to the list
            while (fileRead != null) {
                String[] tokens = fileRead.split(" ");
                List<String> line = new ArrayList<String>(Arrays.asList(tokens));
                words.addAll(line);
                fileRead = br.readLine();
            }
            br.close();
            
        } catch (FileNotFoundException fnfe) {
            System.err.println("file not found");
        }
		return words;
	}
	
	
	/**
	 * The function to read label file
	 * @param filename label file name
	 * @return an array contains all labels
	 * @throws IOException
	 */
	public static double[] readLabels(String filename) throws IOException {

        List<Double> labels = new ArrayList<Double>();
        double[] t = null;
		// try to open and read the file
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
            String fileRead = br.readLine();
            // parse file content by space and add each token to the list
            while (fileRead != null) {
            		String[] tokens = fileRead.split(",");
            		labels.add(Double.parseDouble(tokens[1]));
                fileRead = br.readLine();
            }
            t = new double[labels.size()];
            for (int i = 0; i < t.length; i++) {
                t[i] = labels.get(i);
             }
            br.close();
            
        } catch (FileNotFoundException fnfe) {
            System.err.println("file not found");
        }
		return t;
	}
	
	/**
	 * randomly assign topic to doc
	 * @param k number of topics
	 * @return a random topic
	 */
	public static int randomAssignTopic (int k) {
		int min = 1;
		int max = k;

		Random r = new Random();
		int random_topic = r.nextInt(max - min + 1) + min;
		return random_topic;
	}
	
	/**
	 * Build the indices for the given input
	 * @param path the file path
	 * @param k number of topics
	 * @return the Indices built from give docs
	 * @throws IOException
	 */
	public static Indices formCorpus(String path, int k) throws IOException {
		File folder = new File(path);
		File[] list_of_files = folder.listFiles();
		List<String> words = new ArrayList<String>();
		List<Integer> wn = new ArrayList<Integer>();
		List<Integer> dn = new ArrayList<Integer>();
		List<Integer> zn = new ArrayList<Integer>();
		int n = 0;
		for (int i = 0; i < list_of_files.length; i++) {
			if (list_of_files[i].isFile()) {
				if(!list_of_files[i].getName().contains(".csv")){
					String filename = list_of_files[i].getName();
					List<String> word_i = readData(path + filename);
					int file_size = word_i.size();
					List<Integer> dn_i = Collections.nCopies(file_size, Integer.parseInt(filename));
					List<Integer> zn_i = new ArrayList<Integer>();
					for (int j = 0; j < file_size; j++) {
						zn_i.add(randomAssignTopic(k));
					}
					words.addAll(word_i);
					dn.addAll(dn_i);
					zn.addAll(zn_i);
				}
			}
		}
		n = words.size();

		Set<String> vocabulary = new HashSet<String>(words);
		Set<String> sorted_vocabulary = new TreeSet<String>(vocabulary);
		List<String> vocabulary_list = new ArrayList<String>(sorted_vocabulary);

		for (int j = 0; j < n; j++) {
			String word = words.get(j);
			int index = vocabulary_list.indexOf(word);
			wn.add(index);
		}
		Indices corpus_instance = new Indices(wn, dn, zn, n, vocabulary_list);
		return corpus_instance;
	}
	
	/**
	 * Find the most frequency words based of the LDA results
	 * @param m the number of most frequency words
	 * @param ct the C_t matrix
	 * @param vocabulary vocabulary of given docs
	 * @return return most frequencey words for each topic
	 */
	public static List<List<String>> findTheNLargest(int m, Matrix ct, List<String> vocabulary) {
	    int row = ct.getRowDimension(); 
	    int column = ct.getColumnDimension();
	    double[][] ct_array = ct.getArray();
	    Matrix nLargest = new Matrix(row, m);
	    double max = 0;
	    int index;
	    List<List<String>> nLargest_words = new ArrayList<List<String>>();
	    for (int k = 1; k < row; k++) {
	    		List<String> nLargest_words_topick = new ArrayList<String>();
	    		for (int j = 0; j < m; j++) {
	    			max = ct_array[k][0];
	    			index = 0;
	    			for (int l = 1; l < column; l++) {
	    				if (max < ct_array[k][l]) {
	    					max = ct_array[k][l];
	    					index = l;
	    				}
	    			}
	    			nLargest_words_topick.add(vocabulary.get(index));
	    			nLargest.set(k, j, max);
	    			ct_array[k][index] = Double.MIN_VALUE;
	    		}
	    		nLargest_words.add(nLargest_words_topick);
	    }
	    return nLargest_words;
	}
	
	/**
	 * Build the bag of words input
	 * @param corpus_params the corpus for the given docs
	 * @return a matrix fog bag of words input
	 */
	public static Matrix formBagOfWords(Indices corpus_params) {
		List<String> vocabulary = corpus_params.getVocabulary();
		List<Integer> wn = corpus_params.getWn();
		List<Integer> dn = corpus_params.getDn();
		Set<Integer> dn_set = new HashSet<Integer>(dn);
		int column = vocabulary.size();
		int row = dn_set.size() + 1;
		Matrix bag_of_words = new Matrix (row-1, column);
		
		for (int doc : dn_set) {
			double total_words_doc = Collections.frequency(dn, doc);
			for (int i = 0; i < dn.size(); i++) {
				if (dn.get(i) == doc) {
					int word = wn.get(i);
					double update = bag_of_words.get(doc-1, word) + 1;
					bag_of_words.set(doc-1, word, update);
				}
			}
			for (int j = 0; j < column; j++) {
				double counter = bag_of_words.get(doc-1, j);
				double freq = counter / total_words_doc;
				bag_of_words.set(doc-1, j, freq);
			}
		}
		
		return bag_of_words;
	}
	
	/**
	 * main method for project 4
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		int k = 20;
		Indices test = LDA.formCorpus("data/newsgroups/", k);
		String task1_output = "topicwords.csv";
		System.out.println("****** Task 1 topic words ******");
		GibbsSampler gs = new GibbsSampler(test, k);
		GibbsResults gr = gs.implementAlgorithm(500);
		Matrix topic_representation = gs.formDocTopicRepresentation(gr.getCd());
		List<List<String>> topics_results = LDA.findTheNLargest(5, gr.getCt(), test.getVocabulary());
		PrintWriter writer  = new PrintWriter(task1_output, "UTF-8");

		for(int i = 0; i < topics_results.size(); i++) {
			System.out.print("Topic " + (i + 1) + ": ");
		    String collect = topics_results.get(i).stream().collect(Collectors.joining(","));
		    System.out.println(collect);
		    writer.println(collect);
		}

	    writer.close();
	    System.out.println("****** Task 2 Results *****");
		Matrix bow = LDA.formBagOfWords(test);
		double[] labels = LDA.readLabels("data/newsgroups/index.csv");
		String topic_method = "topic-representation";
		String bow_method = "bow";
		System.out.println(">>>>> Results to Topic Represenations <<<<<");
		Task2.runTask2(topic_representation, labels, topic_method);
		System.out.println(">>>>>> Results to Bag-of-wrods <<<<<<");
		Task2.runTask2(bow, labels, bow_method);
		
	}

}
