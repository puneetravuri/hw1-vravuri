package annotator;

import utils.GeneTaggingUtils;
import utils.PosTagNamedEntityRecognizer;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * This annotator performs annotations on p53 protein gene names. Grammatical templates based on
 * this protien are identified and annotated.
 * 
 * @author vravuri
 * 
 */
public class P53GeneTagAnnotator extends JCasAnnotator_ImplBase {

  private ArrayList<String> bioTerms = new ArrayList<String>();

  private ArrayList<Boolean> namedEntityPresence;

  private ArrayList<Integer> startIndexes;

  private ArrayList<Integer> endIndexes;

  private ArrayList<String> wordList;

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    /* Input document is retrieved from the CAS */
    String inputContent = aJCas.getDocumentText();

    /*
     * Populates the array list with the set of identified biological words that will be used in
     * annotation
     */
    populateBioTerms();

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

        /* Lines that have p53 in them are picked and analysed */
        if (lineData.contains("p53") || lineData.contains("P53")) {

          namedEntityPresence = new ArrayList<Boolean>();
          startIndexes = new ArrayList<Integer>();
          endIndexes = new ArrayList<Integer>();
          wordList = new ArrayList<String>();

          /*
           * For every line, the required metadata like startindex of the word in the line, end
           * index of the word in the line, whether the name is a named entity or not and the list
           * of words in a list is retrieved. This metadata eases many calculations that will be
           * performed later on the line
           */
          tagUtils.populateLineMetadata(ner, namedEntityPresence, startIndexes, endIndexes,
                  wordList, lineData);

          /* Annotations, if necessary will be added based on the line analysis */
          addP53Annotations(aJCas, lineCode, lineData);
        }

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
  private void addP53Annotations(JCas aJCas, String lineCode, String lineData) {
    int annotationStart = 0;
    int annotationEnd = 0;
    boolean isFollowNeeded = true;
    GeneTaggingUtils tagUtils = new GeneTaggingUtils();

    /* Every line of the word is iterated and checked */
    for (int i = 0; i < wordList.size(); i++) {
      String word = wordList.get(i);

      /* Analysis begins when the word containing p53 is hit */
      if (word.contains("p53") || word.contains("P53")) {

        /*
         * Set the start index and end index to the word initially. They will change in case
         * additional words get added to the phrase
         */
        annotationStart = startIndexes.get(i);
        annotationEnd = endIndexes.get(i);

        /* If the word ends with a dot, that is removed from the annotation */
        if (word.charAt(word.length() - 1) == '.') {
          annotationEnd -= 1;
          isFollowNeeded = false;
          word = word.substring(0, word.length() - 1);
        }

        /*
         * If the word contains p53 and is hyphenated, words with numbers, words having brackets or
         * words from the set of biological words are picked
         */
        if (!word.endsWith("p53")) {
          StringTokenizer divideWord = new StringTokenizer(word, "-");
          divideWord.nextToken();

          if (divideWord.hasMoreTokens()) {
            String suffixWord = divideWord.nextToken();
            if (!(suffixWord.matches("^[a-zA-Z]*$") || bioTerms.contains(suffixWord))) {
              annotationEnd -= suffixWord.length() + 1;
              isFollowNeeded = false;
            }
          } else {
            int bracketIndex = word.indexOf('(', word.indexOf("p53") + 3);
            if (bracketIndex != -1) {
              annotationEnd = annotationStart + bracketIndex - 1;
              isFollowNeeded = false;
            }
          }
        }

        /*
         * Words prior to the word in question are added to the phrase if the word is alphanumeric
         * and a named entity or a biological word and named entity
         */
        for (int j = i - 1; j >= 0
                && ((namedEntityPresence.get(j) && !(wordList.get(j).matches("^[a-zA-Z]*$")) && wordList
                        .get(j).matches("^[a-zA-Z0-9]*$")) || bioTerms.contains(wordList.get(j))); j--) {
          annotationStart -= (1 + wordList.get(j).length());
        }

        /*
         * Some words would have additional words to be added to the phrase. These kinds of words
         * should be named entities and alphanumeric or should be biological terms
         */
        if (isFollowNeeded) {
          for (i++; i < wordList.size()
                  && ((namedEntityPresence.get(i) && !(wordList.get(i).matches("^[a-zA-Z]*$")) && wordList
                          .get(i).matches("^[a-zA-Z0-9]*$")) || bioTerms.contains(wordList.get(i))); i++) {
            annotationEnd += (1 + wordList.get(i).length());
          }
        }

        /* The phrase is then added to the list of annotations */
        tagUtils.addToAnnotator(aJCas, lineCode,
                tagUtils.getAdjustedIndex(lineData, annotationStart),
                tagUtils.getAdjustedIndex(lineData, annotationEnd),
                lineData.substring(annotationStart, annotationEnd + 1));
      }
    }
  }

  /**
   * The list of terms that are biology related and can appear as part of p53 gene
   */
  private void populateBioTerms() {
    bioTerms.add("mutant");
    bioTerms.add("mutants");
    bioTerms.add("protein");
    bioTerms.add("proteins");
    bioTerms.add("product");
    bioTerms.add("products");
    bioTerms.add("peptide");
    bioTerms.add("peptides");
    bioTerms.add("element");
    bioTerms.add("elements");
    bioTerms.add("wild-type");
    bioTerms.add("gene");
    bioTerms.add("genes");
    bioTerms.add("tumor");
    bioTerms.add("tumors");
    bioTerms.add("suppressor");
    bioTerms.add("suppressors");
    bioTerms.add("wild-type");
    bioTerms.add("wt");
    bioTerms.add("promoter");
    bioTerms.add("promoters");
    bioTerms.add("mutation");
    bioTerms.add("mutations");
    bioTerms.add("mRNA");
    bioTerms.add("tumor-suppressor");
    bioTerms.add("target");
    bioTerms.add("targets");
    bioTerms.add("human");
    bioTerms.add("humans");
    bioTerms.add("antibodies");
    bioTerms.add("antibody");
    bioTerms.add("dominant-negative");
    bioTerms.add("homolog");
    bioTerms.add("homologs");
    bioTerms.add("C-terminus");
    bioTerms.add("response");
    bioTerms.add("responses");
    bioTerms.add("DNA-binding");
    bioTerms.add("domain");
    bioTerms.add("transcripts");
    bioTerms.add("transcript");
  }
}
