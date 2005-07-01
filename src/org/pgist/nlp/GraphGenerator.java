package org.pgist.nlp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 * Graph generator. Generage data files for Touch Graph from informap-nlp.
 * @author kenny
 *
 */
public class GraphGenerator {

    
    /**
     * print help information
     */
    private static void printHelp() {
        System.out.println("Usage: infomap -c seattle -o filename [-d depthlist]");
        System.out.println("  -c corpus name");
        System.out.println("  -o output file name");
        System.out.println("  -d recursive depth list");
        System.out.println("Examples:");
        System.out.println("  With default settings (ie., -d 10, 8, 5):");
        System.out.println("    infomap -c seattle -o output.xml");
        System.out.println("  With depth setting");
        System.out.println("    infomap -c seattle -o output.xml -d 10,8,5,3......");
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        String corpusName = "";
        String outputFile = "";
        int MAX_LEVEL = 3;
        int[] depths = null;
        
        //check arguments
        if (args.length<4 || args.length==5) {
            printHelp();
            return;
        }
        if (!"-c".equals(args[0])) {
            printHelp();
            return;
        } else {
            corpusName = args[1];
        }
        if (!"-o".equals(args[2])) {
            printHelp();
            return;
        } else {
            outputFile = args[3];
        }
        if (args.length>4) {//depth list
            if (!"-d".equals(args[4])) {
                printHelp();
                return;
            } else {
                String[] depthStr = args[5].split(",");
                
                MAX_LEVEL = depthStr.length;
                depths = new int[MAX_LEVEL];
                System.out.println("depth: "+MAX_LEVEL);
                for (int i=0; i<depthStr.length; i++) {
                    try {
                        depths[i] = Integer.parseInt(depthStr[i]);
                    } catch(Exception e) {
                        System.out.println("Invalid number");
                        System.out.println();
                        printHelp();
                        return;
                    }
                }//for i
            }
        } else {
            depths = new int[MAX_LEVEL];
            depths[0] = 10;
            depths[1] = 8;
            depths[2] = 5;
        }
        
        
        /*---- begin main processing ----*/
        
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        //origin is the first word to be associated
        Word origin = new Word(null);
        origin.setScore(1.0f);
        System.out.println("Please input the beginning word:");
        try {
            origin.setName(reader.readLine());
        } catch(Exception e) {
            System.out.println("wrong input.");
            return;
        }
        
        Corpus corpus = new Corpus(corpusName, null);
        
        /*
         * map is used to exclude the duplicated word, and used to export nodes later
         */
        Map map = new HashMap(1024);
        map.put(origin.getName(), origin);
        
        /*
         * nodeList contains the same objects as in map, but all objects are in the
         * order of insertion.
         */
        List nodeList = new ArrayList(1024);
        nodeList.add(origin);
        
        /*
         * stack is to trace recursive invoking of vertexes
         * Note that the words which are put into the stack will be associated later
         */
        Stack stack = new Stack();
        stack.push(origin);
        
        /*
         * vertexList is used to contain edgeList, that is, each element in vertexList
         * is a list, this list contains many words, the first is the word to be associated,
         * and the other words are words which are found related to the first word.
         */
        List vertexList = new ArrayList(1024);
        
        try {
            while (!stack.empty()) {
                Word oneWord = (Word)stack.pop();//pop
                
                //associate word
                List edgeList = corpus.associate(oneWord, depths[oneWord.getLevel()]);
                System.out.println(
                        "find " + (edgeList.size()-1)
                        +" words in level " + (oneWord.getLevel()+1) +
                        " related to word "+oneWord.getName()
                );
                if (edgeList.size()==0) continue;
                vertexList.add(edgeList);
                
                //process each word
                for (int i=0; i<edgeList.size(); i++) {
                    
                    Word word = (Word) edgeList.get(i);
                    if (word.getName().equals(oneWord.getName())) continue;
                    if (word.getLevel()<MAX_LEVEL && !map.containsKey(word.getName())) {
                        /*
                         * only words in search level and not in the current map
                         * will be pushed into stack
                         */
                        stack.push(word);//push
                    }
                    if (!map.containsKey(word.getName())) {
                        //the first time to appear of the word
                        map.put(word.getName(), word);
                        nodeList.add(word);
                    }
                    
                }//for i
            }//while
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
        
        /*
         * output to external files
         */
        NanoXMLOutputter outputter = new NanoXMLOutputter();
        outputter.output(nodeList, vertexList, outputFile);
        
    }//main()
    
    
}//class GraphGenerator
