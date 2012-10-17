package flow;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.cas.CAS;
import org.apache.uima.flow.CasFlowController_ImplBase;
import org.apache.uima.flow.CasFlow_ImplBase;
import org.apache.uima.flow.FinalStep;
import org.apache.uima.flow.Flow;
import org.apache.uima.flow.SimpleStep;
import org.apache.uima.flow.Step;

/**
 * Flow controller of the application.
 * 
 * @author vravuri
 * 
 */
public class GeneTagFlowController extends CasFlowController_ImplBase {

  @Override
  public Flow computeFlow(CAS aCAS) throws AnalysisEngineProcessException {
    GeneTagFlow geneTagFlow = new GeneTagFlow();
    return geneTagFlow;
  }

  class GeneTagFlow extends CasFlow_ImplBase {
    private Set<String> mAlreadyCalled = new HashSet<String>();

    @Override
    public Step next() throws AnalysisEngineProcessException {

      Iterator<Map.Entry<String, AnalysisEngineMetaData>> aeIter = getContext()
              .getAnalysisEngineMetaDataMap().entrySet().iterator();

      /* Gets the analysis engine metadata, checks if already called and calls them accordingly */
      while (aeIter.hasNext()) {
        Map.Entry<String, AnalysisEngineMetaData> entry = (Map.Entry<String, AnalysisEngineMetaData>) aeIter
                .next();
        String aeKey = (String) entry.getKey();
        if (!mAlreadyCalled.contains(aeKey)) {
          mAlreadyCalled.add(aeKey);
          return new SimpleStep(aeKey);
        }
      }
      return new FinalStep();
    }
  }
}
