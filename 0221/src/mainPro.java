import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;


public class mainPro {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String path= "c:/url.txt";
		String outpath="c:/title.txt";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
		OutputStreamWriter log = new OutputStreamWriter(new FileOutputStream(new File(outpath)));
		String line = reader.readLine();
		while(line.length() != 0)
		{
			System.out.println("LINE:"+line);
			String title="";
			try{
				title = MyStrings.getTitle(line);
			}
			catch(Exception e)
			{
				System.out.println("error!");
			}
			log.write(title+"\r\n");
			log.flush();
			line = reader.readLine();
		}

	}

}
