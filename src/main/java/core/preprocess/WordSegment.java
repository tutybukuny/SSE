package core.preprocess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class WordSegment implements Comparable<WordSegment>, Serializable {
    /**
     * the meaning word that this object contain
     */
    private String word;

    /**
     * indexes of all documents which contains this word
     */
    private ArrayList<Integer> documentIndexes;

    public WordSegment(String word) {
        this.word = word;
        documentIndexes = new ArrayList<>();
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ArrayList<Integer> getDocumentIndexes() {
        return documentIndexes;
    }

    public void setDocumentIndexes(ArrayList<Integer> documentIndexes) {
        this.documentIndexes = documentIndexes;
    }

    public int getDocumentIndexesSize() {
        return this.documentIndexes.size();
    }

    public void addDocumentIndex(int index) {
        if(!this.documentIndexes.contains(index))
            this.documentIndexes.add(index);
    }

    public void sortDocumentIndex() {
        Collections.sort(this.documentIndexes);
    }

    public boolean existsInDocument(int documentIndex) {
        return this.documentIndexes.contains(documentIndex);
    }

    @Override
    public int compareTo(WordSegment o) {
        return this.documentIndexes.size() - o.getDocumentIndexesSize();
    }
}
