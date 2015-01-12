package jp.co.altxt2db.service;

import java.util.HashMap;
import java.util.Map;

import jp.co.altxt2db.constants.SystemConstants;

public class Altxt2DbService extends AbstractGeneralService implements SystemConstants {
	public void createWork(String from) {
		Map<String, String> param = new HashMap<>();
		String workTable = WORK_PREFIX + from;

		param.put("to", workTable);
		param.put("from", from);
		updateBySqlFile("drop.sql", param).execute();
		updateBySqlFile("selectcreate.sql", param).execute();
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