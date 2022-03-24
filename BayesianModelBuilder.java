
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class BayesianModelBuilder {

	HashMap<String, Double> posList = new HashMap<>();		//list of words with # of occurrences of each word
	HashMap<String, Double> negList = new HashMap<>();		//in negative reviews
	static final int POS = 810;			//# of positive reviews
	static final int NEG = 810;			//# of negative reviews
	static final int TOTAL = 1620;		//# of total reviews


	//take the paths to a folder of text files as input
	BayesianModelBuilder(String path1, String path2){

		//open the positive review folder and list the files in it
		File inputFolder = new File(path1);
		File fileList[] = inputFolder.listFiles();
		//count occurrences of every word appears in the files
		this.posList = wordCounter(fileList);

		//negative folder
		inputFolder = new File(path2);
		fileList = inputFolder.listFiles();
		//count occurrences of every word appears in the files
		this.negList = wordCounter(fileList);


		//select useful words using mutual information for positive reviews
		HashMap<String, Double> positiveEvidence = computeIG(posList,negList);

		//Repeat process for negative reviews
		HashMap<String, Double> negativeEvidence = computeIG(negList, posList);

		//Compute probabilities for evidences given positive or negative reviews
		HashMap<String, Double> posWordProbabilities = computePosteriors(positiveEvidence, posList,negList);

		HashMap<String, Double> negWordProbabilities = computePosteriors(negativeEvidence,negList,posList);

		System.out.println(posList.get("finest"));
		try {
			File positiveEvidences = new File("pos_list.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter("pos_list.txt"));
			for(Entry<String, Double> entry : posWordProbabilities.entrySet() ) {
				// put key and value separated by a colon
				bw.write(entry.getKey() + ":"
						+ entry.getValue());

				// new line
				bw.newLine();
			}

			bw.flush();

			File negativeEvidences = new File("neg_list.txt");
			bw = new BufferedWriter(new FileWriter("neg_list.txt"));
			for(Entry<String, Double> entry : negWordProbabilities.entrySet() ) {
				// put key and value separated by a colon
				bw.write(entry.getKey() + ":"
						+ entry.getValue());

				// new line
				bw.newLine();
			}

			bw.flush();
			bw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}




		//Compute best 5 evidences for both positive and negative reviews and print them
	}


	static void findTop5Positive(HashMap<String, ArrayList<Double>> input){

	}


	//count words
	static HashMap<String, Double> wordCounter(File[] fileList){
		HashMap<String, Double> wordList = new HashMap<>();
		for(File inputFile : fileList) {
			try(BufferedReader bfReader = new BufferedReader(new FileReader(inputFile))){
				String line;
				//read file line by line and tokenize each line into an array of words
				while((line = bfReader.readLine()) != null) {
					String[] strArr = line.split("[^a-zA-Z0-9']+");
					for(String word : strArr) {
						if(!(word.equals("") || (word.indexOf("'")==word.length()-1) || (word.indexOf("'")==0))) {
							//if the word seen already, increment the counter; add it to map with counter 1
							if(wordList.containsKey(word))
								wordList.put(word, wordList.get(word)+1);
							else wordList.put(word, 1.0);
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return wordList;
	}

	//select useful features
	static HashMap<String, Double> computeIG(HashMap<String, Double> posList, HashMap<String, Double> negList){
		HashMap<String, Double> sorted = new HashMap<>();

		for(Entry<String, Double> entry : posList.entrySet()) {
			String word = entry.getKey();

			//if word is only in one list, then ignore it
			if(!negList.containsKey(word))
				continue;
			double ig = Math.abs(Math.log(entry.getValue()/negList.get(word))/Math.log(2));
			sorted.put(word, ig);
		}
		return sorted;
	}

	static HashMap<String, Double> computePosteriors(HashMap<String, Double> list,HashMap<String, Double> list1, HashMap<String, Double> list2 ){
		HashMap<String, Double> posteriors = new HashMap<>();

		for(Entry<String, Double> entry : list.entrySet()){
			String word = entry.getKey();
			if(entry.getValue() < 2)
				continue;

			//compute likelihood that word appears in list1
			double likelihoodList1 = (list1.get(word)) / 810.0;

			//initialize normalization constant
			double a;

			//compute likelihood of word in list2
			double likelihoodList2 = (list2.get(word)) / 810;

			//compute normalization constant to make values add up to 1
			a = 1 / (likelihoodList1 + likelihoodList2);

			//compute posterior probability
			double posterior = a * likelihoodList1;
			//store in evidence list
			posteriors.put(word, posterior);
		}

		return posteriors;
	}


}