
/* First created by JCasGen Sun Oct 14 10:11:30 EDT 2012 */
package typesystem;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue Oct 16 23:10:17 EDT 2012
 * @generated */
public class GeneTag_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (GeneTag_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = GeneTag_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new GeneTag(addr, GeneTag_Type.this);
  			   GeneTag_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new GeneTag(addr, GeneTag_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = GeneTag.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("typesystem.GeneTag");



  /** @generated */
  final Feature casFeat_lineCode;
  /** @generated */
  final int     casFeatCode_lineCode;
  /** @generated */ 
  public String getLineCode(int addr) {
        if (featOkTst && casFeat_lineCode == null)
      jcas.throwFeatMissing("lineCode", "typesystem.GeneTag");
    return ll_cas.ll_getStringValue(addr, casFeatCode_lineCode);
  }
  /** @generated */    
  public void setLineCode(int addr, String v) {
        if (featOkTst && casFeat_lineCode == null)
      jcas.throwFeatMissing("lineCode", "typesystem.GeneTag");
    ll_cas.ll_setStringValue(addr, casFeatCode_lineCode, v);}
    
  
 
  /** @generated */
  final Feature casFeat_content;
  /** @generated */
  final int     casFeatCode_content;
  /** @generated */ 
  public String getContent(int addr) {
        if (featOkTst && casFeat_content == null)
      jcas.throwFeatMissing("content", "typesystem.GeneTag");
    return ll_cas.ll_getStringValue(addr, casFeatCode_content);
  }
  /** @generated */    
  public void setContent(int addr, String v) {
        if (featOkTst && casFeat_content == null)
      jcas.throwFeatMissing("content", "typesystem.GeneTag");
    ll_cas.ll_setStringValue(addr, casFeatCode_content, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public GeneTag_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_lineCode = jcas.getRequiredFeatureDE(casType, "lineCode", "uima.cas.String", featOkTst);
    casFeatCode_lineCode  = (null == casFeat_lineCode) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lineCode).getCode();

 
    casFeat_content = jcas.getRequiredFeatureDE(casType, "content", "uima.cas.String", featOkTst);
    casFeatCode_content  = (null == casFeat_content) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_content).getCode();

  }
}



    