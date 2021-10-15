package edu.caltech.cs2.project02.guessers;

import com.sun.jdi.CharType;
import edu.caltech.cs2.project02.interfaces.IHangmanGuesser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class AIHangmanGuesser implements IHangmanGuesser {
  private static String dictionary = "data/scrabble.txt";

  @Override
  public char getGuess(String pattern, Set<Character> guesses) {

    File file = new File(this.dictionary);
    List<String> possibleWords = new ArrayList<String >();

    // gets list of words amenable to the pattern
    try {
      Scanner in = new Scanner(file);
      while (in.hasNextLine()) {
        boolean amenable = false;
        String word = in.nextLine();
        if (word.length() == pattern.length()) {
          amenable = true;
          for (int k = 0; k < pattern.length(); k++) {
            if (pattern.charAt(k) == '-') {
              if (guesses.contains(word.charAt(k))) {
                amenable = false;
              }
            } else {
              if (word.charAt(k) != pattern.charAt(k)) {
                amenable = false;
              }
            }
          }

        }
        if (amenable) {
          possibleWords.add(word);
        }
      }
    }
    catch (FileNotFoundException e) {
    }

    // populate occurrences
    Map<Character, Integer> occurrences = new TreeMap<Character, Integer>();
    for (char letter = 'a'; letter <= 'z'; letter++) {
      int counter = 0;
      if (!guesses.contains(letter)) {
        for (int p = 0; p < possibleWords.size(); p++) {
          String word = possibleWords.get(p);

          if (word.contains("" + letter)) {
            counter++;
          }
        }
        occurrences.put(letter, counter);
      }
    }

    char guess = '=';
    int max = -100000;

    //char guess = '=';
    for (Character mapElement : occurrences.keySet()) {
      int count = occurrences.get(mapElement);
      if (count > max) {

        max = count;
        guess = mapElement;

      }
    }

    return guess;
  }

}
