package org.pgist.nlp;


/**
 * A word and its score to some other word
 * @author kenny
 *
 */
public class Word {
    
    
    private String name  = "";
    private float  score = 0.0f;
    private int    level = 0;
    
    
    public String getName() {
        return name;
    }
    
    
    public void setName(String name) {
        this.name = name;
    }
    
    
    public float getScore() {
        return score;
    }
    
    
    public void setScore(float score) {
        this.score = score;
    }


    public int getLevel() {
        return level;
    }


    public void setLevel(int level) {
        this.level = level;
    }
    

}
