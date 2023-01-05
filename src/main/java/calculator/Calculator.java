package calculator;

import java.io.*;
import java.util.*;
public class Calculator {
	public static void main(String args[]){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("config.properties");
		
		Map<Object, String> propMap = loadMap(inputStream);	
		
		List<String> inputLines = new ArrayList<String>();
		
		inputLines = readFile(propMap.get("inputPath"));
		
		List<String> outputLines = formOutput(inputLines);  
		
    	writeToOutput(outputLines,propMap.get("outputPath"));		
	}

	private static Map<Object, String> loadMap(InputStream inputStream){
		Properties props = new Properties();
		try {
			props.load(inputStream);
			inputStream.close();
		}catch (IOException e) {
			throw new BusinessException(e.getLocalizedMessage());
		}
		Map<Object,String> propMap = new HashMap<Object,String>();
		for(Object str: props.keySet()) {
			String value = props.getProperty((String) str);
			System.out.println(str+" - "+value);
			propMap.put(str, value);	         
		}
		return propMap;
	}

	private static boolean valid(String str) {
		Map<Character,Integer> occurenceMap = occurenceMap(str);  
		char[] chrs = str.toCharArray();
		boolean isValid = true;
		if(isMultipleOperator(occurenceMap)) {
			if(chrs[0] != '+' && chrs[0] != '-' && chrs[0] != '/' && chrs[0] != '*' && 
					chrs[chrs.length-1] != '+' && chrs[chrs.length-1] != '-' && chrs[chrs.length-1] != '/' && chrs[chrs.length-1] != '*') {
				for(int i = 0; i < chrs.length; i++) {
					if(!Character.isWhitespace(chrs[i]) && !Character.isDigit(chrs[i]) && chrs[i] != '+' &&
							chrs[i] != '-' && chrs[i] != '/' && chrs[i] != '*') {
						isValid = false;
						break;
					}
				}
			}else {
				isValid = false;
			}
		}
		return isValid;

	}

	private static List<String> formOutput(List<String> inputLines){
		List<String> outputLines = new ArrayList<String>();
		for(String line : inputLines) {
			if(line.equals("=")) {
				break;
			}
			int operatorIndex;
			int leftInt;
			int rightInt;
			Number result;
			if(valid(line)) {
				operatorIndex = line.indexOf('+');
				if(operatorIndex != -1) {
					leftInt = Integer.parseInt(line.substring(0,operatorIndex));
					rightInt = Integer.parseInt(line.substring(operatorIndex+1,line.length()));
					result = leftInt + rightInt;
					outputLines.add(line + "=" + result);
				}
				operatorIndex = line.indexOf('-');
				if(operatorIndex != -1) {
					leftInt = Integer.parseInt(line.substring(0,operatorIndex));
					rightInt = Integer.parseInt(line.substring(operatorIndex+1,line.length()));
					result = leftInt - rightInt;
					outputLines.add(line + "=" + result);
				}

				operatorIndex = line.indexOf('/');
				if(operatorIndex != -1) {
					leftInt = Integer.parseInt(line.substring(0,operatorIndex));
					rightInt = Integer.parseInt(line.substring(operatorIndex+1,line.length()));
					float val = (float)leftInt / rightInt;
					outputLines.add(line + "=" + val);
				}

				operatorIndex = line.indexOf('*');
				if(operatorIndex != -1) {
					leftInt = Integer.parseInt(line.substring(0,operatorIndex));
					rightInt = Integer.parseInt(line.substring(operatorIndex+1,line.length()));
					result = leftInt * rightInt;
					outputLines.add(line + "=" + result);
				}
			}else {
				outputLines.add("Error");
			}
		}
		return outputLines;
	}
	private static boolean isMultipleOperator(Map<Character,Integer> occurMap) {
		boolean isValidOccurence = true;
		if(occurMap.getOrDefault('+',0) > 1 || occurMap.getOrDefault('-',0) > 1 || occurMap.getOrDefault('/',0) > 1 || occurMap.getOrDefault('*',0) > 1 ) {
			return false;
		}
		return isValidOccurence;

	}
	private static HashMap<Character,Integer> occurenceMap(String str){
		HashMap <Character, Integer> charCount = new HashMap<>();  
		for (int i = str.length() - 1; i >= 0; i--)   {  
			if(charCount.containsKey(str.charAt(i))){  
				int count = charCount.get(str.charAt(i));  
				charCount.put(str.charAt(i), ++count);  
			}   
			else   
			{  
				charCount.put(str.charAt(i),1);  
			}  
		}
		return charCount;
	}


	private static List<String> readFile(String filePath){
		List<String> lines = new ArrayList<String>();
		File file = new File(filePath);
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader(file));
			String st;
			while ((st = br.readLine()) != null) {
				lines.add(st);
			}
		} catch (IOException e) {
			throw new BusinessException(e.getMessage());
		}
		return lines;
	}


	private static void writeToOutput(List<String> list,String fileName){			   
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			for(String str: list) {
				writer.write(str);
				writer.newLine();
			}

			writer.close();
		} catch (IOException e) {
			throw new BusinessException(e.getMessage());
		}

	}


}
