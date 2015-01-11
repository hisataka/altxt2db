package jp.co.altxt2db.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.altxt2db.constants.SystemConstants;
import jp.co.altxt2db.dto.AltxtMetaDto;


public class Altxt2DbLogic extends AbstractLogic implements SystemConstants  {
	public String makeUpdateSql(AltxtMetaDto altxtMetaDto) {
	//MERGE INTO work AS A
	//	USING (SELECT
	//		10 AS no
	//		,'太郎さん' AS name) AS B
	//	ON (
	//		A.no = B.no
	//		AND A.xx = B.xx)
	//	WHEN MATCHED THEN
	//		UPDATE SET
	//			name = B.name
	//			,age = B.age
	//	WHEN NOT MATCHED THEN
	//		INSERT (
	//			no
	//			,name)
	//	VALUES (
	//		B.no
	//		,B.name
	//);
		StringBuilder sql = new StringBuilder();

		sql.append("merge into ");
		sql.append(WORK_PREFIX);
		sql.append(altxtMetaDto.table);
		sql.append(" as A using (select '");
		sql.append(altxtMetaDto.coldef.get(0).value);
		sql.append("' as ");
		sql.append(altxtMetaDto.coldef.get(0).name);

		for(int i = 1; i < altxtMetaDto.coldef.size(); i ++) {
			sql.append(" , '");
			sql.append(altxtMetaDto.coldef.get(i).value);
			sql.append("' as ");
			sql.append(altxtMetaDto.coldef.get(i).name);
		}

		sql.append(" ) as B on (A.");
		sql.append(altxtMetaDto.keys.get(0));
		sql.append(" = B.");
		sql.append(altxtMetaDto.keys.get(0));

		for (int i = 1; i < altxtMetaDto.keys.size(); i ++) {
			sql.append(" and A.");
			sql.append(altxtMetaDto.keys.get(i));
			sql.append(" = B.");
			sql.append(altxtMetaDto.keys.get(i));
		}

		sql.append(" ) when matched then update set ");
		sql.append(altxtMetaDto.coldef.get(0).name);
		sql.append(" = B.");
		sql.append(altxtMetaDto.coldef.get(0).name);

		for (int i = 1; i < altxtMetaDto.coldef.size(); i ++) {
			sql.append(" , ");
			sql.append(altxtMetaDto.coldef.get(i).name);
			sql.append(" = B.");
			sql.append(altxtMetaDto.coldef.get(i).name);
		}

		sql.append(" when not mached then insert (");
		sql.append(altxtMetaDto.coldef.get(0).name);

		for (int i = 1; i < altxtMetaDto.coldef.size();  i ++) {
			sql.append(" , ");
			sql.append(altxtMetaDto.coldef.get(i).name);
		}

		sql.append(" ) values ( B.");
		sql.append(altxtMetaDto.coldef.get(0).name);

		for (int i = 1; i < altxtMetaDto.coldef.size();  i ++) {
			sql.append(" , B.");
			sql.append(altxtMetaDto.coldef.get(i).name);
		}
		sql.append(");");

		return sql.toString();
	}


	public boolean setColValue(AltxtMetaDto altxtMetaDto, String[] vals) {

		for (int i = 0; i < altxtMetaDto.coldef.size(); i ++) {

			// 集合項目の場合
			if ("true".equals(altxtMetaDto.coldef.get(i).aggregate)) {
				List<Map<String, String>> aggregates = new ArrayList<>();

				for (; i < altxtMetaDto.coldef.size() && "true".equals(altxtMetaDto.coldef.get(i).aggregate); i ++) {
					Map<String, String> colmap = new HashMap<>();
					colmap.put(KEY, String.valueOf(i));
					colmap.put(VAL, vals[i]);
					aggregates.add(colmap);
				}
				// 集合項目に対する処理
				// 先頭が@であるか
				if (isAt(aggregates.get(0).get(VAL))) {
					// 全項目を@に置換
					aggregates = replaceAllAt(aggregates);
				} else {
					// すべて空であるか
					if (isAllEmpty(aggregates)) {
						// 置換なし
					} else {
						// 空項目を@に置換
						aggregates = empty2At(aggregates);
					}
				}
				for(Map<String, String> aggregate: aggregates) {
					altxtMetaDto.coldef.get(Integer.parseInt(aggregate.get(KEY))).value = aggregate.get(VAL);
				}
			}

			if (i < altxtMetaDto.coldef.size()) {
				// 通常項目の処理
				altxtMetaDto.coldef.get(i).value = vals[i];
			}
		}
		return true;
	}

	public String mAt2sAt(String str) {
		if ("＠".equals(str)) {
			return "@";
		}  else {
			return str;
		}
	}

	public boolean isAt(String str) {
		if ("＠".equals(str) || "@".equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	public List<Map<String, String>> replaceAllAt(List<Map<String, String>> aggregates) {
		List<Map<String, String>> result = new ArrayList<>();

		for (Map<String, String> aggregate: aggregates) {
			Map<String, String> map = new HashMap<>();
			map.put(KEY, aggregate.get(KEY));
			map.put(VAL, "@");
			result.add(map);
		}
		return result;
	}

	public boolean isAllEmpty(List<Map<String, String>> aggregates) {
		for(Map<String, String> aggregate: aggregates) {
			String val = aggregate.get(VAL);
			if (val == null || !"".equals(val)) {
				return false;
			}
		}
		return true;
	}

	public List<Map<String, String>> empty2At(List<Map<String, String>> aggregates) {
		List<Map<String, String>> result = new ArrayList<>();

		for (Map<String, String> aggregate: aggregates) {
			Map<String, String> map = new HashMap<>();
			map.put(KEY, aggregate.get(KEY));
			String val = aggregate.get(VAL);

			if (val == null || "".equals(val)) {
				map.put(VAL, "@");
			} else {
				map.put(VAL, val);
			}

			result.add(map);
		}

		return result;
	}

}
