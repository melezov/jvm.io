package io.jvm;

public class Join {
  public static final String escaping(final String URI[], final char separator) {
    String ret= "";

    if (URI == null) return null;
    if (URI.length==0) return "";

    int charCnt = 0;

    for (int i=0; i<URI.length; i++) {
      if (URI[i]==null) {
        throw new IllegalArgumentException("URI["+ i +"] cannot contain null string!");
      }else {
        for (int j=0; j<URI[i].length(); j++) {
          if (URI[i].charAt(j)==separator) {
            //separator found
            charCnt+=2;
          }else {
            charCnt++;
          }

        }
      }
    }

    char[] allChars = new char[charCnt];



    return ret;
  }

  public static final String escapingLegacy(final String uris[], final char separator) {
    String ret= "";

    if (uris == null) return null;
    if (uris.length==0) return "";

    final String searchStr= String.valueOf(separator);
    final String replaceStr = searchStr+separator;

    boolean existsBefore=false;

    for (int i=0; i<uris.length; i++) {
      if (uris[i]!=null) {
        ret+= (existsBefore ? separator : "") + uris[i].replace(searchStr, replaceStr);
        existsBefore=true;
      }
    }

    return ret;
  }
}
