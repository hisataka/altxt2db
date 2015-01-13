package jp.co.altxt2db.util;

import org.seasar.extension.unit.S2TestCase;

public class CsvParserTest extends S2TestCase { 
    
    @Override 
    protected void setUp() throws Exception { 
        super.setUp(); 
    }
    
    public void testSplit() {
        // "aaa","bbb","ccc"　→　{"aaa","bbb","ccc"}
        String test1 = "\"aaa\",\"bbb\",\"ccc\"";
        String[] result1 = CsvParser.split(test1);
        String[] assert1 = {"aaa", "bbb", "ccc"};
        for (int i = 0; i < assert1.length; i ++) {
            assertEquals(assert1[i], result1[i]);
        }
        
        // "aaa",bbb,"ccc"　→　{"aaa","bbb","ccc"}
        String test2 = "\"aaa\",bbb,\"ccc\"";
        String[] result2 = CsvParser.split(test2);
        String[] assert2 = {"aaa", "bbb", "ccc"};
        for (int i = 0; i < assert2.length; i ++) {
            assertEquals(assert2[i], result2[i]);
        }
        
        // "a,aa",b,bb,"c,cc"　→　{"a,aa","b","bb","c,cc"}
        String test3 = "\"a,aa\",b,bb,\"c,cc\"";
        String[] result3 = CsvParser.split(test3);
        String[] assert3 = {"a,aa", "b", "bb", "c,cc"};
        for (int i = 0; i < assert3.length; i ++) {
            assertEquals(assert3[i], result3[i]);
        }

        // "a,aa",b,bb,"c"",""cc"　→　{"a,aa","b","bb","c\",\"cc"}
        String test4 = "\"a,aa\",b,bb,\"c\"\",\"\"cc\"";
        String[] result4 = CsvParser.split(test4);
        String[] assert4 = {"a,aa", "b", "bb", "c\",\"cc"};
        for (int i = 0; i < assert4.length; i ++) {
            assertEquals(assert4[i], result4[i]);
        }
    }
    
}