package net.pakddo.utils;

import java.io.File;
import java.util.ArrayList;


public class FilePathCollector {

	//filepath list
	private ArrayList<String> fileList = new ArrayList<String>();
	private String targetExt;

	/**
	 * find a absolute file path recursively
	 * 
	 * @param path
	 */
	public FilePathCollector(String path, String _ext) {	
		targetExt = _ext;
		walk(path);
	}

	public void walk( String path ) { 

		File root = new File( path ); 
		File[] list = root.listFiles(); 

		for ( File f : list ) { 
			if ( f.isDirectory() ) { 
				walk( f.getAbsolutePath() ); 
				//System.err.println( "Dir:" + f.getAbsoluteFile() ); 
			} 
			else { 

				int pos = f.getName().lastIndexOf( "." );
				String ext = f.getName().substring( pos + 1 );

				//only selected extension add to arraylist 
				if(ext.equalsIgnoreCase(targetExt)) 		
					fileList.add(f.getPath());
				//System.err.println( "File:" + f.getAbsoluteFile() ); 
			} 
		} 

	}

	public ArrayList<String> getFileList() {
		return fileList;
	}
	

}
