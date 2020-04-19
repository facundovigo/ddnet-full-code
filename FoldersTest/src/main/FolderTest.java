package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class FolderTest {
	
	public static void main(String[] args) {
		
		File[] files = new File("/home/server/Apps/ddnet/report-templates").listFiles();
		
		//for(File f : files) System.out.println(f.getName());
		
		
		Collection<String> aux = new ArrayList<String>();
		Collection<String> str = showFiles(files, aux);
		
		Object archs[] = new String[str.size()];
		archs = str.toArray();
		
		for(int i = 0; i < archs.length; i++) {
			
			if(archs[i].toString().endsWith(".txt")) archs[i] = "c) " + archs[i];
			
			//	System.out.println(archs[i]);
			
			//if(str.toArray()[i].toString().endsWith(".txt"))
			//	str.toArray()[i]  = "a) " + str.toArray()[i].toString();
				
			//else str.toArray()[i] = "c) " + str.toArray()[i].toString();
		}
		for(int i = 0; i < archs.length; i++) System.out.println(archs[i]);
	}
	
	
	public static Collection<String> showFiles(File[] files, Collection<String> aux) {
	    
		for (File file : files) {
	       if (file.isDirectory()) {
	            aux.add(file.getName());
	        	//System.out.println(i + ") Directory: " + file.getName());
	            showFiles(file.listFiles(), aux); // Calls same method again.
	       } else aux.add(file.getName());
	    }
		
		return aux;
	}
}
