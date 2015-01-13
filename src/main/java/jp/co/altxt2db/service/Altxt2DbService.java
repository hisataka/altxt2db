package jp.co.altxt2db.service;

import jp.co.altxt2db.constants.SystemConstants;

/**
 * AltxtをDBへ格納するバッチのDBサービスクラス
 *
 */
public class Altxt2DbService extends AbstractGeneralService implements SystemConstants {
	/**
	 * 
	 * SQL実行（パラメータ無し）
	 * 
	 * @param sql
	 */
	public void execSql(String sql) {
		updateBySql(sql).execute();
	}

	/**
	 * 
	 * SQL実行（パラメータ有り）
	 * 
	 * @param sql
	 * @param params
	 */
	@SuppressWarnings("rawtypes")
	public void execSql(String sql, Object[] params) {
		Class[] classes = new Class[params.length];
		for (int i = 0; i < params.length; i ++) {
			classes[i] = String.class;
		}
		updateBySql(sql, classes).params(params).execute();
	}
}