package jp.co.altxt2db.service;

import java.util.List;
import javax.annotation.Generated;
import jp.co.altxt2db.entity.Child;

import static jp.co.altxt2db.entity.ChildNames.*;
import static org.seasar.extension.jdbc.operation.Operations.*;

/**
 * {@link Child}のサービスクラスです。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.46", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2015/01/10 21:58:18")
public class ChildService extends AbstractService<Child> {

    /**
     * 識別子でエンティティを検索します。
     * 
     * @param KRid
     *            識別子
     * @param KKcd
     *            識別子
     * @param KBk
     *            識別子
     * @param SRid
     *            識別子
     * @param SKcd
     *            識別子
     * @param SBk
     *            識別子
     * @return エンティティ
     */
    public Child findById(String KRid, String KKcd, String KBk, String SRid, String SKcd, String SBk) {
        return select().id(KRid, KKcd, KBk, SRid, SKcd, SBk).getSingleResult();
    }

    /**
     * 識別子の昇順ですべてのエンティティを検索します。
     * 
     * @return エンティティのリスト
     */
    public List<Child> findAllOrderById() {
        return select().orderBy(asc(KRid()), asc(KKcd()), asc(KBk()), asc(SRid()), asc(SKcd()), asc(SBk())).getResultList();
    }
}