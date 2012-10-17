package reader;

import java.io.File;
import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.FileUtils;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

/**
 * Collection Reader for the application. Reads the input file and uploads the document to the CAS
 * object
 * 
 * @author vravuri
 * 
 */
public class GeneCollectionReader extends CollectionReader_ImplBase {

  public static final String INPUT_FILE = "InputFile";

  private int isDocumentRead;

  private File inputDocument;

  @Override
  public void initialize() throws ResourceInitializationException {
    inputDocument = new File((String) getConfigParameterValue(INPUT_FILE));

    /* If the file does not exist, appropriate error message is thrown */
    if (!inputDocument.exists()) {
      throw new ResourceInitializationException("Input document not found", new Object[] {
          INPUT_FILE, this.getMetaData().getName(), inputDocument.getName() });
    }
    isDocumentRead = 0;
  }

  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    JCas cas;

    try {
      cas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }

    /* Takes the content of the input file and loads it into the CAS object */
    String docContent = FileUtils.file2String(inputDocument);
    cas.setDocumentText(docContent);
    isDocumentRead = 1;

  }

  /**
   * If the document has already been read, it returns false, otherwise it returns true.
   */
  @Override
  public boolean hasNext() throws IOException, CollectionException {
    return isDocumentRead == 0;
  }

  /**
   * Returns the progress of the reader.
   */
  @Override
  public Progress[] getProgress() {
    return new Progress[] { new ProgressImpl(isDocumentRead, 1, Progress.ENTITIES) };
  }

  @Override
  public void close() throws IOException {

  }

}
