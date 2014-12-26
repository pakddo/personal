package net.pakddo.UrlFeatureExtractor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.mozilla.universalchardet.UniversalDetector;

public class Util {
	
	public static ArrayList<TimedContent> concatArray(ArrayList<TimedContent> parsedSmiDataArray, ArrayList<TimedContent> parsedSrtDataArray){
		ArrayList<TimedContent> totalDataArray = new ArrayList<TimedContent>();	
	
		// a + b
		for(TimedContent tempContent : parsedSmiDataArray){
			totalDataArray.add(tempContent);
		}

		for(TimedContent tempContent : parsedSrtDataArray){
			totalDataArray.add(tempContent);
		}			
		
		return totalDataArray;		
	}

	public static String findFileEncoding(File file) throws IOException {

		byte[] buf = new byte[4096];
		java.io.FileInputStream fis = new java.io.FileInputStream(file);

		UniversalDetector detector = new UniversalDetector(null);

		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}

		detector.dataEnd();

		String encoding = detector.getDetectedCharset();
		if (encoding != null) {
			System.out.println("Detected encoding = " + encoding);
		} else {
			System.out.println("No encoding detected.");
		}

		// (5)
		detector.reset();

		return encoding;
	}

	
	public static boolean isAlphabet(String data){
		boolean result = false;
		data = data.replaceAll("\\p{Digit}|\\p{Space}|\\p{Punct}", "");

		for(int i=0;i<data.length();i++){
			char c=data.charAt(i);
			//english + number
			if( ( 0x61 <= c && c <= 0x7A ) || ( 0x41 <= c && c <= 0x5A )){
				result = true;
			}
		}
		
		return result;
	}

	public static boolean isJapanese(String data){
		boolean result = false;
		data = data.replaceAll("\\p{Digit}|\\p{Space}|\\p{Punct}", "");

		if(data.matches("\\p{Alnum}")) System.out.println("Latin!!!");
		
		for(int i=0;i<data.length();i++){
			char c=data.charAt(i);
			if( ( 0x3041 <= c && c <= 0x312C)){
				//System.out.format("%c = 0x%02X (%3d)%n", c, (int) c, (int) c);
				result = true;
			}
			
		}
		
		return result;
	}	


	public static boolean isHangul(String data){
		boolean result = false;
		data = data.replaceAll("\\p{Digit}|\\p{Space}|\\p{Punct}", "");		
		//if(data.matches("\\p{Hangul}")) System.out.println("Hangul!!!");
		
		for(int i=0;i<data.length();i++){
			char c=data.charAt(i);
			//korea
			if( ( 0xAC00 <= c && c <= 0xD7A3 ) || ( 0x3131 <= c && c <= 0x318E )){
				result = true;
			}
		}

		return result;
	}
}
