package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EvilHangmanChooser implements IHangmanChooser {
  private int maxGuesses;
  private String currentPattern = "";
  private SortedSet<String> possibleWords = new TreeSet<String>();
  private SortedSet<Character> guesses = new TreeSet<Character>();

  public EvilHangmanChooser(int wordLength, int maxGuesses) {
    if (wordLength < 1 || maxGuesses < 1) {
      throw new IllegalArgumentException();
    }

    File file = new File("data/scrabble.txt");
    try {
      Scanner in = new Scanner(file);
      boolean foundLength = false;
      while (in.hasNextLine()) {
        String word = in.nextLine();
        if (word.length() == wordLength) {
          foundLength = true;
          this.possibleWords.add(word);
        }
      }
      if (!foundLength) {
        throw new IllegalStateException();
      }
    }
    catch (FileNotFoundException e) {
    }

    this.maxGuesses = maxGuesses;

    for (int i = 0; i < wordLength; i++) {
      this.currentPattern += "-";
    }
  }


  @Override
  public int makeGuess(char letter) {
    if (this.getGuessesRemaining() < 1) {
      throw new IllegalStateException();
    }
    if (this.getGuesses().contains(letter)) {
      throw new IllegalArgumentException();
    }
    if (!(letter >= 'a' && letter <= 'z')) {
      throw new IllegalArgumentException();
    }

    // Create set of patterns for current family
    Set<String> patterns = new TreeSet<String>();

    Iterator<String> iterator = this.possibleWords.iterator();
    while(iterator.hasNext()) {
      String pattern = "";
      String element = iterator.next();
      for (int i = 0; i < this.currentPattern.length(); i++) {
        if (element.charAt(i) == letter) {
          pattern += letter;
        }
        else {
          pattern += "-";
        }
      }
      patterns.add(pattern);
    }

    Map<String, SortedSet<String>> patternToFamily = new TreeMap<String, SortedSet<String>>();
    iterator = patterns.iterator();
    while(iterator.hasNext()) {
      String possiblePattern = iterator.next();
      patternToFamily.put(possiblePattern, this.getFamilyForPattern(letter, possiblePattern, this.possibleWords));
    }

    String pattern2 = "";
    String newPattern = "";
    int max = 0;
    for (Map.Entry mapElement : patternToFamily.entrySet()) {
      SortedSet<String> fam = (SortedSet<String>) mapElement.getValue();
      if (fam.size() > max) {
        max = fam.size();
        this.possibleWords = fam;
        pattern2 = (String) mapElement.getKey();
      }
    }

    for (int j = 0; j < this.currentPattern.length(); j++) {
      if (this.currentPattern.charAt(j) == '-') {
        newPattern += pattern2.charAt(j);
      }
      else {
        newPattern += this.currentPattern.charAt(j);
      }
    }

    this.currentPattern = newPattern;

    int counter = 0;
    for (int i = 0; i < this.currentPattern.length(); i++) {
      if (this.currentPattern.charAt(i) == letter) {
        counter++;
      }
    }

    if (counter == 0) {
      this.maxGuesses--;
    }
    this.guesses.add(letter);
    return counter;
  }

  public SortedSet<String> getFamilyForPattern(char letter, String pattern, SortedSet<String> currentFamily) {
    SortedSet<String> familyForPattern = new TreeSet<String>();

    Iterator<String> iterator = currentFamily.iterator();
    while(iterator.hasNext()) {
      String element = iterator.next();
      // get list of chars in the pattern
      Set<Character> chars = new TreeSet<Character>();
      for (int i = 0; i < pattern.length(); i++) {
        if (pattern.charAt(i) != '-') {
          chars.add(pattern.charAt(i));
        }
      }

      boolean partOfFamily = true;
      for (int i = 0; i < pattern.length(); i++) {
        if (pattern.charAt(i) == '-') {
          if (element.charAt(i) == letter) {
            partOfFamily = false;
            break;
          }
        }
        else {
          if (element.charAt(i) != letter) {
            partOfFamily = false;
            break;
          }
        }
      }

      if (partOfFamily) {
        familyForPattern.add(element);
      }
    }
      return familyForPattern;
  }

  @Override
  public boolean isGameOver() {
    if (this.currentPattern.indexOf('-') < 0) {
      return true;
    }
    if (this.getGuessesRemaining() == 0) {
      return true;
    }
    return false;
  }

  @Override
  public String getPattern() {
    return this.currentPattern;
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
    this.maxGuesses = 0;
    return this.currentPattern;
  }
  }
