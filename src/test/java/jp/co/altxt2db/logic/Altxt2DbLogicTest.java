package jp.co.altxt2db.logic;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.co.altxt2db.dto.AltxtMetaDto;
import jp.co.altxt2db.logic.Altxt2DbLogic;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import org.seasar.extension.unit.S2TestCase;

public class Altxt2DbLogicTest extends S2TestCase { 
    private Altxt2DbLogic logic;
    
    @Override 
    protected void setUp() throws Exception { 
        super.setUp(); 
    } 
    
    /**
     * マルチバイトのアットマークをシングルバイトのアットマークに変換する
     */
    public void testMAt2sAt() {
        // 空文字は空文字
        assertEquals("", logic.mAt2sAt(""));

        // ＠は@
        assertEquals("@", logic.mAt2sAt("＠"));
        
        // @は@
        assertEquals("@", logic.mAt2sAt("＠"));
        
        // nullはnull
        assertNull(logic.mAt2sAt(null));
    }
    
    /**
     * 文字列がアットマークであるかを判定する
     */
    public void testIsAt() {
        // 空文字はfalse
        assertEquals(false, logic.isAt(""));
        
        // nullはfalse
        assertEquals(false, logic.isAt(null));
        
        // @はtrue
        assertEquals(true, logic.isAt("@"));
        
        // ＠はtrue
        assertEquals(true, logic.isAt("＠"));
    }
    
    /**
     * 指定された長さのリストを@で埋めて生成する
     */
    public void testCreateAtList() {
        // 0で生成すると長さ0のリストができる
        List<String> t1 = logic.createAtList(0);
        assertEquals(0, t1.size());
        
        // 1で生成すると長さ1のリストが出来る、値は@
        List<String> t2 = logic.createAtList(1);
        assertEquals(1, t2.size());
        assertEquals("@", t2.get(0));
        
        // 10で生成すると長さ10のリストができる、値は全て@
        List<String> t3 = logic.createAtList(10);
        assertEquals(10, t3.size());
        for(String t3s: t3) {
            assertEquals("@", t3s);
        }
    }
    
    /**
     * 要素が全て空値であるかを判定する
     */
    public void testIsAllEmpty() {
        // nullの場合はfalse
        assertEquals(false, logic.isAllEmpty(null));
        
        // 要素0の場合はtrue
        List<String> p1 = new ArrayList<String>();
        assertEquals(true, logic.isAllEmpty(p1));
        
        // 要素1で値がnullはfalse
        List<String> p2 = new ArrayList<String>();
        p2.add(null);
        assertEquals(false, logic.isAllEmpty(p2));
        
        // 要素1で値が空ならtrue
        List<String> p3 = new ArrayList<String>();
        p3.add("");
        assertEquals(true, logic.isAllEmpty(p3));
        
        // 要素1で値がhogeならfalse
        List<String> p4 = new ArrayList<String>();
        p4.add("hoge");
        assertEquals(false, logic.isAllEmpty(p4));
        
        // 要素3で次の値リストなら[null,'','']false
        List<String> p5 = new ArrayList<String>();
        p5.add(null);
        p5.add("");
        p5.add("");
        assertEquals(false, logic.isAllEmpty(p5));
        
        // 要素3で次の値リストなら['','','']true
        List<String> p6 = new ArrayList<String>();
        p6.add("");
        p6.add("");
        p6.add("");
        assertEquals(true, logic.isAllEmpty(p6));
    }
    
    /**
     * 空値の要素を@に変換する
     */
    public void testEmpty2At() {
        // nullはnull
        assertEquals(logic.empty2At(null), null);
        
        // hogeはhoge
        // @は@
        // ＠は＠
        // ""は@
        List<String> p = new ArrayList<String>();
        p.add("hoge");
        p.add("@");
        p.add("＠");
        p.add("");
        
        List<String> r = logic.empty2At(p);
        assertEquals("hoge", r.get(0));
        assertEquals("@", r.get(1));
        assertEquals("＠", r.get(2));
        assertEquals("@", r.get(3));
        
    }
    
