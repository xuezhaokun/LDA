package pp4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Jama.Matrix;
/**
 * the class to run experiments in task 1
 * @author zhaokunxue
 *
 */
public class Task2 {
	
	/**
	 * make predictions with different method 
	 * @param data_with_labels given dataset
	 * @return a hashmap with prediction results
	 */
	public static List<HashMap<Integer, List<Double>>> predict(double[][] data_with_labels) { 
		int data_size = data_with_labels.length;
		int testing_size = data_size / 3;
		int training_size = data_size - testing_size;
		HashMap<Integer, List<Double>> discriminative_predict_results = new HashMap<Integer, List<Double>>();
		for (int i = 0; i < 30; i++) {
			double[][] shuffled_data = ClassificationAlg.shuffleData(data_with_labels);
			List<double[][]> splitted_data = ClassificationAlg.splitTestAndTrain(shuffled_data, testing_size);
			double[][] training_data_with_labels = splitted_data.get(0);
			double[][] testing_data_with_labels = splitted_data.get(1);
			double[][] testing_data = ClassificationAlg.getDataFromDataWithLabels(testing_data_with_labels);
			double[] testing_labels = ClassificationAlg.getLabelsFromDataWithLabels(testing_data_with_labels);
			
			for (int k = 1; k < 11; k++) {
				int n = 0;
				if (k == 10) {
					n = training_size;
				} else {
					n = (training_size/10) * k;
				}
				double[][] current_training_data_with_labels = ClassificationAlg.getFirstNData(training_data_with_labels, n);
				DiscriminativeAlg.discriminativePredict(discriminative_predict_results, current_training_data_with_labels, testing_data, testing_labels, n);
			}
		}
		List<HashMap<Integer, List<Double>>> predicts = new ArrayList<HashMap<Integer, List<Double>>>();
		predicts.add(discriminative_predict_results);
		return predicts;
	}

	/**
	 * get error rates statistics
	 * @param predicts_errors a hashamp stores prediction error rates
	 * @return a hashmap with error rates statistics
	 */
	public static HashMap<Integer, double[]> getStatics(HashMap<Integer, List<Double>> predicts_errors) {
		HashMap<Integer, double[]> error_statics = new HashMap<Integer, double[]>();
		for (Map.Entry<Integer, List<Double>> entry : predicts_errors.entrySet()) {
		    int key = entry.getKey();
		    List<Double> errors = entry.getValue();
		    double mean = PerformanceStat.calcMean(errors); 
		    double std = PerformanceStat.calStD(errors);
		    double[] statics = new double[2];
		    statics[0] = mean;
		    	statics[1] = std;
		    error_statics.put(key, statics);
		}
		return error_statics;
	}
	
	/**
	 * run the experiments in task1
	 * @throws Exception
	 */
	public static void runTask2 (Matrix dataset_matrix, double[] labels, String representation) throws Exception{

		String discriminative_output = "results/discriminative-predicts-" + representation + ".csv"; 
		
		double[][] dataset = dataset_matrix.getArray();
		double[][] data_with_labels = ClassificationAlg.combineDataWithLabels(dataset, labels);
		List<HashMap<Integer, List<Double>>> predicts_results = predict(data_with_labels);
		HashMap<Integer, List<Double>> discriminative_predicts = predicts_results.get(0);
		HashMap<Integer, double[]> discriminative_predicts_stat = getStatics(discriminative_predicts);
		ClassificationAlg.writeTask2ToFile(discriminative_predicts_stat, discriminative_output);	
	}
}
