/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airport.flights;

/**
 *
 * @author Ilya
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
//

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
//

import java.net.URISyntaxException;
//

import java.util.StringTokenizer;
//
/**
 * <code>ProperNameExtract</code> extracts and remains a proper name list 
 * from input file.
 */
public class ProperNameExtract {
    //
    public ProperNameExtract( ) {
        FStorage = getStorage();
    }
    //
    /**
     * 
     * @param __file_path Input file
     * @param __charSet Charset of input file
     * @throws IOException
     * @throws URISyntaxException 
     */
    public void extract(String __file_path, String __charSet) 
            throws IOException, URISyntaxException {
        //
        InputStream is = new FileInputStream(__file_path);
        BufferedReader FReadFile = new BufferedReader(
                new InputStreamReader(is, __charSet));
        String in;
        //
        while (null != (in = FReadFile.readLine())) {            
            StringTokenizer strToken = new StringTokenizer(in);
            //
            while(strToken.hasMoreTokens()){
                String checkStr=strToken.nextToken
                        (".,{}';:\"[]%\\—$()-+=*#@!~`&|/?><");
                String[] temp = checkStr.split("\\s");
                for (String cWord : temp) {
                    if (cWord.matches(VALIDATE_REXP)) {
                        FStorage.add(cWord);
                    }
                    //
                }
            }
            //
        }
        //
    }
    //
    
    public List<String > getProperNameList(){
        return(Collections.unmodifiableList(FStorage));
    }
    //
    
    private List<String > getStorage( ){
        return (FStorage == null ) ? new ArrayList<String>( ) : FStorage;
    }
    //
    private List<String > FStorage;
    private static final String VALIDATE_REXP = "^\\p{Lu}.+";    
    //
}
