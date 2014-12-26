package net.pakddo.UrlFeatureExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.pakddo.utils.ResultFileWriter;

public class SrtParser {


	private static String fileName;
	

	/**
	 * make SRT file to parsedSrtData (ArrayList<String[]>)
	 * 
	 * @param filePath
	 * @return ArrayList<String[]>
	 * 
	 */

	static ArrayList<TimedContent> srtParsing(String filePath){

		Pattern nonValidTagPattern = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");

		StringBuilder stringBuilder = new StringBuilder();

		fileName = filePath.substring(filePath.lastIndexOf('\\') + 1, filePath.lastIndexOf('.'));

		// file read
		System.out.println(filePath);
		try {

			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String encoding = Util.findFileEncoding(new File(filePath));

			if(encoding != null && encoding.equals("UTF-8")){
				FileInputStream fis = new FileInputStream(new File(filePath)); 
				InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
				br = new BufferedReader(isr);
			}

			TimedContent timedContent;

			Matcher matcher;

			ArrayList<TimedContent> parsedSrtData = new ArrayList<TimedContent>();

			// make data
			while (br.readLine() != null) {
				timedContent = new TimedContent();

				String timeString = br.readLine();
				String lineString = "";
				String s;
				while (!((s = br.readLine()) == null || s.trim().equals(""))) {
					lineString += s + " ";
				}

				long startTime = parse(timeString.split("-->")[0]);
				long endTime = parse(timeString.split("-->")[1]);

				matcher = nonValidTagPattern.matcher(lineString);
				lineString = matcher.replaceAll("");
				lineString = lineString.replaceAll("\"", "");
				lineString = lineString.replaceAll("<", "");
				lineString = lineString.replaceAll(">", "");
				lineString = lineString.replaceAll("\\s+", " ");
				lineString = lineString.trim();			

				timedContent.setTime(startTime);
				timedContent.setDuration(endTime - startTime);
				timedContent.setContent(lineString);
				timedContent.setCode("SRT");

				stringBuilder.append(timedContent.toString() + "\r\n");
				parsedSrtData.add(timedContent);
				
			}
			br.close();

			if(stringBuilder.length() > 0){
				// makeMiddleFile
				ResultFileWriter rfw = new ResultFileWriter(Constants.middleFilepath, "Middle_" + fileName);
				rfw.writeFile(stringBuilder);
				stringBuilder.delete(0, stringBuilder.length());
			}

			return parsedSrtData;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	private static long parse(String in) {
		long hours = Long.parseLong(in.split(":")[0].trim());
		long minutes = Long.parseLong(in.split(":")[1].trim());
		long seconds = Long.parseLong(in.split(":")[2].split(",")[0].trim());
		long millies = Long.parseLong(in.split(":")[2].split(",")[1].trim());

		return hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000 + millies;

	}

}
