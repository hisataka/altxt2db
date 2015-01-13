package jp.co.altxt2db.service;

import jp.co.altxt2db.constants.SystemConstants;

/**
 * AltxtをDBへ格納するバッチのDBサービスクラス
 * @author tie302852
 *
 */
public class Altxt2DbService extends AbstractGeneralService implements SystemConstants {
    
    /**
     * 
     * 一時テーブルの再生成
     * 
     * @param from
     */
	public void createWork(String from) {
        // IF OBJECT_ID(N'<table>', N'U') IS NOT NULL drop table <table>
		StringBuilder dropSql = new StringBuilder();
		dropSql.append("IF OBJECT_ID(N'");
		dropSql.append(WORK_PREFIX);
		dropSql.append(from);
		dropSql.append("', N'U') IS NOT NULL drop table ");
        dropSql.append(WORK_PREFIX);
        dropSql.append(from);

        // select * into <table> from parent where 0 = 1;
		StringBuilder createSql = new StringBuilder();
		createSql.append("select * into ");
		createSql.append(WORK_PREFIX);
		createSql.append(from);
		createSql.append(" from ");
		createSql.append(from);
		createSql.append(" where 0 = 1;");

		execSql(dropSql.toString());
		execSql(createSql.toString());
	}

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