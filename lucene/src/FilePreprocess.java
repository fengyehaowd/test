import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


public class FilePreprocess {

	/**
	 * @param args
	 */
	private static String replace(String line)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("£¬", ",");
		map.put("¡£", ".");
		map.put("¡¶", "<");
		map.put("¡·", ">");
		map.put("||", "|");
		map.put("¡¾", "[");
		map.put("¡¿", "]");
		map.put("£¿", "?");
		map.put("¡°", "\"");
		map.put("¡±", "\"");
		map.put("¡ª¡ª", "-");
		map.put("~", "~");
		map.put("£¡", "!");
		map.put("¡®", "'");
		
		int length = line.length();
		for(int i=0;i<length;i++)
		{
			String charat = line.substring(i,i+1);
			if(map.get(charat)!=null)
				line = line.replace(charat, (String)map.get(charat));
		}
		return line;
	}
	
	public static void preprocess(File file,String outputdir)
	{
		try {
			splitToSmallFiles(charProcess(file, outputdir+"all.txt"), outputdir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static File charProcess(File file,String desFile) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		File des = new File(desFile);
		BufferedWriter writer = new BufferedWriter(new FileWriter(des));
		
		String line = reader.readLine();
		while(line != null)
		{
			if(line!="\r\n")
			{
				String newline = replace(line);
				writer.write(newline+"\r\n");
				writer.flush();
			}
			line= reader.readLine();
		}
		reader.close();
		writer.close();
		
		return new File(desFile);
		
	}
	
	public static void splitToSmallFiles(File file,String outputdir) throws IOException
	{
		int filePointer = 0;
		int MAX_SIZE = 10240;
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		File folder = new File(outputdir);
		folder.mkdirs();
		String line = reader.readLine();
		StringBuffer buffer = new StringBuffer(); 
		int count = 0;
		while(line != null)
		{
			buffer.append(line+"\r\n");
			if(buffer.toString().getBytes().length > MAX_SIZE)
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputdir+"/output"+(count++)+".txt")));
				writer.write(buffer.toString());
				buffer.delete(0, buffer.length());
				writer.flush();
				writer.close();
			}
			line = reader.readLine();
			
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputdir+"/output"+count+".txt")));
		writer.write(buffer.toString());
		writer.flush();
		writer.close();
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String txt = "d:/Î÷ÐÐÂþ¼Ç.txt";
		String output = "d:/txt";
		preprocess(new File(txt),output);

	}

}
