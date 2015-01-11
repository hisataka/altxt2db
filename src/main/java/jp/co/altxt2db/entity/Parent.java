package jp.co.altxt2db.entity;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Parentエンティティクラス
 * 
 */
@Entity
@Generated(value = {"S2JDBC-Gen 2.4.46", "org.seasar.extension.jdbc.gen.internal.model.EntityModelFactoryImpl"}, date = "2015/01/10 21:58:07")
public class Parent implements Serializable {

    private static final long serialVersionUID = 1L;

    /** lkbプロパティ */
    @Column(length = 3, nullable = true, unique = false)
    public String lkb;

    /** KRidプロパティ */
    @Id
    @Column(length = 2, nullable = false, unique = false)
    public String KRid;

    /** KKcdプロパティ */
    @Id
    @Column(length = 3, nullable = false, unique = false)
    public String KKcd;

    /** KBkプロパティ */
    @Id
    @Column(length = 1, nullable = false, unique = false)
    public String KBk;

    /** ymdプロパティ */
    @Column(length = 8, nullable = true, unique = false)
    public String ymd;

    /** mkbプロパティ */
    @Column(length = 1, nullable = true, unique = false)
    public String mkb;

    /** nd1プロパティ */
    @Column(length = 128, nullable = true, unique = false)
    public String nd1;

    /** nd2プロパティ */
    @Column(length = 128, nullable = true, unique = false)
    public String nd2;

    /** adCdプロパティ */
    @Column(length = 3, nullable = true, unique = false)
    public String adCd;

    /** adNmプロパティ */
    @Column(length = 128, nullable = true, unique = false)
    public String adNm;
}