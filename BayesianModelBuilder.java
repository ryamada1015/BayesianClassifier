package assignment2b1;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class BayesianModelBuilder {

	Map<String, Integer> posList = new HashMap<>();		//list of words with # of occurrences of each word
	Map<String, Integer> negList = new HashMap<>();		//in negative reviews
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
		HashMap<String, Integer> positiveEvidence = new HashMap<>();
		positiveEvidence = selectWords(posList,negList);


		//Repeat process for negative reviews
		HashMap<String, Integer> negativeEvidence = new HashMap<>();
		negativeEvidence = selectWords(negList, posList);

		//Compute probabilities given evidences for both negative and positive reviews
		HashMap<String, ArrayList<Double>> posWordProbabilities = conditionalProbabilities(positiveEvidence,
				posList,negList);

		HashMap<String, ArrayList<Double>> negWordProbabilities = conditionalProbabilities(negativeEvidence,
				posList,negList);

		
		
		
		//sort posteriors
		LinkedHashMap<String, Double> sortedP = new LinkedHashMap<>();
		posWordProbabilities.entrySet().stream().sorted(Map.Entry.comparingByValue())
		.forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
		
		LinkedHashMap<String, Double> sortedN = new LinkedHashMap<>();
		negWordProbabilities.entrySet().stream().sorted(Map.Entry.comparingByValue())
		.forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
		
		
		
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
	
	//compute likelihoods
	static HashMap<String, Double> computeLikelihoods(HashMap<String, Double> list){
		HashMap<String, Double> likelihoods = new HashMap<>();

		for(Entry<String, Double> entry : list.entrySet()) {
			double likelihood = entry.getValue()/810.0;
			likelihoods.put(entry.getKey(), likelihood);
		}
		
		return likelihoods;
	}

	//select useful features 
	static HashMap<String, Double> computeIG(HashMap<String, Double> posList, HashMap<String, Double> negList){
		HashMap<String, Double> sorted = new HashMap<>();
		
		for(Entry<String, Double> entry : posList.entrySet()) {
			String word = entry.getKey();
			
			//if word is only in one list, then ignore it 
			if(!negList.containsKey(word)) continue;
			double ig = Math.abs(Math.log(entry.getValue()/negList.get(word)))/Math.log(2);
			sorted.put(word, ig);
		}
		return sorted;
	}


	//compute likelihoods (Conditional Probabilities of word)
	static HashMap<String, ArrayList<Double>> conditionalProbabilities(Map<String,Integer> wordList,Map<String,
			Integer> posList, Map<String, Integer> negList ){
		HashMap<String,ArrayList<Double>> wordProbabilities = new HashMap<>();
		
		//P(Positive | word) = a * P(word | Positive) * P(Positive)
		for(Map.Entry<String,Integer> entry : wordList.entrySet()){
			
			//How likely does the word appear in all reviews
			double p_Word = entry.getValue() / 1620.0;
			
			//How likely does the word appear given positive
			double p_WordGivenPos = 0;
			if(posList.containsKey(entry.getKey()))
				p_WordGivenPos = posList.get(entry.getKey()) / 810.0;
			
			//How likely does the word appear given negative
			double p_WordGivenNeg = 0;
			if(negList.containsKey(entry.getKey()))
				p_WordGivenNeg = negList.get(entry.getKey()) / 810.0;
			
			// compute P(Positive | word) without constant a
			double p_PositiveGivenWord = 0.5 * p_WordGivenPos;
			double p_NegativeGivenWord = 0.5 * p_WordGivenNeg;
			
			// find normalization constant
			double a = 1 / (p_PositiveGivenWord + p_NegativeGivenWord);
			//Store word with values of P(Positive|word) in the hashmap
			ArrayList<Double> condProbabilitiesGivenWord = new ArrayList<>();
			condProbabilitiesGivenWord.add(a * p_PositiveGivenWord);
			condProbabilitiesGivenWord.add(a * p_NegativeGivenWord);
			wordProbabilities.put(entry.getKey(), condProbabilitiesGivenWord);
		}
		return wordProbabilities;
	}






}
