package parserOnJavaV1;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ImageSaver {
	public void saveImage(String imageUrl,String imageName, String tableName) {
		URL url;
		URLConnection connection = null;
		
		
		try {
			url = new URL(imageUrl);
			connection = url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(10000);	
			
			File dir = new File("C://Eclipse/parserOnJavaV1/imgs/"+ tableName);
			dir.mkdir();
			BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
			FileOutputStream fos = new FileOutputStream(new File("C://Eclipse/parserOnJavaV1/imgs/"+tableName+"/"+imageName+".jpg"));
			int ch;
			while((ch = bis.read()) != -1) {
				fos.write(ch);
			}
			bis.close();
			fos.flush();
			fos.close();
			
		} catch (Exception e) {

			e.printStackTrace();
		}finally{
			//System.out.println(imageName+ " saved");
		}
		
		
		
				
	}
}