    /**
     * 
     * 子テーブル削除用SQL生成
     * 
     * update 子 set 修正区分 = 'C' 
     *     where exists ( select * 
     *                    from 親一時テーブル
     *                    where 修正区分 = 'C'
     *                          キー1 = 子.キー1
     *                          AND キー2 = 子.キー2
     *                              ・
     *                              ・
     *                              ・
     *           );
     * 
     */
    public void testMakeDeleteChildSql() {
        AltxtMetaDto altxtMetaDto = null;
        try {
            altxtMetaDto = JSON.decode(new FileReader("C:\\tisdev\\workspace\\altxt2db\\procfiles\\testParentdef.txt"), AltxtMetaDto.class);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        List<String> deleteChildSqls = logic.makeDeleteChildSql(altxtMetaDto);
        
        // デリート文は3つ
        assertEquals(deleteChildSqls.size(), 3);
        
        // 一つ目のデリート
        String sql1 = "update child set MMKB = 'DEL' where exists ( select * from _____altxt2db_parent where MMKB = 'DEL' and K_RID = child.K_RID1 and K_KCD = child.K_KCD1 and K_BK = child.K_BK1);";
        assertEquals(sql1, deleteChildSqls.get(0));
        
        // 二つ目のデリート
        String sql2 = "update child2 set MMKB = 'DEL' where exists ( select * from _____altxt2db_parent where MMKB = 'DEL' and K_RID = child2.K_RID2 and K_KCD = child2.K_KCD2 and K_BK = child2.K_BK2);";
        assertEquals(sql2, deleteChildSqls.get(1));

        // 三つ目のデリート
        String sql3 = "update child3 set MMKB = 'DEL' where exists ( select * from _____altxt2db_parent where MMKB = 'DEL' and K_RID = child3.K_RID3 and K_KCD = child3.K_KCD3 and K_BK = child3.K_BK3);";
        assertEquals(sql3, deleteChildSqls.get(2));
    }
    
    /**
     * 
     *  本テーブルマージ用SQL生成
     *  
     *  merge into 本テーブル as A
     *     using (select isnull(case 一時テーブル.カラム1 when '@' then '' when '' then 本テーブル.カラム1 else 一時テーブル.カラム1 end, '') as カラム1
     *                   , isnull(case 一時テーブル.カラム2 when '@' then '' when '' then 本テーブル.カラム2 else 一時テーブル.カラム2 end, '') as カラム2
     *                      ・
     *                      ・
     *                      ・
     *            from 一時テーブル
     *                left outer join 本テーブル
     *                    on 一時テーブル.キー1 = 本テーブル.キー1
     *                        and 一時テーブル.キー2 = 本テーブル.キー2
     *                            ・
     *                            ・
     *                            ・
     *     ) as B
     *     on (A.キー1 = B.キー1
     *         and A.キー2 = B.キー2
     *             ・
     *             ・
     *             ・
     *     )
     *     when matched then update set
     *         カラム1 = B.カラム1
     *         , カラム2 = B.カラム2
     *             ・
     *             ・
     *             ・
     *     when not matched then insert (
     *         カラム1
     *         , カラム2
     *             ・
     *             ・
     *             ・
     *     ) values (
     *         B.カラム1
     *         , B.カラム2
     *             ・
     *             ・
     *             ・
     *     );
     *  
     */
    public void testMakeMerge2MainSql() {
        AltxtMetaDto altxtMetaDto = null;
        try {
            altxtMetaDto = JSON.decode(new FileReader("C:\\tisdev\\workspace\\altxt2db\\procfiles\\testParentdef.txt"), AltxtMetaDto.class);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        String merge2MainSql = logic.makeMerge2MainSql(altxtMetaDto);
        
        String sql = "merge into parent as A using (select isnull(case _____altxt2db_parent.LKB when '@' then '' when '' then parent.LKB else _____altxt2db_parent.LKB end, '') as LKB" 
                + " , isnull(case _____altxt2db_parent.K_RID when '@' then '' when '' then parent.K_RID else _____altxt2db_parent.K_RID end, '') as K_RID"
                + " , isnull(case _____altxt2db_parent.K_KCD when '@' then '' when '' then parent.K_KCD else _____altxt2db_parent.K_KCD end, '') as K_KCD"
                + " , isnull(case _____altxt2db_parent.K_BK when '@' then '' when '' then parent.K_BK else _____altxt2db_parent.K_BK end, '') as K_BK"
                + " , isnull(case _____altxt2db_parent.YMD when '@' then '' when '' then parent.YMD else _____altxt2db_parent.YMD end, '') as YMD"
                + " , isnull(case _____altxt2db_parent.MODIFY_KB when '@' then '' when '' then parent.MODIFY_KB else _____altxt2db_parent.MODIFY_KB end, '') as MODIFY_KB"
                + " , isnull(case _____altxt2db_parent.ND1 when '@' then '' when '' then parent.ND1 else _____altxt2db_parent.ND1 end, '') as ND1"
                + " , isnull(case _____altxt2db_parent.ND2 when '@' then '' when '' then parent.ND2 else _____altxt2db_parent.ND2 end, '') as ND2"
                + " , isnull(case _____altxt2db_parent.AD_CD when '@' then '' when '' then parent.AD_CD else _____altxt2db_parent.AD_CD end, '') as AD_CD"
                + " , isnull(case _____altxt2db_parent.AD_NM when '@' then '' when '' then parent.AD_NM else _____altxt2db_parent.AD_NM end, '') as AD_NM"
                + " from _____altxt2db_parent left outer join parent on _____altxt2db_parent.K_RID = parent.K_RID and _____altxt2db_parent.K_KCD = parent.K_KCD and _____altxt2db_parent.K_BK = parent.K_BK ) as B"
                + " on (A.K_RID = B.K_RID and A.K_KCD = B.K_KCD and A.K_BK = B.K_BK )"
                + " when matched then update set "
                + "LKB = B.LKB , K_RID = B.K_RID , K_KCD = B.K_KCD , K_BK = B.K_BK , YMD = B.YMD , MODIFY_KB = B.MODIFY_KB , ND1 = B.ND1 , ND2 = B.ND2 , AD_CD = B.AD_CD , AD_NM = B.AD_NM"
                + " when not matched then insert ("
                + "LKB , K_RID , K_KCD , K_BK , YMD , MODIFY_KB , ND1 , ND2 , AD_CD , AD_NM ) values ( "
                + "B.LKB , B.K_RID , B.K_KCD , B.K_BK , B.YMD , B.MODIFY_KB , B.ND1 , B.ND2 , B.AD_CD , B.AD_NM);";
        
        assertEquals(sql, merge2MainSql);

    }
    
    /**
     * 
     *  一時テーブルマージ用SQL生成
     *  
     *  merge into 一時テーブル as A
     *     using (select isnull(case txt.カラム1 when '' then work.カラム1 else txt.カラム1 end, '') as カラム1
     *                   , isnull(case txt.カラム2 when '' then work.カラム2 else txt.カラム2 end, '') as カラム2
     *                      ・
     *                      ・
     *                      ・
     *            from (select ? as カラム1
     *                         , ? as カラム2
     *                             ・
     *                             ・
     *                             ・
     *                 ) as txt
     *                     left outer join 一時テーブル as work
     *                         on txt.キー1 = work.キー1
     *                         and txt.キー2 = work.キー2
     *                             ・
     *                             ・
     *                             ・
     *     ) as B
     *     on (A.キー1 = B.キー1
     *         and A.キー2 = B.キー2
     *             ・
     *             ・
     *             ・
     *     )
     *     when matched then update set
     *         カラム1 = B.カラム1
     *         , カラム2 = B.カラム2
     *             ・
     *             ・
     *             ・
     *     when not matched then insert (
     *         カラム1
     *         , カラム2
     *             ・
     *             ・
     *             ・
     *     ) values (
     *         B.カラム1
     *         , B.カラム2
     *             ・
     *             ・
     *             ・
     *     );
     *     
     */
    public void testMakeMerge2WorkSql() {
        AltxtMetaDto altxtMetaDto = null;
        try {
            altxtMetaDto = JSON.decode(new FileReader("C:\\tisdev\\workspace\\altxt2db\\procfiles\\testParentdef.txt"), AltxtMetaDto.class);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        String merge2WorkSql = logic.makeMerge2WorkSql(altxtMetaDto);
        
        String sql = "merge into _____altxt2db_parent as A using (select isnull(case txt.LKB when '' then work.LKB else txt.LKB end, '') as LKB" 
                + " , isnull(case txt.K_RID when '' then work.K_RID else txt.K_RID end, '') as K_RID"
                + " , isnull(case txt.K_KCD when '' then work.K_KCD else txt.K_KCD end, '') as K_KCD"
                + " , isnull(case txt.K_BK when '' then work.K_BK else txt.K_BK end, '') as K_BK"
                + " , isnull(case txt.YMD when '' then work.YMD else txt.YMD end, '') as YMD"
                + " , isnull(case txt.MODIFY_KB when '' then work.MODIFY_KB else txt.MODIFY_KB end, '') as MODIFY_KB"
                + " , isnull(case txt.ND1 when '' then work.ND1 else txt.ND1 end, '') as ND1"
                + " , isnull(case txt.ND2 when '' then work.ND2 else txt.ND2 end, '') as ND2"
                + " , isnull(case txt.AD_CD when '' then work.AD_CD else txt.AD_CD end, '') as AD_CD"
                + " , isnull(case txt.AD_NM when '' then work.AD_NM else txt.AD_NM end, '') as AD_NM"
                + " from (select ? as LKB , ? as K_RID , ? as K_KCD , ? as K_BK , ? as YMD , ? as MODIFY_KB , ? as ND1 , ? as ND2 , ? as AD_CD , ? as AD_NM ) as txt"
                + " left outer join _____altxt2db_parent as work on txt.K_RID = work.K_RID and txt.K_KCD = work.K_KCD and txt.K_BK = work.K_BK"
                + " ) as B"
                + " on (A.K_RID = B.K_RID and A.K_KCD = B.K_KCD and A.K_BK = B.K_BK )"
                + " when matched then update set "
                + "LKB = B.LKB , K_RID = B.K_RID , K_KCD = B.K_KCD , K_BK = B.K_BK , YMD = B.YMD , MODIFY_KB = B.MODIFY_KB , ND1 = B.ND1 , ND2 = B.ND2 , AD_CD = B.AD_CD , AD_NM = B.AD_NM"
                + " when not matched then insert ("
                + "LKB , K_RID , K_KCD , K_BK , YMD , MODIFY_KB , ND1 , ND2 , AD_CD , AD_NM ) values ( "
                + "B.LKB , B.K_RID , B.K_KCD , B.K_BK , B.YMD , B.MODIFY_KB , B.ND1 , B.ND2 , B.AD_CD , B.AD_NM);";
        
        assertEquals(sql, merge2WorkSql);

    }
    
    public void testCreateMergeVals() {
        AltxtMetaDto altxtMetaDto = null;
        try {
            altxtMetaDto = JSON.decode(new FileReader("C:\\tisdev\\workspace\\altxt2db\\procfiles\\testParentdef.txt"), AltxtMetaDto.class);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        
        // 全項目そのまま
        String[] vals1 =  {"lkb", "krid", "kkcd", "kbk", "ymd", "mod", "nd1", "nd2", "adcd", "adnm"};
        String[] result1 = logic.createMergeVals(altxtMetaDto, vals1);
        for (int i = 0; i < vals1.length; i ++) {
            assertEquals(vals1[i], result1[i]);
        }
        
        // 通常項目に＠や@や空文字→＠だけは@に置換
        String[] vals2 = {"＠", "krid", "kkcd", "kbk", "@", "", "nd1", "nd2", "adcd", "adnm"};
        String[] result2 = logic.createMergeVals(altxtMetaDto, vals2);
        String[] assert2 = {"@", "krid", "kkcd", "kbk", "@", "", "nd1", "nd2", "adcd", "adnm"};
        for (int i = 0; i < assert2.length; i ++) {
            assertEquals(assert2[i], result2[i]);
        }
        
        // 集合項目の先頭に@→集合項目全てを@に置換
        String[] vals3 =  {"lkb", "@", "", "", "ymd", "mod", "nd1", "nd2", "＠", ""};
        String[] result3 = logic.createMergeVals(altxtMetaDto, vals3);
        String[] assert3 = {"lkb", "@", "@", "@", "ymd", "mod", "nd1", "nd2", "@", "@"};
        for (int i = 0; i < assert3.length; i ++) {
            assertEquals(assert3[i], result3[i]);
        }
        
        // 集合項目の一部が空文字→その一部を@に置換
        String[] vals4 =  {"lkb", "krid", "", "", "ymd", "mod", "nd1", "nd2", "＠", ""};
        String[] result4 = logic.createMergeVals(altxtMetaDto, vals4);
        String[] assert4 = {"lkb", "krid", "@", "@", "ymd", "mod", "nd1", "nd2", "@", "@"};
        for (int i = 0; i < assert4.length; i ++) {
            assertEquals(assert4[i], result4[i]);
        }

        // 集合項目の全てが空文字→置換なし
        String[] vals5 =  {"lkb", "", "", "", "ymd", "mod", "nd1", "nd2", "＠", ""};
        String[] result5 = logic.createMergeVals(altxtMetaDto, vals5);
        String[] assert5 = {"lkb", "", "", "", "ymd", "mod", "nd1", "nd2", "@", "@"};
        for (int i = 0; i < assert5.length; i ++) {
            assertEquals(assert5[i], result5[i]);
        }

        // 集合項目の全て埋まっている→置換なし
        String[] vals6 =  {"lkb", "krid", "kkcd", "kbk", "ymd", "mod", "nd1", "nd2", "＠", ""};
        String[] result6 = logic.createMergeVals(altxtMetaDto, vals6);
        String[] assert6 = {"lkb", "krid", "kkcd", "kbk", "ymd", "mod", "nd1", "nd2", "@", "@"};
        for (int i = 0; i < assert6.length; i ++) {
            assertEquals(assert6[i], result6[i]);
        }
        
    }
    
    /**
     * 
     * 一時テーブルdrop用SQL生成
     * 
     * IF OBJECT_ID(N'<work_table>', N'U') IS NOT NULL drop table <work_table>
     */
    public void testMakeDropWorkSql() {
        String assert1 = "IF OBJECT_ID(N'_____altxt2db_tablename', N'U') IS NOT NULL drop table _____altxt2db_tablename";
        String result1 = logic.makeDropWorkSql("tablename");
        assertEquals(assert1, result1);
    }
    
    /**
     * 
     * 一時テーブルcreate用SQL生成
     * 
     * select * into <work_table> from <table> where 0 = 1;
     * 
     */
    public void testMakeCreateWorkSql() {
        String assert1 = "select * into _____altxt2db_tablename from tablename where 0 = 1;";
        String result1 = logic.makeCreateWorkSql("tablename");
        assertEquals(assert1, result1);
    }
}