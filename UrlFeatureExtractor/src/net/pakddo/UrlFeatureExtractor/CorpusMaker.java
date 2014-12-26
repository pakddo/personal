package net.pakddo.UrlFeatureExtractor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import net.pakddo.utils.ResultFileWriter;

public class CorpusMaker {

	static String fileName;

	final static int timeThreshold = 1000;
	final static int durationThreshold = 2000;
	final static int durationThresholdMin = 750;
	final static int durationThresholdMax = 1250;

	public static void main(String[] args){

		SmiParser.SmiParserTest();
		SmiSrtTest();

	}
	
	
	public static void SmiSrtTest(){
		
		/*
		 * SMI + SRT test
		 */
		
		StringBuilder out = new StringBuilder();
		StringBuilder tempStringBuilder = new StringBuilder();
		int resultLength;
		ResultFileWriter rfw = new ResultFileWriter(Constants.resultFilePath, "_");

		try{

			//SMI + SRT
			tempStringBuilder.append(matchingSmiSrt(Constants.fileName1, Constants.fileName2));
			rfw.writeFile(tempStringBuilder);
			resultLength = tempStringBuilder.toString().split("\r\n").length;
			if(resultLength > 1){
				out.append(Constants.fileName1 + "\t" + resultLength + "\r\n");
			}
			tempStringBuilder.delete(0, tempStringBuilder.length());
			resultLength = 0;

			//SRT + SRT
			tempStringBuilder.append(matchingSrt(Constants.fileName3, Constants.fileName4));
			rfw.writeFile(tempStringBuilder);
			resultLength = tempStringBuilder.toString().split("\r\n").length;
			if(resultLength > 1){
				out.append(Constants.fileName3 + "\t" + resultLength + "\r\n");
			}
			tempStringBuilder.delete(0, tempStringBuilder.length());
			resultLength = 0;

			//summary
			rfw = new ResultFileWriter(Constants.resultFilePath, "_Summary");
			rfw.writeFile(out);

		} catch (Exception e){
			e.printStackTrace();
		}
		
		
	}
	

	/**
	 * concatenate smi and srt file and matching each other
	 * 
	 * @param smiFileName
	 * @param srtFileName
	 * @returnStringBuilder matchingResult
	 */

	private static StringBuilder matchingSmiSrt(String smiFileName, String srtFileName){

		ArrayList<TimedContent> parsedSmiDataArray;
		ArrayList<TimedContent> parsedSrtDataArray;
		ArrayList<TimedContent> totalDataArray = null;	

		StringBuilder tempStringBuilder = new StringBuilder();

		parsedSmiDataArray = SmiParser.smiParsing(smiFileName);
		parsedSrtDataArray = SrtParser.srtParsing(srtFileName);

		// smiFileName + srtFileName
		totalDataArray = Util.concatArray(parsedSmiDataArray, parsedSrtDataArray);

		System.out.println("length : " + totalDataArray.size());
		totalDataArray = CorpusMaker.contentNormalize(totalDataArray, 0);
		tempStringBuilder.append(CorpusMaker.keyMatching(totalDataArray, 1));	

		return tempStringBuilder;
	}

	/**
	 * concatenate srt file and matching each other
	 * 
	 * @param srtFileName1
	 * @param srtFileName2
	 * @return StringBuilder matchingResult
	 */

	private static StringBuilder matchingSrt(String srtFileName1, String srtFileName2){

		ArrayList<TimedContent> parsedSrtDataArray1;
		ArrayList<TimedContent> parsedSrtDataArray2;
		ArrayList<TimedContent> totalDataArray = null;	

		StringBuilder tempStringBuilder = new StringBuilder();

		parsedSrtDataArray1 = SrtParser.srtParsing(srtFileName1);
		parsedSrtDataArray2 = SrtParser.srtParsing(srtFileName2);

		// smiFileName + srtFileName
		totalDataArray = Util.concatArray(parsedSrtDataArray1, parsedSrtDataArray2);

		System.out.println("length : " + totalDataArray.size());
		totalDataArray = CorpusMaker.contentNormalize(totalDataArray, 0);
		tempStringBuilder.append(CorpusMaker.keyMatching(totalDataArray, 1));	

		return tempStringBuilder;
	}

