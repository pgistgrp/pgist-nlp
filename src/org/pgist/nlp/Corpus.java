package org.pgist.nlp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * lnfomap-nlp corpus
 * @author kenny
 *
 */
public class Corpus {

    
    private String corpusName;
    private String modelPath;
    private Runtime runtime = Runtime.getRuntime();
    
    
    public Corpus(String corpusName, String modelPath) {
        this.corpusName = corpusName;
        
        if (modelPath==null || "".equals(modelPath)) {
            this.modelPath = "/usr/local/share/infomap-nlp/models";
        } else {
            this.modelPath = modelPath;
        }
    }
    
    
    public String getCorpusName() {
        return corpusName;
    }


    public String getModelPath() {
        return modelPath;
    }


    /**
     * associate one word in the given corpus
     * @param word
     * @param count
     * @return
     */
    public List associate(Word word, int count) throws Exception {
        List list = new ArrayList(1024);
        
        String[] cmdarray = {
            "/usr/local/bin/associate",
            "-m",
            modelPath,
            "-c",
            corpusName,
            "-n",
            ""+count,
            word.getName()
        };
        System.out.println("hello, "+cmdarray[7]);
        
        Process process = runtime.exec(cmdarray);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        
        int newLevel = word.getLevel() + 1;
        String line = null;
        while ((line = reader.readLine())!=null) {
            String[] s = line.trim().split(":");
            if (s[0]==null || s[1]==null) continue;
            Word newWord = new Word();
            newWord.setName(s[0]);
            newWord.setScore(Float.parseFloat(s[1]));
            newWord.setLevel(newLevel);
            list.add(newWord);
        }//while
        
        if (list.size()>0) {
            Word first = (Word) list.get(0);
            if (!first.getName().equals(word.getName())) {
                for (int i=0; i<list.size(); i++) {
                    Word one = (Word) list.get(i);
                    if (one.getName().equals(word.getName())) {
                        one = (Word) list.remove(i);
                        list.add(0, one);
                        break;
                    }
                }
            }
        }
        
        return list;
    }


}
