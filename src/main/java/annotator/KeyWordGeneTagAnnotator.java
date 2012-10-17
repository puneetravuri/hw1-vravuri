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
 * This annotator performs annotation based on certain keywords. These keywords are normally part of
 * gene names. Key word occurrence is checked either in the entire word or as a part of word suffix.
 * A separate list of suffixes and key words are maintained.
 * 
 * @author vravuri
 * 
 */
public class KeyWordGeneTagAnnotator extends JCasAnnotator_ImplBase {

  private ArrayList<Boolean> namedEntityPresence;

  private ArrayList<Integer> startIndexes;

  private ArrayList<Integer> endIndexes;

  private ArrayList<String> wordList;

  private ArrayList<String> exceptionWords = new ArrayList<String>();

  /* The key word appears anywhere in a word */
  private final String FULL_WORD = "full_word";

  /* The key word appears as a suffix in a word */
  private final String SUFFIX_WORD = "suffix_word";

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

        /* Each key word is analyzed per line of input document */
        for (String baseWord : getKeyWords()) {
          if (lineData.contains(baseWord)) {

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
            addWordAnnotations(aJCas, lineCode, lineData, baseWord, FULL_WORD);
          }
        }

        /* Each suffix word is analyzed per line of input */
        for (String baseWord : getSuffixWords()) {
          if (lineData.contains(baseWord)) {
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
            addWordAnnotations(aJCas, lineCode, lineData, baseWord, SUFFIX_WORD);
          }
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
   * @param baseWord
   *          The keyword that is being analyzed
   * @param wordPart
   *          Either full word or a suffix
   */
  private void addWordAnnotations(JCas aJCas, String lineCode, String lineData, String baseWord,
          String wordPart) {
    int annotationStart = 0;
    int annotationEnd = 0;
    GeneTaggingUtils tagUtils = new GeneTaggingUtils();

    /* Every line of the word is iterated and checked */
    for (int i = 0; i < wordList.size(); i++) {
      String word = wordList.get(i);

      /* Analysis of full words */
      if (wordPart.equals(FULL_WORD)) {
        if (word.contains(baseWord)) {
          /*
           * Set the start index and end index to the word initially. They will change in case
           * additional words get added to the phrase
           */
          annotationStart = startIndexes.get(i);
          annotationEnd = endIndexes.get(i);

          /*
           * Additional words are added to the phrase if the word is alpha-numeric or if it
           * hyphenated or if it is a named entity
           */
          for (int j = i - 1; j >= 0
                  && ((!(wordList.get(j).matches("^[a-zA-Z]*$")) && wordList.get(j).matches(
                          "^[a-zA-Z0-9]*$"))
                          || wordList.get(j).contains("-")
                          || (wordList.get(j).startsWith("(") && wordList.get(j).endsWith(")")) || namedEntityPresence
                            .get(j)); j--) {
            annotationStart -= (1 + wordList.get(j).length());
          }

          /*
           * The phrase is added as an annotation only if it has more than a single word and is not
           * part of the exception list
           */
          if (!(annotationStart == startIndexes.get(i) && (word.equalsIgnoreCase(baseWord) || word
                  .equalsIgnoreCase(baseWord + "s")))) {
            String annotatedWord = lineData.substring(annotationStart, annotationEnd + 1);
            if (!exceptionWords.contains(annotatedWord)) {
              tagUtils.addToAnnotator(aJCas, lineCode,
                      tagUtils.getAdjustedIndex(lineData, annotationStart),
                      tagUtils.getAdjustedIndex(lineData, annotationEnd),
                      lineData.substring(annotationStart, annotationEnd + 1));
            }
          }
        }
      } else { /* Analysis of suffixes */
        if (word.endsWith(baseWord)) {
          /*
           * Set the start index and end index to the word initially. They will change in case
           * additional words get added to the phrase
           */
          annotationStart = startIndexes.get(i);
          annotationEnd = endIndexes.get(i);

          /*
           * Additional words are added to the phrase if the word is alpha-numeric or if it
           * hyphenated or if it is a named entity
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
           * The phrase is added as an annotation only if it has more than a single word and is not
           * part of the exception list
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
  }

  /**
   * Populates the exception words that will not be annotated since they are not genes
   * and are just dictionary and common words
   */
  private void fillExceptionWords() {
    exceptionWords.add("serum response element");
    exceptionWords.add("AC element");
    exceptionWords.add("B-cell-specific enhancer element");
    exceptionWords.add("dioxin responsive transcriptional enhancer");
    exceptionWords.add("homologous");
    exceptionWords.add("retransfusion");
    exceptionWords.add("homology");
    exceptionWords.add("infusion");
    exceptionWords.add("locoregional");
    exceptionWords.add("complexes");
    exceptionWords.add("subunits");
    exceptionWords.add("elements");
    exceptionWords.add("regional");
    exceptionWords.add("complexity");
    exceptionWords.add("perfusion");
    exceptionWords.add("homologies");
    exceptionWords.add("messages");
    exceptionWords.add("isoforms");
    exceptionWords.add("enhancers");
    exceptionWords.add("promoters");
    exceptionWords.add("introns");
    exceptionWords.add("operons");
    exceptionWords.add("binding sites");
    exceptionWords.add("oligomers");
    exceptionWords.add("genes");
    exceptionWords.add("genetic");
    exceptionWords.add("genetics");
    exceptionWords.add("genetically");
    exceptionWords.add("genetical");
    exceptionWords.add("general");
    exceptionWords.add("generous");
    exceptionWords.add("generally");
    exceptionWords.add("generate");
    exceptionWords.add("generalize");
    exceptionWords.add("generously");
    exceptionWords.add("elemental");
    exceptionWords.add("nitrogen");
    exceptionWords.add("hydrogen");
    exceptionWords.add("increase");
    exceptionWords.add("decrease");
    exceptionWords.add("decreases");
    exceptionWords.add("increases");
    exceptionWords.add("phase");
    exceptionWords.add("phases");
    exceptionWords.add("case");
    exceptionWords.add("cases");
    exceptionWords.add("base");
    exceptionWords.add("bases");
    exceptionWords.add("gases");
    exceptionWords.add("disease");
    exceptionWords.add("diseases");
  }

  /**
   * Returns the list of keywords based on which analysis is performed
   * 
   * @return List of key words
   */
  private String[] getKeyWords() {
    return new String[] { "receptor", "domain", "motif", "immunoglobulins", "fusion",
        "light chain", "heavy chain", "monomer", "codon", "region", "exon", "orf", "cdna",
        "reporter", "gene product", "antibody", "complex", "mrna", "oligomer", "chemokine",
        "subunit", "peptide", "message", "transactivator", "homolog", "binding site", "enhancer",
        "element", "allele", "isoform", "intron", "promoter", "operon", "alpha", "beta", "ltr",
        "ogen", "gene", "Vasopressin", "prolactin", "FSH" };
  }

  /**
   * Returns the list of suffix words on which analysis is performed
   * 
   * @return List of suffix words
   */
  private String[] getSuffixWords() {
    return new String[] { "ase", "ases" };
  }
}
