

/* First created by JCasGen Sun Oct 14 10:11:30 EDT 2012 */
package typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Oct 16 23:10:17 EDT 2012
 * XML source: C:/Users/Puneet/juno_workspace/hw1-vravuri/src/main/resources/annotator/MasterAnnotatorDescriptor.xml
 * @generated */
public class GeneTag extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(GeneTag.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected GeneTag() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public GeneTag(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public GeneTag(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public GeneTag(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
  //*--------------*
  //* Feature: lineCode

  /** getter for lineCode - gets Unique identifier of each input line
   * @generated */
  public String getLineCode() {
    if (GeneTag_Type.featOkTst && ((GeneTag_Type)jcasType).casFeat_lineCode == null)
      jcasType.jcas.throwFeatMissing("lineCode", "typesystem.GeneTag");
    return jcasType.ll_cas.ll_getStringValue(addr, ((GeneTag_Type)jcasType).casFeatCode_lineCode);}
    
  /** setter for lineCode - sets Unique identifier of each input line 
   * @generated */
  public void setLineCode(String v) {
    if (GeneTag_Type.featOkTst && ((GeneTag_Type)jcasType).casFeat_lineCode == null)
      jcasType.jcas.throwFeatMissing("lineCode", "typesystem.GeneTag");
    jcasType.ll_cas.ll_setStringValue(addr, ((GeneTag_Type)jcasType).casFeatCode_lineCode, v);}    
   
    
  //*--------------*
  //* Feature: content

  /** getter for content - gets Content of the gene tag
   * @generated */
  public String getContent() {
    if (GeneTag_Type.featOkTst && ((GeneTag_Type)jcasType).casFeat_content == null)
      jcasType.jcas.throwFeatMissing("content", "typesystem.GeneTag");
    return jcasType.ll_cas.ll_getStringValue(addr, ((GeneTag_Type)jcasType).casFeatCode_content);}
    
  /** setter for content - sets Content of the gene tag 
   * @generated */
  public void setContent(String v) {
    if (GeneTag_Type.featOkTst && ((GeneTag_Type)jcasType).casFeat_content == null)
      jcasType.jcas.throwFeatMissing("content", "typesystem.GeneTag");
    jcasType.ll_cas.ll_setStringValue(addr, ((GeneTag_Type)jcasType).casFeatCode_content, v);}    
  }

    