	/**
	 * concatenate smi file and matching each other
	 * 
	 * @param smiFileName1
	 * @param smiFileName2
	 * @return StringBuilder matchingResult
	 */
	private static StringBuilder matchingSmi(String smiFileName1, String smiFileName2){

		ArrayList<TimedContent> parsedSmiDataArray1;
		ArrayList<TimedContent> parsedSmiDataArray2;
		ArrayList<TimedContent> totalDataArray = null;	

		StringBuilder tempStringBuilder = new StringBuilder();

		parsedSmiDataArray1 = SrtParser.srtParsing(smiFileName1);
		parsedSmiDataArray2 = SrtParser.srtParsing(smiFileName2);

		// smiFileName + srtFileName
		totalDataArray = Util.concatArray(parsedSmiDataArray1, parsedSmiDataArray2);

		System.out.println("length : " + totalDataArray.size());
		totalDataArray = CorpusMaker.contentNormalize(totalDataArray, 0);
		tempStringBuilder.append(CorpusMaker.keyMatching(totalDataArray, 1));	

		return tempStringBuilder;
	}

	/**
	 * Normalizing contents for better matching results.
	 * 
	 * If duration is in the range of setting value, concatenate content with previous or next content.
	 * 
	 * option 0 = next
	 * option 1 = previous
	 * 
	 * @param _parsedDataArray
	 * @param option
	 * @return
	 */
	static ArrayList<TimedContent> contentNormalize(ArrayList<TimedContent> _parsedDataArray, int option){

		ArrayList<TimedContent> resultArray = new ArrayList<TimedContent>();
		TimedContent prevString = null;
		TimedContent nextString = null;
		TimedContent processString = null;
		TimedContent tempString = null;
		long duration;

		//for the file writing
		StringBuilder stringBuilder = new StringBuilder();
		String PNvalue;

		for(int i = 0; i < _parsedDataArray.size(); i++){
			processString = _parsedDataArray.get(i);
			duration = 0;
			PNvalue = "";

			if(i + 1 < _parsedDataArray.size()){
				nextString = _parsedDataArray.get(i + 1);
			} 

			if(i > 1){
				prevString = _parsedDataArray.get(i - 1);
			}
			tempString = new TimedContent();

			//matching criteria
			duration = processString.getDuration();
			if(duration < durationThresholdMax && duration > durationThresholdMin){
				if(processString.getCode().equals(nextString.getCode())){

					tempString.setCode(processString.getCode());
					tempString.setTime(processString.getTime());
					switch (option){
					case 0:
						tempString.setContent((processString.getContent()+ " " + nextString.getContent()).replace("... ...", " "));
						tempString.setDuration(processString.getDuration() + nextString.getDuration());
						break;
					case 1:
						tempString.setContent((prevString.getContent()+ " " + processString.getContent()).replace("... ...", " "));
						tempString.setDuration(prevString.getDuration() + processString.getDuration());
						break;
					default :
					}

					if(processString.getPNvalue() != null){
						tempString.setPNvalue(processString.getPNvalue() + "_M");
						PNvalue = tempString.getPNvalue(); 
					}
					i++;
				} else {
					tempString = processString;
				}
			} else {
				tempString = processString;
			}

			resultArray.add(tempString);
			stringBuilder.append(tempString.toString() + "\r\n");

		}
		if(stringBuilder.length() > 0){
			ResultFileWriter rfw = new ResultFileWriter(Constants.normalizeFilepath, "Normalize_" + fileName);
			rfw.writeFile(stringBuilder);
			stringBuilder.delete(0, stringBuilder.length());
		}
		return resultArray;	
	}

	/**
	 * Parsing middle file for quality measurement.
	 * If the file format is not correct, this method will not working.
	 * 
	 * File format (txt) ======================
	 * sync code content duration PNvalue
	 * 
	 * @param filePath
	 * @return ArrayList<TimedContent> parsedArray
	 */

