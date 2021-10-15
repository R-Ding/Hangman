package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;

public class RandomHangmanChooser implements IHangmanChooser {
  private int wordLength;
  private int maxGuesses;
  private static final Random RANDOM = new Random();
  private final String secretWord;
  private SortedSet<Character> guesses = new TreeSet<Character>();

  public RandomHangmanChooser(int wordLength, int maxGuesses) {
    if (wordLength < 1 || maxGuesses < 1) {
      throw new IllegalArgumentException();
    }

    SortedSet<String> possibleWords = new TreeSet<String>();
    File file = new File("data/scrabble.txt");
    try {
      Scanner in = new Scanner(file);
      boolean foundLength = false;
      while (in.hasNextLine()) {
        String word = in.nextLine();
        if (word.length() == wordLength) {
          foundLength = true;
          possibleWords.add(word);
        }
      }
      if (!foundLength) {
        throw new IllegalStateException();
      }
    }
    catch (FileNotFoundException e) {

    }

    int randIndex = RANDOM.nextInt(possibleWords.size());
    Iterator<String> iterator = possibleWords.iterator();
    int counter = 0;
    String chosenWord = "";
    while(iterator.hasNext()) {
      String element = iterator.next();
      if (counter == randIndex) {
        chosenWord = element;
      }
      counter++;
    }
    this.secretWord = chosenWord;

    this.wordLength = wordLength;
    this.maxGuesses = maxGuesses;
  }

  @Override
  public int makeGuess(char letter) {
    if (this.getGuessesRemaining() < 1) {
      throw new IllegalStateException();
    }
    if (this.getGuesses().contains(letter)) {
      throw new IllegalStateException();
    }
    if (!(letter >= 'a' && letter <= 'z')) {
      throw new IllegalArgumentException();
    }

    int counter = 0;
    for (int i = 0; i < this.secretWord.length(); i++) {
      if (this.secretWord.charAt(i) == letter) {
        counter++;
      }
    }
    if (counter == 0) {
      this.maxGuesses--;
    }
    this.guesses.add(letter);
    return counter;
  }

  @Override
  public boolean isGameOver() {
    if (this.getPattern().equals(this.secretWord)) {
      return true;
    }
    if (this.getGuessesRemaining() == 0) {
      return true;
    }
    return false;
  }

  @Override
  public String getPattern() {
    String pattern = "";
    for (int i = 0; i < this.secretWord.length(); i++) {
      if (this.guesses.contains(this.secretWord.charAt(i))) {
        pattern += this.secretWord.charAt(i);
      }
      else {
        pattern += "-";
      }
    }
    return pattern;
  }

  @Override
  public SortedSet<Character> getGuesses() {
    return this.guesses;
  }

  @Override
  public int getGuessesRemaining() {
    return this.maxGuesses;
  }

  @Override
  public String getWord() {
    this.guesses.clear();
    for (int i = 0; i < this.secretWord.length(); i++) {
      this.guesses.add(this.secretWord.charAt(i));
    }

    return this.secretWord;
  }
}