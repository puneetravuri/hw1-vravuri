package utils;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import typesystem.GeneTag;

/**
 * This is a utility class that has methods used across all annotators
 * 
 * @author vravuri
 * 
 */
public class GeneTaggingUtils {

  /**
   * Adds the gene phrase as an annotation
   * 
   * @param aJCas
   *          CAS to which the annotations is added
   * @param lineCode
   *          Unique ID of the line
   * @param start
   *          Start index of the phrase
   * @param end
   *          End index of the phrase
   * @param content
   *          Phrase content that will be annotated
   */
  public void addToAnnotator(JCas aJCas, String lineCode, int start, int end, String content) {
    GeneTag geneTag = new GeneTag(aJCas);
    geneTag.setBegin(start);
    geneTag.setEnd(end);
    geneTag.setLineCode(lineCode);
    geneTag.setContent(content);
    geneTag.addToIndexes();
  }

  /**
   * The required metadata like startindex of the word in the line, end index of the word in the
   * line, whether the name is a named entity or not and the list of words in a list is retrieved
   * and returned. This metadata eases many calculations that will be performed later on the line by
   * the annotator.
   * 
   * @param ner
   *          Named entity recognizer
   * @param namedEntityPresence
   *          Existance of a word in the named entity
   * @param startIndexes
   *          Start index of every word in the line
   * @param endIndexes
   *          End index of every word in the line
   * @param wordList
   *          List of words in the line
   * @param line
   *          Line for which the metadata is being computed
   * @throws ResourceInitializationException
   *           When any issue with the named entity recognizer
   */
  public void populateLineMetadata(PosTagNamedEntityRecognizer ner,
          ArrayList<Boolean> namedEntityPresence, ArrayList<Integer> startIndexes,
          ArrayList<Integer> endIndexes, ArrayList<String> wordList, String line)
          throws ResourceInitializationException {

    int countedOffset = 0;

    Map<Integer, Integer> namedEntities = ner.getGeneSpans(line);

    StringTokenizer sToken = new StringTokenizer(line, " ");

    /*
     * For every word of the line, each arraylist is populated accordingly. At the end of the loop,
     * metadata for every word will be computed
     */
    while (sToken.hasMoreTokens()) {
      String nextWord = sToken.nextToken();

      /* If the word contains period characters, they are removed from the word */
      if (nextWord.endsWith(".") || nextWord.endsWith(",") || nextWord.endsWith(":")
              || nextWord.endsWith(";")) {
        nextWord = nextWord.substring(0, nextWord.length() - 1);
        wordList.add(nextWord);
        int startPosition = line.indexOf(nextWord, countedOffset);
        startIndexes.add(startPosition);
        endIndexes.add(startPosition + nextWord.length() - 1);
        namedEntityPresence.add(isWordInNamedEntity(namedEntities, startPosition));
      } else {
        wordList.add(nextWord);
        int startPosition = line.indexOf(nextWord, countedOffset);
        startIndexes.add(startPosition);
        endIndexes.add(startPosition + nextWord.length() - 1);
        namedEntityPresence.add(isWordInNamedEntity(namedEntities, startPosition));
      }
      countedOffset += nextWord.length();
    }
  }

  /**
   * Returns the unique ID of the line
   * 
   * @param line
   *          Line for which the unique ID is returned
   * @return Unique ID
   */
  public String getLineCode(String line) {
    StringTokenizer sToken = new StringTokenizer(line, " ");
    return sToken.nextToken();
  }

  /**
   * Returns the data part of the line
   * 
   * @param line
   *          Line for which the data is returned
   * @return Data of the line
   */
  public String getLineData(String line) {
    StringTokenizer sToken = new StringTokenizer(line, " ");
    String lineCode = sToken.nextToken();
    return line.substring(lineCode.length() + 1, line.length());
  }

  /**
   * Adjusted index of the provided index by removing white space character occurrences
   * 
   * @param line
   *          Line being used to calculate the index
   * @param index
   *          Input index for which adjusted value is being calculated
   * @return Adjusted index value
   */
  public int getAdjustedIndex(String line, int index) {
    int count = 0;

    for (int i = 0; i < index; i++) {
      if (line.charAt(i) == ' ')
        count++;
    }
    return index - count;
  }

  /**
   * Checks if the provided word is a named entity in that line
   * 
   * @param namedEntities
   *          Named entitied list of the line
   * @param startPosition
   *          Start position of the target word
   * @return True if the word is a named entity, false otherwise
   */
  private boolean isWordInNamedEntity(Map<Integer, Integer> namedEntities, int startPosition) {

    for (Integer key : namedEntities.keySet()) {
      if (key <= startPosition && namedEntities.get(key) >= startPosition) {
        return true;
      }
    }
    return false;
  }

}
