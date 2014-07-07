package lucene;

import java.io.File;

import javax.swing.text.Document;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Search {
	
	   public void search(String type,String key) throws Exception {
	        String filePath="D:/index";
	        Directory dir=FSDirectory.open(new File(filePath));
	        IndexReader reader=DirectoryReader.open(dir);
	        IndexSearcher searcher=new IndexSearcher(reader);
	        Term term=new Term(type, key);
	        TermQuery query=new TermQuery(term);
	        TopDocs topdocs=searcher.search(query, 5);
	        ScoreDoc[] scoreDocs=topdocs.scoreDocs;
	        System.out.println("��ѯ�������---" + topdocs.totalHits+"��������--"+topdocs.getMaxScore());
	        for(int i=0; i < scoreDocs.length; i++) {
	            int doc = scoreDocs[i].doc;
	            org.apache.lucene.document.Document document = searcher.doc(doc);
	            System.out.println("content===="+document.get("content"));
	            System.out.println("id--" + scoreDocs[i].doc + "---scors--" + scoreDocs[i].score+"---index--"+scoreDocs[i].shardIndex);
	        }
	        reader.close();
	    }
	
	public static void main(String[] args) throws Exception
	{
		Search s = new Search();
		s.search("content", "ë��");
	}

}