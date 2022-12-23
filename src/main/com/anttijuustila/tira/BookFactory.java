package com.anttijuustila.tira;

import java.io.*;
import java.lang.invoke.WrongMethodTypeException;
import java.util.*;
import java.nio.charset.StandardCharsets;

/**
 * TODO: This class is used to create an instance of your implementation of the
 * Book interface.
 * <p>
 * Implement the <code>createBook()</code> method to return your instance of the
 * Book interface.
 * 
 * @author Antti Juustila
 * @version 1.0
 */
public final class BookFactory implements Book {
    private BookFactory() {
    }

    /**
     * TODO: You must implement this method so that it returns an instance of
     * your concrete class implementing the Book interface.
     * 
     * @return Your implementation of the Book interface.
     */

    private File bookFile;
    private File ignoreFile;
    private String ignoredWords[] = new String[39];
    private int lengthOfIgnoredwords;
    private int lengthOfSkippedWords;
    private int wordSizeUnique;
    Word[] allUniqueWords;
    private int wordSizeAll;
    private char word[] = new char[30];
    private String newWord;
    private long hashed;

    public Tree tree = new Tree();

    public static Book createBook() {
        BookFactory newBook = new BookFactory();
        return newBook;
    }

    @Override
    public void setSource(String fileName, String ignoreWordsFile) throws FileNotFoundException {
        bookFile = new File(fileName);
        ignoreFile = new File(ignoreWordsFile);

        if (bookFile.exists() && ignoreFile.exists()) {

        } else {
            throw new FileNotFoundException("The files were not found");
        }

    }

