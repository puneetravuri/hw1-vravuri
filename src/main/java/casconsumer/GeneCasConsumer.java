package casconsumer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;

import typesystem.GeneTag;
import typesystem.GeneTagComparator;

/**
 * CAS Consumer of the application. It opens the output file and writes the annotations in a fixed
 * format.
 * 
 * @author vravuri
 * 
 */
public class GeneCasConsumer extends CasConsumer_ImplBase {

  private Writer output;

  public static final String OUTPUT_FILE = "OutputFile";

  @Override
  public void initialize() throws ResourceInitializationException {
    File outputFile = new File((String) getConfigParameterValue(OUTPUT_FILE));

    /* Initializes the output writer */
    try {
      output = new BufferedWriter(new FileWriter(outputFile));
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  public void processCas(CAS aCAS) throws ResourceProcessException {

    JCas cas;
    try {
      cas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    Iterator<?> annotationIter = cas.getAnnotationIndex(GeneTag.type).iterator();

    List<GeneTag> list = new ArrayList<GeneTag>();

    while (annotationIter.hasNext()) {
      list.add((GeneTag) annotationIter.next());
    }

    /* Sorts the annotations so that they are printed in order of their unique codes */
    Collections.sort(list, new GeneTagComparator());

    /* These two strings are to avoid duplicate annotations to be printed */
    String prevLineCode = new String();
    String prevBeginId = new String();
    try {
      for (int i = 0; i < list.size(); i++) {
        GeneTag geneTag = list.get(i);

        /* Prints the annotations in the specified format */
        if (!(prevLineCode.equals(geneTag.getLineCode()) && prevBeginId.equals(geneTag.getBegin()
                + ""))) {
          output.write(geneTag.getLineCode() + "|" + geneTag.getBegin() + " " + geneTag.getEnd()
                  + "|" + geneTag.getContent() + "\n");
        }

        prevLineCode = geneTag.getLineCode();
        prevBeginId = geneTag.getBegin() + "";
      }

      output.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
