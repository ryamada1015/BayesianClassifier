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
		this.posList = wordCounter(fileList);
		
		
		//select useful words 
		
	}
	
	//count words 
	Map<String, Integer> wordCounter(File[] fileList){
		Map<String, Integer> wordList = new HashMap<>();
		for(File inputFile : fileList) {
			try(BufferedReader bfReader = new BufferedReader(new FileReader(inputFile))){
				String line;
				//read file line by line and tokenize each line into an array of words 
				while((line = bfReader.readLine()) != null) {
					String[] strArr = line.split("\\.|\\,|\\(|\\)|\"|\\s|\\?|-|\\n|\\t");
					for(String word : strArr) {
						if(!(word.equals("") || word.equals("'"))) {
							//if the word seen already, increment the counter; add it to map with counter 1
							if(wordList.containsKey(word))
								wordList.put(word, wordList.get(word)+1);
							else wordList.put(word, 1);
						}
					}
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return wordList;
	}
	
	//select useful words 
	Map<String, Integer> selectWords(Map<String, Integer> list1, Map<String, Integer> list2){
		Map<String, Integer> wordList = new HashMap<>();
		
		for(Entry<String, Integer> entry : list1.entrySet()) {
			String word = entry.getKey();
			if(list2.containsKey(word)) continue;
			double x = Math.abs((Math.log(entry.getValue()/list2.get(word)))/Math.log(2));
		}
		
		
		return wordList;
	}
	
	
	//compute likelihood of a word 
	
	//classify 
	

	
}
