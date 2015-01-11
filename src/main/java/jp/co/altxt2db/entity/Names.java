package jp.co.altxt2db.entity;

import javax.annotation.Generated;
import jp.co.altxt2db.entity.ChildNames._ChildNames;
import jp.co.altxt2db.entity.ParentNames._ParentNames;

/**
 * 名前クラスの集約です。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.46", "org.seasar.extension.jdbc.gen.internal.model.NamesAggregateModelFactoryImpl"}, date = "2015/01/10 21:58:12")
public class Names {

    /**
     * {@link Child}の名前クラスを返します。
     * 
     * @return Childの名前クラス
     */
    public static _ChildNames child() {
        return new _ChildNames();
    }

    /**
     * {@link Parent}の名前クラスを返します。
     * 
     * @return Parentの名前クラス
     */
    public static _ParentNames parent() {
        return new _ParentNames();
    }
}