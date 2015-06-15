BEGIN {
  print "||License|| || ||"
  print "||Artifact Name||Artifact ID||Artifact URL||"
}

/^List/ {
    getline
}

/^[a-zA-Z]/ {
    print "||"$0"|| || || ||"
}

/^[  ]\\*[ ]/ {
    # Count the number of open parens in the string
    parencount = gsub("\\(", "(")
    str = $0
    offset = 5

    if (parencount > 1)
    {
      openparen = index(str, "(")
      lastparenIndex = openparen
      #print "First paren index: "lastparenIndex
      for (i=0; i<parencount; i++) 
      {
        str = substr(str, openparen+1, length($0))
        #print i+1" String: "str
        openparen = index(str, "(")
        #print i+1" Relative paren index: "openparen
        lastparenIndex = openparen + lastparenIndex
        #print i+1" paren overall string index: "lastparenIndex
      }

      openparen = lastparenIndex
      #closeparen = index(str, ")") + openparen      
      closeparen = length()
      str = $0
    }
    else 
    {
      openparen = index(str, "(")
      closeparen = index(str, ")")
      #closeparen = length()
    }

    #print str
    #print openparen" "closeparen

    name = substr(str, offset-1, openparen-offset)
    split(substr(str, openparen+1, closeparen-openparen-1), artifactAndUrl, " - ")

    if(substr(artifactAndUrl[2],1,4) == "http") 
    { 
        print "|"name"|"artifactAndUrl[1]"|["artifactAndUrl[2]"]|" 
    }
    else 
    {
        print "|"name"|"artifactAndUrl[1]"|"artifactAndUrl[2]"|"
    }
    
}
