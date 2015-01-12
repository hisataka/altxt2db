package jp.co.altxt2db.service;

import jp.co.altxt2db.constants.SystemConstants;

public class Altxt2DbService extends AbstractGeneralService implements SystemConstants {
	public void createWork(String from) {

		StringBuilder dropSql = new StringBuilder();
		dropSql.append("drop table if exists ");
		dropSql.append(WORK_PREFIX);
		dropSql.append(from);
		dropSql.append(";");

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

	public void execSql(String sql) {
		updateBySql(sql).execute();
	}

	@SuppressWarnings("rawtypes")
	public void execSql(String sql, Object[] params) {
		Class[] classes = new Class[params.length];
		for (int i = 0; i < params.length; i ++) {
			classes[i] = String.class;
		}
		updateBySql(sql, classes).params(params).execute();
	}
}