	private static ArrayList<TimedContent> parseMiddleFile(String filePath){
		// file read
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String lineString;
			String[] processString;
			ArrayList<TimedContent> resultArray = new ArrayList<TimedContent>();
			br.mark(1000);
			String tempString = br.readLine();
			TimedContent timedContent;
			if(tempString != null && tempString.split("\t").length == 5){
				System.out.println(filePath);
				br.reset();
				while((lineString = br.readLine()) != null ){
					timedContent = new TimedContent();
					lineString = lineString.replaceAll("\"", "");
					processString = lineString.split("\t");
					
					timedContent.setTime(Long.valueOf(processString[0]));
					timedContent.setCode(processString[1]);
					timedContent.setContent(processString[2]);
					timedContent.setDuration(Long.valueOf(processString[3]));
					timedContent.setPNvalue(processString[4]);
					
					resultArray.add(timedContent);

				}
			}
			br.close();
			return resultArray;
		} catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * make dual content from _parsedDataArray
	 * 
	 * @param totalDataArray
	 * @param option
	 * @return
	 */
	static StringBuilder keyMatching(ArrayList<TimedContent> totalDataArray, int option){

		StringBuilder out = new StringBuilder();
		long key1, key2, durationKey1;
		long similarityByTime, similarityByDuration;
		double euclideanDistance;

		String matchedContent;
		long matchedContentTime, matchedSimilarity;
		String PNvalue;

		String tempString;
		TimedContent processString1, processString2;		
		TimedContent nextString1 = null;

		//TreeMap for duplication control
		TreeMap<Integer, String> resultDataSet = new TreeMap<Integer, String>();	
		TreeMap<Integer, String> matchingSet;

		//forward
		for(int i = 0 ; i < totalDataArray.size() ; i++){
			//System.out.println("1-i:" + i);

			processString1 = totalDataArray.get(i);			
			if(i + 1 < totalDataArray.size()) nextString1 = totalDataArray.get(i + 1);


			tempString = null;
			PNvalue = "";
			matchedContent = null;
			matchedContentTime = 0;
			matchedSimilarity = 0;
			similarityByTime = 0;
			
			similarityByDuration = 0;

			key1 = processString1.getTime();
			matchingSet = new TreeMap<Integer, String>();

			if(Util.isHangul(processString1.getContent())){
				for(int j = 0 ; j < totalDataArray.size() ; j++){

					processString2 = totalDataArray.get(j);

					//similarity (Absolute Distance)
					key2 = processString2.getTime();
					similarityByTime = Math.abs(key2 - key1);
					similarityByDuration = Math.abs(processString1.getDuration() - processString2.getDuration());	
					euclideanDistance = Math.sqrt(Math.pow(similarityByTime,2) + Math.pow(similarityByDuration, 2));

					if(Util.isAlphabet(processString2.getContent()) || Util.isJapanese(processString2.getContent())){
						if(processString1.getCode() != processString2.getCode() && !processString1.getCode().equals("") && !processString2.getCode().equals("")){

							switch (option){
							case 0 :
								
								if(similarityByTime < timeThreshold){ //similarity
									matchedSimilarity = similarityByTime;
									matchedContent =  processString2.getContent();
									matchedContentTime = processString2.getTime();
									matchingSet.put((int) matchedSimilarity, matchedContentTime + "\t" + matchedContent + "\t" + matchedSimilarity + "\t" + key1);

									if(processString1.getPNvalue() != null){
										PNvalue = "\t" + processString1.getPNvalue() + "|" + processString2.getPNvalue();
									}
								}
								break;
							case 1 :
								if(euclideanDistance < timeThreshold){ //similarity
									if(similarityByDuration > durationThreshold) {
										if(nextString1 != null){
											durationKey1 = processString1.getDuration() + nextString1.getDuration();

											if(Math.abs((durationKey1 - processString2.getDuration())) < (durationThresholdMax - durationThresholdMin)) {

												processString1.setContent((processString1.getContent() + " " + nextString1.getContent()).replace("... ...", " "));
												processString1.setDuration(durationKey1);
												totalDataArray.set(i, processString1);
												if(i + 1 < totalDataArray.size()){
													totalDataArray.set(i + 1, processString1);
												}

												if(processString1.getPNvalue() != null){
													processString1.setPNvalue(processString1.getPNvalue() + "_M2");
												}
											}
										}
									} 
									matchedSimilarity = (long) euclideanDistance;
									matchedContent =  processString2.getContent();
									matchedContentTime = processString2.getTime();
									matchingSet.put((int) matchedSimilarity, matchedContentTime + "\t" + matchedContent + "\t" + matchedSimilarity + "\t" + key1);

									if(processString1.getPNvalue() != null){
										PNvalue = "\t" + processString1.getPNvalue() + "|" + processString2.getPNvalue();
									}

								}
								break;
							default   :

								break;
							}

						} 
					} 

				}

				// 1 KRCC : N ENCC duplication
				if(matchingSet.size() > 1){
					String[] tempKeyString;

					Iterator<Integer> iterator= matchingSet.keySet().iterator();

					while (iterator.hasNext()) {
						int key = (Integer) iterator.next();
						tempKeyString = matchingSet.get(key).split("\t");

						if(!tempKeyString[0].equals(tempKeyString[3])){
							resultDataSet.remove(Integer.valueOf(tempKeyString[0]) + Integer.valueOf(tempKeyString[3]));
						}

						String temp = matchingSet.get(matchingSet.firstKey());
						String[] tempArray = temp.split("\t");
						matchedContentTime = Long.parseLong(tempArray[0]);
						matchedContent = tempArray[1];
						matchedSimilarity = Long.parseLong(tempArray[2]);
					}

				}	

				if(matchedContent != null){
					tempString = String.valueOf(processString1.getTime()) + "\t"; // KRCC time
					tempString += processString1.getContent() + "\t"; // KRCC
					tempString += String.valueOf(matchedContentTime) + "\t"; //ENCC time
					tempString += matchedContent + "\t"; //ENCC
					tempString += String.valueOf(matchedSimilarity); //similarity
					tempString += PNvalue; //PNValue for experiments
					tempString += "\r\n";

					resultDataSet.put((int) (processString1.getTime() + matchedContentTime),tempString);
				} else {
					//for the remain file writing
					out.append(processString1.toString() + "\r\n");
				}
			}

		}


		//reverse
		for(int i = 0 ; i < totalDataArray.size() ; i++){
			//System.out.println("2-i:" + i);
			processString1 = totalDataArray.get(i);			
			if(i + 1 < totalDataArray.size()) nextString1 = totalDataArray.get(i + 1);


			tempString = null;
			PNvalue = "";
			matchedContent = null;
			matchedContentTime = 0;
			matchedSimilarity = 0;
			similarityByTime = 0;
			similarityByDuration = 0;

			key1 = processString1.getTime();
			matchingSet = new TreeMap<Integer, String>();


			if(Util.isAlphabet(processString1.getContent()) || Util.isJapanese(processString1.getContent())){

				for(int j = 0 ; j < totalDataArray.size() ; j++){

					processString2 = totalDataArray.get(j);

					key2 = processString2.getTime();
					similarityByTime = Math.abs(key2 - key1);
					similarityByDuration = Math.abs(processString1.getDuration() - processString2.getDuration());

					if(Util.isHangul(processString2.getContent())){
						if(processString1.getCode() != processString2.getCode() && !processString1.getCode().equals("") && !processString2.getCode().equals("")){

							switch (option){
							case 1 :
								//key / 1000 
								if(key1/1000 == key2/1000){
									matchedContent =  processString2.getContent();
									matchedContentTime = processString2.getTime();
									matchedSimilarity = similarityByTime;
								}
								break;
							case 2 :
								if(similarityByTime < timeThreshold){ //similarity
									matchedSimilarity = similarityByTime;
									matchedContent =  processString2.getContent();
									matchedContentTime = processString2.getTime();
									matchingSet.put((int) matchedSimilarity, matchedContentTime + "\t" + matchedContent + "\t" + matchedSimilarity + "\t" + key1);

									if(processString1.getPNvalue() != null){
										PNvalue = "\t" + processString1.getPNvalue() + "|" + processString2.getPNvalue();
									}
								}
								break;
							case 3 :
								if(similarityByTime < timeThreshold){ //similarity
									if(similarityByDuration > durationThreshold) {
										if(nextString1 != null){
											durationKey1 = processString1.getDuration() + nextString1.getDuration();

											if(Math.abs((durationKey1 - processString2.getDuration())) < (durationThresholdMax - durationThresholdMin)) {

												processString1.setContent((processString1.getContent() + " " + nextString1.getContent()).replace("... ...", " "));
												processString1.setDuration(durationKey1);
												totalDataArray.set(i, processString1);
												if(i + 1 < totalDataArray.size()){
													totalDataArray.set(i + 1, processString1);
												}

												if(processString1.getPNvalue() != null){
													processString1.setPNvalue(processString1.getPNvalue() + "_M2");
												}
											}
										}
									} 
									matchedSimilarity = similarityByTime;
									matchedContent =  processString2.getContent();
									matchedContentTime = processString2.getTime();
									matchingSet.put((int) matchedSimilarity, matchedContentTime + "\t" + matchedContent + "\t" + matchedSimilarity + "\t" + key1);

									if(processString1.getPNvalue() != null){
										PNvalue = "\t" + processString1.getPNvalue() + "|" + processString2.getPNvalue();
									}

								}
								break;
							default   :
								//same key
								if(key1 == key2){
									matchedSimilarity = similarityByTime;
									matchedContent =  processString2.getContent();
									matchedContentTime = processString2.getTime();

								}
								break;
							}

						}
					}
				}

				//1 ENCC : N KRCC  duplication
				if(matchingSet.size() > 1){
					String[] tempKeyString;

					Iterator<Integer> iterator= matchingSet.keySet().iterator();

					while (iterator.hasNext()) {
						int key = (Integer) iterator.next();
						tempKeyString = matchingSet.get(key).split("\t");

						if(!tempKeyString[0].equals(tempKeyString[3])){

							resultDataSet.remove(Integer.valueOf(tempKeyString[0]) + Integer.valueOf(tempKeyString[3]));
						}

						String temp = matchingSet.get(matchingSet.firstKey());
						String[] tempArray = temp.split("\t");
						matchedContentTime = Long.parseLong(tempArray[0]);
						matchedContent = tempArray[1];
						matchedSimilarity = Long.parseLong(tempArray[2]);
					}

				}				

				if(matchedContent != null){
					tempString = String.valueOf(matchedContentTime) + "\t"; // KRCC time
					tempString += matchedContent + "\t"; // KRCC
					tempString += String.valueOf(processString1.getTime()) + "\t"; //ENCC time
					tempString += processString1.getContent() + "\t"; //ENCC
					tempString += String.valueOf(matchedSimilarity); //similarity
					tempString += PNvalue; //PNValue for experiments
					tempString += "\r\n";

					resultDataSet.put((int) (processString1.getTime() + matchedContentTime),tempString);
				} else {
					//for the remain file writing
					out.append(processString1.toString() + "\r\n");
				}

			} 


		}


		StringBuilder stringBuilder = new StringBuilder();
		Iterator<Integer> iterator = resultDataSet.keySet().iterator();
		while (iterator.hasNext()) {
			int key = (Integer) iterator.next();
			stringBuilder.append(resultDataSet.get(key));
		}


		ResultFileWriter rfw = new ResultFileWriter(Constants.remainFilepath, "Remain_" + fileName);
		if(out.length() > 0){
			rfw.writeFile(out);
			out.delete(0, out.length());
		}		
		return stringBuilder;

	}
}
