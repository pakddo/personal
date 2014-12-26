package net.pakddo.UrlFeatureExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.pakddo.utils.FilePathCollector;
import net.pakddo.utils.ResultFileWriter;


public class SmiParser {

	private static String fileName;

	public static void SmiParserTest(){

		FilePathCollector fpc = new FilePathCollector(Constants.inputSmiFilepath, "smi");
		//middleFile usage - FilePathCollector fpc = new FilePathCollector(middleFilepath, "txt");
		ArrayList<String> fileList = fpc.getFileList();
		ResultFileWriter rfw = new ResultFileWriter(Constants.resultFilePath, "_");

		ArrayList<TimedContent> parsedDataArray;
		StringBuilder tempStringBuilder = new StringBuilder();
		StringBuilder out = new StringBuilder();
		int resultLength;

		for(String filePath:fileList){
			fileName = filePath.substring(filePath.lastIndexOf('\\') + 1, filePath.lastIndexOf('.'));
			try{
				
				//parsedDataArray = CorpusMaker.contentNormalize(smiParsing(filePath), 0);
				//middleFile usage - parsedDataArray = CorpusMaker.contentNormalize(parseMiddleFile(filePath), 0);
				
				parsedDataArray = smiParsing(filePath);
				

				tempStringBuilder.append(CorpusMaker.keyMatching(parsedDataArray, 1));
				rfw.writeFile(tempStringBuilder);


				resultLength = tempStringBuilder.toString().split("\r\n").length;

				if(resultLength > 1){
					out.append(fileName + "\t" + resultLength + "\r\n");
				}

				tempStringBuilder.delete(0, tempStringBuilder.length());
				resultLength = 0;
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		rfw = new ResultFileWriter(Constants.resultFilePath, "_Summary");
		rfw.writeFile(out);
	}

	

	

	/**
	 * make SMI file to parsedArray (ArrayList<String[]>)
	 * 
	 * @param filePath
	 * @return ArrayList<TimedContent>
	 * 
	 */

	static ArrayList<TimedContent> smiParsing(String filePath){

		Pattern syncTagPattern  =  Pattern.compile("<SYNC[ ]*(start)=([0-9]*)[^>]*>", Pattern.CASE_INSENSITIVE);
		Pattern pTagPattern  =  Pattern.compile("<p[^>]*(Class=[\"']?([^>\"']+)[\"']?)[^>]*>", Pattern.CASE_INSENSITIVE);
		Pattern nonValidTagPattern = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
		
		fileName = filePath.substring(filePath.lastIndexOf('\\') + 1, filePath.lastIndexOf('.'));
		
		// file read
		System.out.println(filePath);

		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));		
			File file = new File(filePath);
			String encoding = Util.findFileEncoding(file);
			
			if(encoding != null && encoding.equals("UTF-8")){
				FileInputStream fis = new FileInputStream(new File(filePath)); 
				InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
				br = new BufferedReader(isr);
			}
			
			char[] c = new char[(int)file.length()];                      
			br.read(c);

			//replace all line feed to null
			char[] lineSeperator = "\r\n".toCharArray();				

			for (int i = 0; i < c.length; i++) {	
				if(c[i] == lineSeperator[0] || c[i] == lineSeperator[1]){
					c[i] = 32;
				}				
			}

			String allFile = new String(c);
			allFile = allFile.replace("<SYNC", "\r\n<SYNC");

			br = new BufferedReader(new StringReader(allFile));

			//pre-processing for make the data (time, code, content); 
			String lineString;		
			ArrayList<String[]> preProcessArray = new ArrayList<String[]>();

			String resultString[];
			String code;
			String time;
			String content;
			
			
			Matcher matcher;

			//for the file writing
			StringBuilder stringBuilder = new StringBuilder();

			// make data
			while((lineString = br.readLine()) != null ){
				
				//for blank and line break
				resultString = new String[3];
				
				code = null;
				time = null;
				content = null;

				if(lineString.startsWith("<SYNC")){

					if(lineString.contains("&nbsp;")){
						lineString = lineString.replace("&nbsp;", " ");
					}

					if(lineString.contains("<br>")){
						lineString = (lineString).replace("<br>", " ");
					}

					// extract data
					matcher = syncTagPattern.matcher(lineString);
					while (matcher.find()) {
						time = matcher.group(2);						
					}
					if(time != null){
						resultString[0] = time.trim();
					}

					matcher = pTagPattern.matcher(lineString);
					while (matcher.find()) {
						code = (matcher.group(2));
					}
					if(code != null){
						resultString[1] = code;
					}

					matcher = nonValidTagPattern.matcher(lineString);
					lineString = matcher.replaceAll("");
					lineString = lineString.replaceAll("\"", "");
					lineString = lineString.replaceAll("<", "");
					lineString = lineString.replaceAll(">", "");
					lineString = lineString.replaceAll("\\s+", " ");
					lineString = lineString.trim();					

					//catch the data
					if(!lineString.startsWith("<")){
						content = lineString;
						resultString[2] = content;
					}

					preProcessArray.add(resultString);
					
				}
			}
			br.close();

			
			String[] targetString;
			String[] prevString = new String[3];
			String[] nextString = new String[3];	
			
			ArrayList<TimedContent> resultArray1 = new ArrayList<TimedContent>();
			TimedContent timedContent;

			for(int i = 0; i < preProcessArray.size(); i++){
				timedContent = new TimedContent();
				
				targetString = preProcessArray.get(i);

				if(i > 1) prevString = preProcessArray.get(i-1);
				
				if(i + 1 < preProcessArray.size()) nextString = preProcessArray.get(i+1);
				
				if(targetString[1].equals("COMM")) continue;

				if(!targetString[0].equals("") && !targetString[2].equals("")){

					timedContent.setTime(Integer.valueOf(targetString[0]));
					timedContent.setCode(targetString[1]);
					timedContent.setContent(targetString[2]);
				
					if(nextString[0] != null && !nextString[0].equals("")) {
						timedContent.setDuration(Integer.valueOf(nextString[0]) - Integer.valueOf(targetString[0]));
					} else {
						timedContent.setDuration(0);
					}
					
					resultArray1.add(timedContent);
					
					stringBuilder.append(timedContent.toString() + "\r\n");		

				}

			} 

			if(stringBuilder.length() > 0){
				// makeMiddleFile
				ResultFileWriter rfw = new ResultFileWriter(Constants.middleFilepath, "Middle_" + fileName);
				rfw.writeFile(stringBuilder);
				stringBuilder.delete(0, stringBuilder.length());
			}
			
			//return resultArray;
			return resultArray1;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}



}
