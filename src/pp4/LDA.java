package pp4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

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
	
	public static int randomAssignTopic (int k) {
		int min = 1;
		int max = k;

		Random r = new Random();
		int random_topic = r.nextInt(max - min + 1) + min;
		return random_topic;
	}
	
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
		Set<String> sorted_vocabulary = new TreeSet(vocabulary);
		List<String> vocabulary_list = new ArrayList<String>(sorted_vocabulary);

		System.out.println(Arrays.toString(vocabulary_list.toArray()));
		for (int j = 0; j < n; j++) {
			String word = words.get(j);
			int index = vocabulary_list.indexOf(word);
			System.out.println(word + " > " + index);
			wn.add(index);
		}
		Indices corpus_instance = new Indices(wn, dn, zn, n);
		return corpus_instance;
	}
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//List<String> words = LDA.readData("data/haha");
		//List<String> words2 = LDA.readData("data/haha2");
		int k = 2;
//		List<String> totalWords = new ArrayList<String> ();
//		totalWords.addAll(words);
//		totalWords.addAll(words2);
		Indices test = LDA.formCorpus("data/test/", k);
		List<Integer> wn = test.getWn();
		List<Integer> dn = test.getDn();
		List<Integer> zn = test.getZn();
		for (int i = 0; i < wn.size(); i++) {
			System.out.println(i + " : " + wn.get(i) + " : " + dn.get(i) + " : " + zn.get(i));
		}
		System.out.println(Arrays.toString(wn.toArray()));
		System.out.println("total words: " + test.getN());

		Set<Integer> docs = new HashSet<Integer>(dn);
		System.out.println("# of docs: " + docs.size());
	}

}
