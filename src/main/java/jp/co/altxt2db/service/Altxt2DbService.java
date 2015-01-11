package jp.co.altxt2db.service;

import java.util.HashMap;
import java.util.Map;

import jp.co.altxt2db.constants.SystemConstants;



public class Altxt2DbService extends AbstractGeneralService implements SystemConstants {
	public String createWork(String from) {
		Map<String, String> param = new HashMap<>();
		String workTable = WORK_PREFIX + from;

		param.put("to", workTable);
		param.put("from", from);
		updateBySqlFile("drop.sql", param).execute();
		updateBySqlFile("selectcreate.sql", param).execute();

		return workTable;
	}
	public void execSql(String sql) {
		updateBySql(sql).execute();
	}
//	public List<HOGE> selecthoge() {
//		return selectBySqlFile(HOGE.class, "hogeselect.sql").getResultList();
//	}
}