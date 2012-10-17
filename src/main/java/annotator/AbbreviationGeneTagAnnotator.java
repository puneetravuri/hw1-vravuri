package annotator;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import utils.GeneTaggingUtils;
import utils.PosTagNamedEntityRecognizer;

/**
 * This annotator performs annotations based on abbreviations. The abbreviation words that are named
 * entities are analyzed to be added to the annotations.
 * 
 * @author vravuri
 * 
 */
public class AbbreviationGeneTagAnnotator extends JCasAnnotator_ImplBase {

  private ArrayList<Boolean> namedEntityPresence;

  private ArrayList<Integer> startIndexes;

  private ArrayList<Integer> endIndexes;

  private ArrayList<String> wordList;

  private ArrayList<String> exceptionWords = new ArrayList<String>();

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    /* Input document is retrieved from the CAS */
    String inputContent = aJCas.getDocumentText();

    /* The exception word arraylist is populated */
    fillExceptionWords();

    /* Input document is tokenized wrt lines */
    StringTokenizer lines = new StringTokenizer(inputContent, "\n");

    try {
      PosTagNamedEntityRecognizer ner = new PosTagNamedEntityRecognizer();
      GeneTaggingUtils tagUtils = new GeneTaggingUtils();

      /* Processing for each line is performed */
      while (lines.hasMoreTokens()) {
        String line = lines.nextToken();
        String lineCode = tagUtils.getLineCode(line);
        String lineData = tagUtils.getLineData(line);

        namedEntityPresence = new ArrayList<Boolean>();
        startIndexes = new ArrayList<Integer>();
        endIndexes = new ArrayList<Integer>();
        wordList = new ArrayList<String>();

        /*
         * For every line, the required metadata like startindex of the word in the line, end index
         * of the word in the line, whether the name is a named entity or not and the list of words
         * in a list is retrieved. This metadata eases many calculations that will be performed
         * later on the line
         */
        tagUtils.populateLineMetadata(ner, namedEntityPresence, startIndexes, endIndexes, wordList,
                lineData);

        /* Annotations, if necessary will be added based on the line analysis */
        addAbbrAnnotations(aJCas, lineCode, lineData);
      }
    } catch (ResourceInitializationException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method performs analysis per line of input and adds the annotations if needed
   * 
   * @param aJCas
   *          CAS object
   * @param lineCode
   *          Unique ID of the line
   * @param lineData
   *          Data of the line
   */
  private void addAbbrAnnotations(JCas aJCas, String lineCode, String lineData) {
    int annotationStart = 0;
    int annotationEnd = 0;
    GeneTaggingUtils tagUtils = new GeneTaggingUtils();

    /* Every line of the word is iterated and checked */
    for (int i = 0; i < wordList.size(); i++) {
      String word = wordList.get(i);

      if (isWordCamelCased(word)) {

        /*
         * Set the start index and end index to the word initially. They will change in case
         * additional words get added to the phrase
         */
        annotationStart = startIndexes.get(i);
        annotationEnd = endIndexes.get(i);

        /*
         * Additional words are added to the phrase if the word is alpha-numeric or if it hyphenated
         * or if it is a named entity
         */
        for (int j = i; j >= 0
                && ((!(wordList.get(j).matches("^[a-zA-Z]*$")) && wordList.get(j).matches(
                        "^[a-zA-Z0-9]*$"))
                        || wordList.get(j).contains("-")
                        || (wordList.get(j).startsWith("(") && wordList.get(j).endsWith(")")) || namedEntityPresence
                          .get(j)); j--) {
          if (j != i)
            annotationStart -= (1 + wordList.get(j).length());
        }

        /*
         * The phrase with abbreviation is added as an annotation only if it has more than a single
         * word and is not part of the exception list
         */
        String annotatedWord = lineData.substring(annotationStart, annotationEnd + 1);
        if (!exceptionWords.contains(annotatedWord)) {
          tagUtils.addToAnnotator(aJCas, lineCode,
                  tagUtils.getAdjustedIndex(lineData, annotationStart),
                  tagUtils.getAdjustedIndex(lineData, annotationEnd),
                  lineData.substring(annotationStart, annotationEnd + 1));
        }
      }

    }
  }

  /**
   * Checks if the word is camel cased or not
   * 
   * @param word
   *          Word to be checked
   * @return True if it is camel cased, false otherwise
   */
  private boolean isWordCamelCased(String word) {
    for (int i = 1; i < word.length(); i++) {
      if (Character.isUpperCase(word.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Populates the exception words that will not be annotated since they are not genes and are just
   * dictionary and common words
   */
  private void fillExceptionWords() {
    exceptionWords.add("CO2");
  }

}
