package lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import net.paoding.analysis.knife.SmartKnifeBox;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class indeProcessor {

	/**
	 * @param args
	 */
	private String indexdir = "d:\\index";
	private Directory INDEX_STORE_PATH; 
	public void createIndex(String input)
	{
		try{
			
		INDEX_STORE_PATH = FSDirectory.open(new File(indexdir));
		Version matchVersion = Version.LUCENE_CURRENT;
		IndexWriterConfig cfg = new IndexWriterConfig(matchVersion, new SmartChineseAnalyzer(matchVersion));
		IndexWriter writer = new IndexWriter(INDEX_STORE_PATH,cfg);
		File filesdir = new File(input);
		File[] files = filesdir.listFiles();
		
		for(int i=0;i < files.length; i++)
		{
			Document doc = new Document();
			Field field = new StringField("filename",files[i].getName(),Field.Store.YES);
			doc.add(field);
			field = new TextField("content",load2String(files[i]),Field.Store.YES);
			doc.add(field);
			writer.addDocument(doc);
			
		TokenStream ts = new SmartChineseAnalyzer(matchVersion).tokenStream("content", load2String(files[i]));
			ts.addAttribute(CharTermAttribute.class);
			 ts.reset(); 
	        while (ts.incrementToken()) { 
	        	CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);  
	            System.out.println(ta.toString());  
	        }  
		}
		
		writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public String load2String(File file)
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuffer buffer = new StringBuffer();
			String line = reader.readLine();
			while(line != null)
			{
				buffer.append(line);
				line = reader.readLine();
			}
			System.out.println("line:"+buffer.length()+" "+file.getName());
			return buffer.toString();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		indeProcessor i = new indeProcessor();
		i.createIndex("d:\\txt");
	}

}
