package net.pakddo.UrlFeatureExtractor;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.pakddo.utils.FilePathCollector;


public class UrlFeatureExtractor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FilePathCollector filePathCollector = new FilePathCollector(Constants.inputUrlFilePath, "txt");
		for(String temp : filePathCollector.getFileList()){
			System.out.println(temp);

			checkHttps(temp);
			checkPunctuation(temp);
			checkIp(temp);
			checkLength(temp);

		}
	}

	public static void checkHttps(String filePath){
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));

			String lineString = "";
			int count = 0;
			int httpCount = 0;
			int httpsCount = 0;

			while((lineString = br.readLine()) != null ){
				//System.out.println(lineString);
				if(lineString.contains("http://") == true){
					httpCount++;
				} else if (lineString.contains("https://") == true){
					httpsCount++;
				} else {
					System.out.println(lineString);
				}
				count++;
			}
			br.close();
			System.out.println("count :\t\t\t" + count);
			System.out.println("httpCount :\t\t" + httpCount);
			System.out.println("httpsCount :\t\t" + httpsCount);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public static void checkPunctuation(String filePath){
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));

			String lineString = "";
			int count = 0;
			int puncCount = 0;

			while((lineString = br.readLine()) != null ){
				//System.out.println(lineString);
				if(lineString.contains("@") == true 
						|| lineString.contains("?") == true
						|| lineString.contains("&") == true){
					puncCount++;
					//System.out.println(lineString);	
				} else {
					//System.out.println(lineString);
				}
				count++;
			}
			br.close();
			//System.out.println("count :" + count);
			System.out.println("puncCount (@,?, &):\t" + puncCount);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public static void checkLength(String filePath){
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));

			String lineString = "";
			
			int lengthCount = 0;

			while((lineString = br.readLine()) != null ){
				//System.out.println(lineString);
				if(lineString.length() > 75){
					lengthCount++;
					//System.out.println(lineString);	
				} else {
					//System.out.println(lineString);
				}
				
			}
			br.close();
			
			System.out.println("lengthCount( > 75):\t" + lengthCount);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static void checkIp(String filePath){
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));

			String lineString = "";

			int ipCount = 0;


			String regex = "(((([0-9])|([1-9]\\d{1})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}(([0-9])|([1-9]\\d{1})|(1\\d{2})|(2[0-4]\\d)|(25[0-5])))";
			Pattern ipPattern = Pattern.compile(regex);

			Matcher matcher;



			while((lineString = br.readLine()) != null ){
				if(lineString.equals("url")) {
					lineString = br.readLine();
				}
				lineString = lineString.replace("\"", "");
				
				URL tempUrl = new URL(lineString);
				//					System.out.println("protocol : " + tempUrl.getProtocol());
				//					System.out.println("host : " + tempUrl.getHost());
				//					System.out.println("port : " + tempUrl.getPort());
				//					System.out.println("path : " + tempUrl.getPath());
				//					System.out.println("ref : " + tempUrl.getRef());
				
				
				matcher = ipPattern.matcher(tempUrl.getHost());
				if (matcher.matches() == true) {
					// System.out.println(lineString);
					ipCount++;					
				}
			}
			br.close();
			System.out.println("ipCount (hostname):\t" + ipCount);



		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e){
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