    @Override
    public void countUniqueWords() throws IOException, OutOfMemoryError {

        Scanner ignore = new Scanner(ignoreFile);

        int i = 0;
        while (ignore.hasNextLine()) {
            String line = ignore.nextLine();
            for (String word : line.split(",")) {
                ignoredWords[i] = word.toLowerCase();
                // lengthOfIgnoredwords++;
                i++;
            }
        }

        // reading file character by character
        FileReader reader = new FileReader(bookFile, StandardCharsets.UTF_8);
        int character;
        boolean wordIgnored = false;
        try {
            int j = 0;
            while ((character = reader.read()) != -1) {
                // check if letter
                if (Character.isLetter(character)) {
                    // add to word array
                    word[j] = (char) character;
                    j++;
                } else {
                    // we have reached the end of a word
                    if (word[1] == Character.MIN_VALUE) {
                        // word is too short
                        lengthOfSkippedWords++;
                    } else {
                        if (word[0] != Character.MIN_VALUE) {
                            wordSizeAll++;
                            // System.out.println("Total word count: " + wordSizeAll);
                            newWord = new String(word, 0, j);
                            newWord = newWord.toLowerCase();
                            // check if word is 'ignored'
                            for (String ignoredWord : ignoredWords) {
                                if (newWord.equals(ignoredWord)) {
                                    // the word was found in the ignored list
                                    lengthOfIgnoredwords++;
                                    wordIgnored = true;
                                    wordSizeAll--;
                                    break;
                                }
                            }
                            if (!wordIgnored) {
                                // add word to tree
                                // duplicates handled inside tree
                                tree.bstAdd(newWord, 1);
                            } else {
                                //
                            }
                            wordIgnored = false;
                        }
                    }
                    j = 0;
                    for (int k = 0; k < word.length; k++) {
                        if (word[k] != Character.MIN_VALUE) {
                            word[k] = Character.MIN_VALUE;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Encountered exception");
        }
    }

    @Override
    public void report() {
        allUniqueWords = new Word[getUniqueWordCount()];
        traversePreOrder(tree.root);
        sort(allUniqueWords);
        System.out.println("Top 100 words:\n-------------------------");
        for (int i = 0; i < allUniqueWords.length;i++) {
            if(i == 100){
                break;
            }
                String formattedText = String.format("%-15s %-5d", allUniqueWords[i].word, allUniqueWords[i].count);
                System.out.println((i+1) + ". \t" + formattedText);
        }

        System.out.println("\nTotal words in the file:" + getTotalWordCount());
        System.out.println("Total Unique words in the file:" + getUniqueWordCount());
        System.out.println("Total Ingored words in the file:" + lengthOfIgnoredwords);
        System.out.println("Total Skipped words in the file:" + lengthOfSkippedWords);
    }

    @Override
    public void close() {
        // Releases all the resources (e.g. memory) reserved by the counting
        bookFile = null;
        ignoreFile = null;
        ignoredWords = null;
        tree.root = null;
    }

    @Override
    public int getUniqueWordCount() {
        return wordSizeUnique;
    }

    @Override
    public int getTotalWordCount() {
        return wordSizeAll;
    }

    @Override
    public String getWordInListAt(int position) {
        return allUniqueWords[position].word;
    }

    @Override
    public int getWordCountInListAt(int position) {

        if (position < 0) {
            return -1;
        }

        return allUniqueWords[position].count;
    }

    public void traversePreOrder(Node node) {
        if (node != null) {
            traversePreOrder(node.right);
            visitNodes(node);
            traversePreOrder(node.left);
        }
    }

    public class Word {
        String word;
        int count;

        public Word(String word, int count) {
            this.word = word;
            this.count = count;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    class Node {

        long index;
        String word;
        int count;
        Node right;
        Node left;

        public Node(long index, String word, int count) {
            this.index = index;
            this.word = word;
            this.count = count;
            this.right = null;
            this.left = null;
        }

        public long Index(){
            return this.index;
        }

        public String Word() {
            return this.word;
        }

        public int Count() {
            return this.count;
        }

        public Node Right() {
            return this.right;
        }

        public Node Left() {
            return this.left;
        }

        public void SetIndex(long index) {
            this.index = index;
        }

        public void SetRight(Node node) {
            this.right = node;
        }

        public void SetLeft(Node node) {
            this.left = node;
        }
    };

    class Tree {
        Node root;

        public Tree() {
        }

        public Node Root() {
            return this.root;
        }

        public void SetRoot(Node theRoot) {
            this.root = theRoot;
        }

        public void bstAdd(String word, int count) {
            Node current = null;
            hashed = CalculateHash(word);

            if (tree.root == null) {
                tree.SetRoot(new Node(hashed, word, count));
                wordSizeUnique++;
            } else {
                current = tree.root;
            }
            while (current != null) {
                // check if word already exists
                if (current.word.equals(word)) {
                    current.count++;
                    break;
                }
                if (hashed > current.Index()) {
                    if (current.Right() != null) {
                        current = current.Right();
                    } else {
                        current.SetRight(new Node(hashed, word, count));
                        wordSizeUnique++;
                        break;
                    }
                } else {
                    if (current.Left() != null) {
                        current = current.Left();
                    } else {
                        current.SetLeft(new Node(hashed, word, count));
                        wordSizeUnique++;
                        break;
                    }
                }
            }
        }
    }

    // lÃ¤hde: https://cp-algorithms.com/string/string-hashing.html#calculation-of-the-hash-of-a-string
    public long CalculateHash(String word) {
        char[] givenWord = new char[word.length()];
        for (int i = 0; i < word.length(); i++){
            givenWord[i] = word.charAt(i);
        }
        int p = 31;
        double m = 1e9 + 9;
        long hash = 0;
        double p_pow = 1;
        for (char c : givenWord) {
            hash = (long) ((hash + (c - 'a' + 1) * p_pow) % m);
            p_pow = (p_pow * p) % m;
        }
        return hash;
    }

    public void visitNodes(Node node){
        // go into node and save word and its count
        Word w = new Word(node.word, node.count);
        int i = 0;
        while(allUniqueWords[i] != null){
            i++;
        }
        allUniqueWords[i] = w;
    }

    public void sort(Word[] array) {
        //inspiration for this method: https://www.geeksforgeeks.org/heap-sort/ 
        int length = array.length;
  
        //build heap (rearrange array)
        for(int i = length / 2 - 1; i >= 0; i--){
           heapify(array, length, i);
        }
  
        //extract elements from the heap, one by one
        for (int i = length - 1; i >= 0; i--) {
           //move current root to end
           Word temp = array[0];
           array[0] = array[i];
           array[i] = temp;
  
           //call max heapify on the reduced heap
           heapify(array, i, 0);
        }
     }
  
     public void heapify(Word array[], int n, int index) {
        int largest = index;
        int left = 2 * index + 1;
        int right = 2 * index + 2;
  
        //left child is larger than the root
        if (left < n && array[left].getCount() < array[largest].getCount()) {
           largest = left;
        }
  
        //right child is larger than the largest so far
        if (right < n && array[right].getCount() < array[largest].getCount()) {
           largest = right;
        }
  
        //largest is not the root
        if (largest != index) {
            Word temp = array[index];
           array[index] = array[largest];
           array[largest] = temp;
  
           //recursively heapify affected sub-tree
           heapify(array, n, largest);
        }
     }
}