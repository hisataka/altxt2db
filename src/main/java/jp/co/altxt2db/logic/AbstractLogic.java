package jp.co.altxt2db.logic;

import java.util.ArrayList;
import java.util.List;

import jp.co.altxt2db.constants.SystemConstants;

/**
 * 基底ロジッククラス
 * 
 */
public abstract class AbstractLogic implements SystemConstants {
    /**
     * 
     * マルチバイトのアットマークをシングルバイトのアットマークに変換する
     * 
     * @param str
     * @return
     */
    public String mAt2sAt(String str) {
        if ("＠".equals(str)) {
            return "@";
        }  else {
            return str;
        }
    }

    /**
     * 
     * 文字列がアットマークであるかを判定する
     * 
     * @param str
     * @return
     */
    public boolean isAt(String str) {
        if ("＠".equals(str) || "@".equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * 指定された長さのリストを@で埋めて生成する
     * 
     * @param length
     * @return
     */
    public List<String> createAtList(int length) {
        List<String> result = new ArrayList<String>();

        for (int i = 0; i < length; i ++) {
            result.add("@");
        }
        return result;
    }

    /**
     * 
     * 要素が全て空値であるかを判定する
     * 
     * @param aggregates
     * @return
     */
    public boolean isAllEmpty(List<String> aggregates) {
        if (aggregates == null) return false;
        
        for(String aggregate: aggregates) {
            if (aggregate == null || !EMPTY.equals(aggregate)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * 空値の要素を@に変換する
     * 
     * @param aggregates
     * @return
     */
    public List<String> empty2At(List<String> aggregates) {
        if (aggregates == null) return null;
        
        List<String> result = new ArrayList<String>();

        for (String aggregate: aggregates) {
            if (aggregate == null || EMPTY.equals(aggregate)) {
                result.add("@");
            } else {
                result.add(aggregate);
            }
        }
        return result;
    }
}
