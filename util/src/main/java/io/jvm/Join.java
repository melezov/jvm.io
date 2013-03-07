package io.jvm;

public class Join {

  public static final String escaping(final String uris[], final char separator) {

    if (uris == null) return null;
    if (uris.length==0) throw new IllegalArgumentException("URI list cannot be empty");;

    int charCnt = uris.length-1; //number of separators between uris

    //calc the length of output
    for (int i=0; i<uris.length; i++) {
      if (uris[i]==null) {
        throw new NullPointerException("URI at index "+ i +" cannot be a null value");
      }else {
        final int len = uris[i].length();
        charCnt+=len;
        for (int j=0; j<len; j++) {
          if (uris[i].charAt(j) == separator) charCnt++;
        }
      }
    }

    char[] allChars = new char[charCnt];
    int charPos=0;

    for (int i=0; i<uris.length; i++) {
        for (int j=0; j<uris[i].length(); j++) {
          final char c = uris[i].charAt(j);
          if (c==separator) {
            //separator found
            allChars[charPos++]=c;
          }
          allChars[charPos++]=c;
        }
        if (i<uris.length-1) allChars[charPos++]=separator;
    }

    return String.valueOf(allChars);
  }

  public static final String buildSimpleUriList(final String uris[]) {

    if (uris == null) return null;
    if (uris.length==0) throw new IllegalArgumentException("URI list cannot be empty");;

    int charCnt = 2*uris.length + uris.length - 1; //number of '' and ,

    //calc the length of output
    for (int i=0; i<uris.length; i++) {
      if (uris[i]==null) {
        throw new NullPointerException("URI at index "+ i +" cannot be a null value");
      }else {
        final int len = uris[i].length();
        charCnt+=len;
        for (int j=0; j<len; j++) {
          char c =uris[i].charAt(j);
          if (c == '/') {
            if (j<len-1) {
              charCnt--;
              j++; // skip next char if not last in uri
            } else throw new IllegalArgumentException("URI at index "+ i +" must not end with '/'");
          } else if (c == '\'') charCnt++;
        }
      }
    }

    char[] allChars = new char[charCnt];
    int charPos=0;

    for (int i=0; i<uris.length; i++) {
      allChars[charPos++]='\''; //open with '
      final int len = uris[i].length();
      for (int j=0; j<len; j++) {
        final char c = uris[i].charAt(j);
        allChars[charPos++]=c;
        if (c == '/') {
          j++; // contract // to /
        } else if (c == '\'') allChars[charPos++]=c; // duplicate '
      }
      allChars[charPos++]='\''; //close with '
      if (i < uris.length-1) allChars[charPos++]=','; // add , if needed
    }

    return String.valueOf(allChars);
  }


  public static final String buildCompositeUriList(final String uris[]) {

    if (uris == null) return null;
    if (uris.length==0) throw new IllegalArgumentException("URI list cannot be empty");;

    // counting and validation
    int charCnt = 4*uris.length + uris.length - 1; // number of ('') and ,

    for (int i=0; i<uris.length; i++) {
      final String uri = uris[i];
      if (uri==null) {
        throw new NullPointerException("URI at index "+ i +" cannot be a null value");
      }else {
        final int len = uris[i].length();
        charCnt+=len;
        for (int j=0; j<len; j++) {
          final char c = uri.charAt(j);
          if (c == '/') {
            if (j<len-1) { // if not last character in string
              if (uri.charAt(j+1) == '/') {
                // contract // to /
                charCnt-=3; // OVO MI SMRDI! trebalo bi biti -1 a ne -3 jer micemo samo taj jedan viska
              } else {
                // add ',' instead of /
                charCnt+=2;
              }
            } else {
              // add ',' if / at end
              charCnt+=2;
            }
          } else if (c == '\'') {
            // expant ' to ''
            charCnt++;
          }
        }
      }
    }

    //build string
    char[] allChars = new char[charCnt];
    int charPos=0;

    for (int i=0; i<uris.length; i++) {

      // open with ('
      allChars[charPos++]='(';
      allChars[charPos++]='\'';
      final String uri = uris[i];
      final int len = uri.length();

      for (int j=0; j<len; j++) {
        final char c = uri.charAt(j);
        if (c == '/') {
          if (j<len-1) { // if not last character in string
            if (uri.charAt(j+1) == '/') {
              // contract // to /
              allChars[charPos++]='/';
              j++;
            } else {
              // add ',' instead of /
              allChars[charPos++]='\'';
              allChars[charPos++]=',';
              allChars[charPos++]='\'';
            }
          } else {
            // add ',' if / at end
            allChars[charPos++]='\'';
            allChars[charPos++]=',';
            allChars[charPos++]='\'';
          }
        } else if (c == '\'') {
          // expand ' to ''
          allChars[charPos++]=c;
          allChars[charPos++]=c;
        } else {
          // use character
          allChars[charPos++]=c;
        }
      }
      //close with ')
      allChars[charPos++]='\'';
      allChars[charPos++]=')';
      if (i < uris.length-1) allChars[charPos++]=','; // add , if needed
    }

    return String.valueOf(allChars);
  }
}
