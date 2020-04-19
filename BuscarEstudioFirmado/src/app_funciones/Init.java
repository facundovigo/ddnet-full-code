package app_funciones;

import java.io.File;
import java.net.URLDecoder;

import javax.swing.JOptionPane;

import junit.framework.Test;
import app_objetos.*;

public class Init {

	public static void main(String[] args) {
		
		//try{
		new app_Frame();
		/*
			File f = new File(app_funciones.Init.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			
		 String current = new java.io.File( "." ).getCanonicalPath();
		 String current1 = new java.io.File( "." ).getAbsolutePath();
		 String current2 = f.getAbsolutePath();
		 
		 current2 = current2.replace('\\', '/');
		 
		 current2 = current2.substring(0, current2.lastIndexOf('/'));
		 
		 current2 += "/config";
	     
		 JOptionPane.showMessageDialog(null, "Current dir:"+current
				 								+";\n" + current1
				 								+";\n" + current2);
		}catch(Exception e){JOptionPane.showMessageDialog(null, e.toString());}
		
		
		
		 	//System.out.println("Current dir:"+current);
	     //String currentDir = System.getProperty("user.dir");
	        //System.out.println("Current dir using System:" +currentDir);*/
	}

}
