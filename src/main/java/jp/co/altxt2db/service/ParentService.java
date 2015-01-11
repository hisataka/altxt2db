package jp.co.altxt2db.service;

import java.util.List;
import javax.annotation.Generated;
import jp.co.altxt2db.entity.Parent;

import static jp.co.altxt2db.entity.ParentNames.*;
import static org.seasar.extension.jdbc.operation.Operations.*;

/**
 * {@link Parent}のサービスクラスです。
 * 
 */
@Generated(value = {"S2JDBC-Gen 2.4.46", "org.seasar.extension.jdbc.gen.internal.model.ServiceModelFactoryImpl"}, date = "2015/01/10 21:58:18")
public class ParentService extends AbstractService<Parent> {

    /**
     * 識別子でエンティティを検索します。
     * 
     * @param KRid
     *            識別子
     * @param KKcd
     *            識別子
     * @param KBk
     *            識別子
     * @return エンティティ
     */
    public Parent findById(String KRid, String KKcd, String KBk) {
        return select().id(KRid, KKcd, KBk).getSingleResult();
    }

    /**
     * 識別子の昇順ですべてのエンティティを検索します。
     * 
     * @return エンティティのリスト
     */
    public List<Parent> findAllOrderById() {
        return select().orderBy(asc(KRid()), asc(KKcd()), asc(KBk())).getResultList();
    }
}