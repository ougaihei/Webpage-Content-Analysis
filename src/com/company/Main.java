package com.company;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class Main {




    public static void main(String[] args) {
        String url;

	    try {
	        //url = args[0];
           url = "http://www.cnn.com/2013/06/10/politics/edward-snowden-profile/";
           // url = "http://www.marketwatch.com/story/fed-might-start-balance-sheet-drawdown-in-september-fomc-minutes-hint-2017-07-05";

            if (!testURL(url))
            {
                url = askInput();
            }
        }
        catch(Exception e){
	        System.out.println("Input either not supplied or not a string, try again:"+e.toString());
	        url = askInput();
        }


       Document doc = readPage(url);
       Set<String> keywords = assembleFinalList(doc);
       printResults(keywords);

    }

    /**
     * Prints the final result
     * @param wordset - the set containing all keywords
     */
    private static void printResults(Set<String> wordset){
        System.out.println("==================================================");
        StringBuilder res = new StringBuilder();
        for (String s: wordset){
            res.append(s).append(", ");
        }
        res = new StringBuilder(res.substring(0, res.length() - 2));
        System.out.println(res);
        System.out.println("==================================================");

    }

    /**
     * Assembles the final list of all 'keywords' related to content found, through tags, meta information, and
     * text analysis.
     * First, the words from all tags and meta information is pooled together and analyzed.
     * Then, the entire text of the page is analyzed (trying to avoid things covered in the previous step),
     * by being inserted into a map with the count of the word as the value, and the word itself as the key.
     * Then, the word with the highest appearance rate is determined, with stop words omitted and words shorter than 5 letters
     * ignored. A set is then created of all the words found in the text with at least 1/3 as many appearance as the most
     * common word.
     * Finally, the set to-be-returned is constructed by combining the two sets of analyzed data.
     * @param doc - the document being analyzed
     * @return a set containing all keywords
     */
    private static Set<String> assembleFinalList(Document doc){
        Set<String> allContent = new HashSet<String>();
        for (String s : determineTagContent(doc)) {
            allContent.add(s);
        }
        for (String s : determineMetaContent(doc)) {
            allContent.add(s);
        }
        allContent = trimTagContent(allContent);
        System.out.println(allContent);

        Map<String,Integer> allWords = determineCommonWords(doc);
        Set<String> keys = allWords.keySet();
        Collection<Integer> vals = allWords.values();
        int maxNumofWords = 0;
        for (Integer i : vals){
            if(i>maxNumofWords) {
                maxNumofWords = i;
            }
        }

        Set<String> newAdditions = new HashSet<String>();
        for(String s : keys){
            if (allWords.get(s)>=maxNumofWords/3){
                newAdditions.add(s.replace("_"," "));
            }
        }
        System.out.println(newAdditions);

        newAdditions.addAll(allContent);
        return newAdditions;
    }

    /**
     * Further analyzes the data gathered from looking at tags and meta information, breaking it down
     * into single words and removing any stop words found. Creates a temporary set of individual words before
     * pruning stop words.
     * @param inputSet - the set of words discovered on the page from tags and meta info
     * @return a trimmed down set with less excess, more focus, and removed stop words
     */
    private static Set<String> trimTagContent(Set<String> inputSet) {
        Set<String> temp = new HashSet<String>();
        String bigString = "";
        for (String s : inputSet){
            bigString +=s+" ";
        }
        bigString = removePunctuation(bigString);
        String[] wordArr = bigString.split(" ");
        for (String s: wordArr){
            temp.add(s);
        }


        Set<String> keyTagWords = new HashSet<String>();
        for (String s : temp){
            if (!isBadWord(s) && s.length()>=3){
                keyTagWords.add(s);
            }
        }

         return keyTagWords;
    }

    /**
     * Removes punctuation from a given string
     * @param s - the string to remove punctuation from
     * @return the string without punctuation
     */
    private static String removePunctuation(String s){
        s = s.replace("."," ");
        s = s.replace(","," ");
        s = s.replace(":"," ");
        s = s.replace(";"," ");
        s = s.replace("?"," ");
        s = s.replace("!"," ");
        s = s.replace("\""," ");
        s = s.replace("\'"," ");
        s = s.replace("’"," ");
        s = s.replace("`"," ");
        s = s.replace("-","");
        s = s.replace("&"," ");
        s = s.replace("("," ");
        s = s.replace(")"," ");
        return s;
    }

    /**
     * Creates a map of all the words found in the text of the page and their occurrences.
     * First the text is acquired, and then various punctuation are removed.
     * Then, if the current word being looked at after splitting the page text by " " is in the map, increment the count. Otherwise,
     * add the new word with a count of 1.
     * @param doc - the document to analyze
     * @return a HashMap containing word, count pairs
     */
    private static Map<String, Integer> determineCommonWords(Document doc){
        Map<String, Integer> result = new HashMap<String, Integer>();
        String page = doc.text();

        page = removePunctuation(page);

        String[] allwords = page.split(" ");
        for (String s : allwords){
            if (result.containsKey(s)){
                result.replace(s,result.get(s),result.get(s)+1);
            }
            else {
                if (s.length()>4 && !s.toUpperCase().equals(s) && !isBadWord(s)) {
                    result.put(s, 1);
                }
            }
        }

        return result;
    }

    /**
     * A method to check whether a word is a stop word.  The array of stop words was constructed from a list of
     * stop words provided at http://xpo6.com/list-of-english-stop-words/
     * @param s - the string to check
     * @return - true if a stop word, false if not
     */
    private static  Boolean isBadWord(String s){
        String[] stopWords = {"couldn","shouldn","wouldn","a", "about", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};
        for (String str : stopWords){
            if (str.equals(s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Discovers content based on meta tagging in the header of an HTML page
     * @param doc - the document to search through
     * @return - the found content
     */
    private static ArrayList<String> determineMetaContent(Document doc){
        ArrayList<String> discoveries = new ArrayList<String>();
        String[] metaWords = {"author","keyword","description","tag","keywords","source"};
        Elements meta = doc.select("meta");
        for (Element mt : meta){
            for (String s : metaWords){
                if (mt.attr("name").contains(s)||mt.attr("property").contains(s)){
                    discoveries.add(mt.attr("content"));
                }
            }
        }
        return discoveries;
    }

    /**
     * Discovers content based off of specific HTML tags that are likely to include relevant information
     * @param doc- the document to search through
     * @return the found content, parsed by parseContentElements
     */
    private static ArrayList<String> determineTagContent(Document doc){
        ArrayList<String> content = new ArrayList<String>();
        String[] criteria = {"title","h1"};

        for (String s : criteria){
            Elements currentCriteria = doc.select(s);
            for(int i=0;i<currentCriteria.size();i++){
                content.add(currentCriteria.get(i).toString());
            }
        }

       return parseContentElements(content);

    }

    /**
     * Parses the results of content discovered through looking at specific HTML elements, parsed to remove other sub-elements
     * @param toParse- A list of the found items so far
     * @return List of content without other HTML inside of it
     */
    private static ArrayList<String> parseContentElements(ArrayList<String> toParse){
        for (int i=0;i<toParse.size();i++){
            String s = toParse.get(i);
            //toParse.set(i,s.substring(s.indexOf(">")+1,s.lastIndexOf("<")));
            toParse.set(i,Jsoup.parse(s).text());
        }
        return toParse;
    }

    /**
     * Tests the provided string to see if it's a valid address by connecting to it.
     * @param url - a String containing a potential URL
     * @return - If the URL is valid, return true, otherwise return false.
     */
    private static boolean testURL(String url) {
        try {
            URLConnection testCon = new URL(url).openConnection();
        }
        catch (Exception e){
            System.out.println("Invalid URL");
            return false;
        }
        return true;
    }

    /**
     * Asks for user input for a URL. If the user enters nothing and returns, the method completes, otherwise, it will return the new input.
     * @return A string containing a new URL.
     */
    private static String askInput(){
        System.out.println("Would you like to attempt to enter a new URL? If no, just hit enter, otherwise type in the URL and then hit enter.");
        Scanner scan = new Scanner(System.in);
        String inp = scan.nextLine();
        if (inp.equals("")){
            System.exit(0);
        }
        if(!testURL(inp)){
            askInput();
        }

        return inp;

    }

    /**
     *  Takes a URL and fetches the page itself the URL points to, utilizing jsoup.
     * @param url - The URL to attempt to connect to
     * @return - A document containing the content of the webpage
     */
    private  static Document readPage(String url){
        Document page = null;
        try{
            page = Jsoup.connect(url).get();
        } catch(Exception e){
            System.out.println("There was an error reading the page, check your internet connection or the URL entered.");
           System.exit(-1);
        }

        return page;
    }
}
