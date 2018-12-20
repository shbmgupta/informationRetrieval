
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class indexReader {

	private static final String INDEX_DIR = "indexedFiles";

	private static void test(IndexReader indexReader ) throws IOException { 
		Fields fields = MultiFields.getFields(indexReader);
		Terms terms = fields.terms("url");
        TermsEnum iterator = terms.iterator();
        BytesRef byteRef = null;
        while((byteRef = iterator.next()) != null) {
        	//            String term = new String(byteRef.bytes, byteRef.offset, byteRef.length);
        	System.out.println(">>"+byteRef.utf8ToString());
        }
	}
	
	public static void main(String[] args) throws Exception{
		String queryTerm="akbr";
		FSDirectory dir = FSDirectory.open(Paths.get(INDEX_DIR));
		IndexReader reader = DirectoryReader.open(dir);
//		test(reader);
		IndexSearcher searcher = new IndexSearcher(reader);
		System.out.println(queryTerm);
		queryTerm=soundexSentence(queryTerm);
		Map<String,Float> boost = new HashMap<String,Float>();
		boost.put("soundex_name",0.3f);
		boost.put("soundex_developer",0.25f);
		boost.put("soundex_location",0.175f);
		boost.put("soundex_year",0.15f);
		boost.put("soundex_description",0.125f);
//		-----------------------------------------------------
		boost.put("name",0.3f);
		boost.put("developer",0.25f);
		boost.put("location",0.175f);
		boost.put("year",0.15f);
		boost.put("description",0.125f);
		
		
		MultiFieldQueryParser qp = new MultiFieldQueryParser(new String[] {"soundex_description","soundex_name","soundex_year","soundex_location","soundex_developer"}, new StandardAnalyzer(),boost);
//		MultiFieldQueryParser qp = new MultiFieldQueryParser(new String[] {"description","name","year","location","developer"}, new StandardAnalyzer(),boost);
		
		Query query = qp.parse(queryTerm);

//		query.createWeight(searcher,true,(float) 0.0);
//		PhraseQuery query=new PhraseQuery("name",queryTerm);
		 
		TopDocs foundDocs = searcher.search(query, 10);
        
		System.out.println("Total Results :: " + foundDocs.totalHits);
        
        //Let's print out the path of files which have searched term
        for (ScoreDoc sd : foundDocs.scoreDocs)
        {
            Document d = searcher.doc(sd.doc);
            System.out.println(d.get("name"));
            System.out.println(d.get("year"));
            System.out.println(d.get("location"));
            System.out.println(d.get("developer"));
            System.out.println(d.get("description"));
            System.out.println(d.get("url"));
            System.out.println(sd.score);
            System.out.println("-----------------------------------------");
        }
		
	}
	private static String get_soundex(String str) throws IOException {
		Process p = Runtime.getRuntime().exec("python3 soundex/get_soundex.py "+str);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s=stdInput.readLine();
        p.destroy();
        stdInput.close();
		return s;
	}
	private static String soundexSentence(String s) throws IOException {
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
				if(!Character.isDigit(sdx.charAt(0))) {
					soundex.add(simsound.get(sdx.charAt(0))+sdx.substring(1));
				}		
			}
		}
		String sname=String.join(" ", soundex);
		System.out.println("query>>"+sname);
		return sname;
	}
}
