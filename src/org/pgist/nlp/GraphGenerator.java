package org.pgist.nlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;


/**
 * Graph generator. Generage data files for Touch Graph from informap-nlp.
 * @author kenny
 *
 */
public class GraphGenerator {

    
    /**
     * @param args
     */
    public static void main(String[] args) {
        int MAX_LEVEL = 3;
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Word origin = new Word();
        origin.setScore(1.0f);
        System.out.println("Please input the beginning word:");
        try {
            origin.setName(reader.readLine());
        } catch(Exception e) {
            System.out.println("wrong input.");
            return;
        }
        
        Corpus corpus = new Corpus("seattle", null);
        
        HashSet set = new HashSet(1024);
        set.add(origin.getName());
        
        Stack stack = new Stack();
        stack.push(origin);
        
        List vertexList = new ArrayList(1024);
        
        try {
            while (!stack.empty()) {
                Word oneWord = (Word)stack.pop();
                System.out.println(oneWord.getName()+" --> "+oneWord.getLevel());
                
                List edgeList = corpus.associate(oneWord, 5);
                if (edgeList.size()==0) continue;
                vertexList.add(edgeList);
                System.out.println("--> "+((Word)(edgeList.get(0))).getName());

                for (int i=0; i<edgeList.size(); i++) {
                    
                    Word word = (Word) edgeList.get(i);
                    if (word.getName().equals(oneWord.getName())) continue;
                    if (word.getLevel()<=MAX_LEVEL && !set.contains(word.getName())) {
                        stack.push(word);
                        set.add(word.getName());
                    }
                    
                }//for i
            }//while
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
        
        try {
            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(
                        "output.txt"
                    )
                )
            );
            for (int i=0; i<vertexList.size(); i++) {
                List edgeList = (List) vertexList.get(i);
                for (int j=0; j<edgeList.size(); j++) {
                    Word one = (Word) edgeList.get(j);
                    writer.append(one.getName());
                    if (j<edgeList.size()-1) {
                        writer.append(" ");
                    } else {
                        writer.append("\n");
                    }
                }//for j
            }//for i
            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
        
        System.out.println(set);
    }
    

}
