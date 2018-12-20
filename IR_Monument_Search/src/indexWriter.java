import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class indexWriter {
	private static final String INDEX_DIR = "indexedFiles";
	private static String get_soundex(String str) throws IOException {
		Process p = Runtime.getRuntime().exec("python3 soundex/get_soundex.py "+str);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s=stdInput.readLine();
        p.destroy();
        stdInput.close();
		return s;
	}
	public static void main(String[] args) throws IOException, ScriptException {
		try {
			Directory dir = FSDirectory.open( Paths.get(INDEX_DIR) );
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE);
//            -----------------------
            indexDocs(dir,iwc);
            dir.close();
            analyzer.close();
//            ----------------------
		}
		catch (IOException e)
        {
            e.printStackTrace();
        }
	}
	static void indexDocs(Directory dir,IndexWriterConfig iwc) throws IOException{
		IndexWriter writer = new IndexWriter(dir, iwc);
		File folder = new File("inputFiles");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
            	Document doc=getDocument(file);
            	writer.addDocument(doc);
            }
        }
        writer.close();
	}
	static Document getDocument(File file) throws IOException {
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line,name="",description="",url="",year="",location="",developer="";
		int count=0;
		String soundex_name="",soundex_description="",soundex_year="",soundex_location="",soundex_developer="";
		while ((line = bufferedReader.readLine()) != null) {
			if(count==0) {
				name=line.trim();
				System.out.println(name);
				soundex_name=soundexSentence(name);
				System.out.println(soundex_name);
				count+=1;
			}
			else if(count==1) {
				url=line.trim();
				System.out.println(url);
				count+=1;
			}
			else if(count==2) {
				year=line.trim();
				System.out.println(year);
				soundex_year=soundexSentence(year);
				System.out.println(soundex_year);
				count+=1;
			}
			else if(count==3) {
				location=line.trim();
				System.out.println(location);
				soundex_location=soundexSentence(location);
				System.out.println(soundex_location);
				count+=1;
			}
			else if(count==4) {
				developer=line.trim();
				System.out.println(developer);
				soundex_developer=soundexSentence(developer);
				System.out.println(soundex_developer);
				count+=1;
			}
			else if(count==5) {
				description=line.trim();
				System.out.println(description);
				soundex_description=soundexSentence(description);
				System.out.println(soundex_description);
				count+=1;
			}
		}
		bufferedReader.close();
		fileReader.close();
		Document doc=new Document();
		
		doc.add(new TextField("name",name,Field.Store.YES));
		TextField soundex_name_index=new TextField("soundex_name",soundex_name,Field.Store.NO);
		doc.add(soundex_name_index);
		
		doc.add(new TextField("description",description,Field.Store.YES));
		TextField soundex_desc_index=new TextField("soundex_description",soundex_description,Field.Store.NO);
		doc.add(soundex_desc_index);
		
		doc.add(new TextField("year",year,Field.Store.YES));
		TextField soundex_year_index=new TextField("soundex_year",soundex_year,Field.Store.NO);
		doc.add(soundex_year_index);
		
		doc.add(new TextField("location",location,Field.Store.YES));
		TextField soundex_location_index=new TextField("soundex_location",soundex_location,Field.Store.NO);
		doc.add(soundex_location_index);
		
		doc.add(new TextField("developer",developer,Field.Store.YES));
		TextField soundex_developer_index=new TextField("soundex_developer",soundex_developer,Field.Store.NO);
		doc.add(soundex_developer_index);
		
		doc.add(new StringField("url",url,Field.Store.YES));
		return doc;
	}
	static String soundexSentence(String s) throws IOException {
		String[] swords=s.split("\\W+");
		String[] stop_words=new String[]{"those", "the", "a", "in", "why", "had", "until", "an", "mustn't", "isn", "any", "didn", "y", "weren", "wouldn't", "she's", "because", "should", "more", "i", "very", "too", "needn", "was", "here", "if", "who", "shouldn't", "your", "other", "own", "about", "mustn", "being", "yourselves", "from", "s", "them", "under", "has", "couldn't", "haven't", "wouldn", "which", "ours", "now", "after", "each", "on", "itself", "up", "than", "whom", "further", "their", "aren't", "having", "into", "be", "to", "can", "down", "him", "what", "m", "ve", "have", "needn't", "am", "theirs", "hadn", "himself", "myself", "where", "you'll", "me", "my", "when", "she", "does", "ma", "of", "o", "you're", "below", "by", "that", "not", "just", "hers", "nor", "with", "our", "his", "some", "that'll", "ourselves", "is", "its", "they", "were", "ain", "these", "it", "how", "wasn't", "same", "it's", "again", "he", "all", "for", "at", "between", "didn't", "won", "will", "hasn", "you'd", "but", "during", "mightn", "shouldn", "you", "do", "mightn't", "doing", "ll", "aren", "t", "before", "don", "through", "yourself", "couldn", "or", "against", "doesn", "been", "this", "such", "only", "doesn't", "herself", "few", "over", "shan", "weren't", "hadn't", "themselves", "we", "most", "d", "above", "isn't", "no", "yours", "and", "should've", "are", "haven", "don't", "once", "did", "you've", "re", "off", "both", "out", "her", "shan't", "as", "so", "hasn't", "wasn", "then", "won't", "while", "there"};
		ArrayList<String> soundex = new ArrayList<String>();
		Map<Character,Integer> simsound = new HashMap<Character,Integer>();
		simsound.put('d', 3);
		simsound.put('s', 2);
		simsound.put('n', 5);
		simsound.put('o', 0);
		simsound.put('j', 2);
		simsound.put('g', 2);
		simsound.put('y', 0);
		simsound.put('x', 2);
		simsound.put('p', 1);
		simsound.put('q', 2);
		simsound.put('m', 5);
		simsound.put('r', 6);
		simsound.put('b', 1);
		simsound.put('l', 4);
		simsound.put('a', 0);
		simsound.put('c', 2);
		simsound.put('u', 0);
		simsound.put('e', 0);
		simsound.put('i', 0);
		simsound.put('w', 0);
		simsound.put('t', 3);
		simsound.put('f', 1);
		simsound.put('h', 0);
		simsound.put('z', 2);
		simsound.put('v', 1);
		simsound.put('k', 2);
		String sdx="";
		for(int i=0;i<swords.length;i++) {
			if(!Arrays.stream(stop_words).anyMatch(swords[i].toLowerCase()::equals)) { 
				sdx=get_soundex(swords[i]);
				soundex.add(sdx);
				if(sdx.length()>0 && Character.isLetter(sdx.charAt(0))) {
					soundex.add(simsound.get(sdx.charAt(0))+sdx.substring(1));
				}
			}
		}
		String sname=String.join(" ", soundex);
		return sname;
	}
}
