package typesystem;

import java.util.Comparator;

/**
 * Comparator for the GeneTag objects. The criteria used to compare is the unique id of the line in
 * which gene tag is being annotated
 * 
 * @author vravuri
 * 
 */
public class GeneTagComparator implements Comparator<GeneTag> {

  @Override
  public int compare(GeneTag arg0, GeneTag arg1) {

    /*
     * The unique ID is of the form P01000378T0000. The part "01000378" is the code part which takes
     * higher preference during comparison and the part "0000" is the sub-code part that takes lower
     * preference during comparison
     */
    String code1 = arg0.getLineCode().substring(1, 9);
    String code2 = arg1.getLineCode().substring(1, 9);

    String subcode1 = arg0.getLineCode().substring(10, 14);
    String subcode2 = arg1.getLineCode().substring(10, 14);

    if (Integer.parseInt(code1) > Integer.parseInt(code2)) {
      return 1;
    } else if (Integer.parseInt(code1) < Integer.parseInt(code2)) {
      return -1;
    } else { /* If codes are same, subcodes are compared */
      if (Integer.parseInt(subcode1) > Integer.parseInt(subcode2)) {
        return 1;
      } else if (Integer.parseInt(subcode1) < Integer.parseInt(subcode2)) {
        return -1;
      } else {
        return 0;
      }
    }
  }

}
