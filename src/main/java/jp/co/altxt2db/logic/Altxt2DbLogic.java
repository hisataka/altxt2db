package jp.co.altxt2db.logic;

import java.util.ArrayList;
import java.util.List;

import jp.co.altxt2db.constants.SystemConstants;
import jp.co.altxt2db.dto.AltxtMetaDto;


public class Altxt2DbLogic extends AbstractLogic implements SystemConstants  {

	public List<String> makeDeleteChildSql(AltxtMetaDto altxtMetaDto) {
		List<String> result = new ArrayList<>();


		StringBuilder sql = new StringBuilder();
    	/* 子供削除
    	 * 基礎ＳＱＬ↓
    	 *
    	delete from child where
    	 exists ( select *  from _____ALTXT2DB_CHILD B where mkb = 'C'
    	        and B.K_RID = child.K_RID and B.K_KCD = child.K_KCD and B.K_BK = child.K_BK and B.S_RID = child.S_RID and B.S_KCD = child.S_KCD and B.S_BK = child.S_BK);

    	        ★子供をループしながら回さないとだめ；配列で返すべきだな
*/
/*
		sql.append("delete from ");
		sql.append(altxtMetaDto.table);
		sql.append(" where exists ( select * from ");
		sql.append(WORK_PREFIX);
		sql.append(altxtMetaDto.table);
		sql.append("where MKB = 'C'");

		for (int i = 1; i < altxtMetaDto.keys.size(); i ++) {
			sql.append(" and ");
			sql.append(altxtMetaDto.keys.get(i));
			sql.append(" = ");
			sql.append(altxtMetaDto.table);
			sql.append(".");
			sql.append(altxtMetaDto.keys.get(i));
		}

		sql.append(");");*/

		return result;
	}

	public String makeMerge2MainSql(AltxtMetaDto altxtMetaDto) {
		StringBuilder sql = new StringBuilder();

		sql.append("merge into ");
		sql.append(altxtMetaDto.table);
		sql.append(" as A using (select case ");
		sql.append(WORK_PREFIX);
		sql.append(altxtMetaDto.table);
		sql.append(".");
		sql.append(altxtMetaDto.coldef.get(0).name);
		sql.append(" when '@' then '' when '' then ");
		sql.append(altxtMetaDto.table);
		sql.append(".");
		sql.append(altxtMetaDto.coldef.get(0).name);
		sql.append(" else ");
		sql.append(WORK_PREFIX);
		sql.append(altxtMetaDto.table);
		sql.append(".");
		sql.append(altxtMetaDto.coldef.get(0).name);
		sql.append(" end as ");
		sql.append(altxtMetaDto.coldef.get(0).name);

		for(int i = 1; i < altxtMetaDto.coldef.size(); i ++) {
			sql.append(" , case ");
			sql.append(WORK_PREFIX);
			sql.append(altxtMetaDto.table);
			sql.append(".");
			sql.append(altxtMetaDto.coldef.get(i).name);
			sql.append(" when '@' then '' when '' then ");
			sql.append(altxtMetaDto.table);
			sql.append(".");
			sql.append(altxtMetaDto.coldef.get(i).name);
			sql.append(" else ");
			sql.append(WORK_PREFIX);
			sql.append(altxtMetaDto.table);
			sql.append(".");
			sql.append(altxtMetaDto.coldef.get(i).name);
			sql.append(" end as ");
			sql.append(altxtMetaDto.coldef.get(i).name);
		}

		sql.append(" from ");
		sql.append(WORK_PREFIX);
		sql.append(altxtMetaDto.table);
		sql.append(" inner join ");
		sql.append(altxtMetaDto.table);
		sql.append(" on ");
		sql.append(WORK_PREFIX);
		sql.append(altxtMetaDto.table);
		sql.append(".");
		sql.append(altxtMetaDto.keys.get(0));
		sql.append(" = ");
		sql.append(altxtMetaDto.table);
		sql.append(".");
		sql.append(altxtMetaDto.keys.get(0));

		for (int i = 1; i < altxtMetaDto.keys.size(); i ++) {
			sql.append(" and ");
			sql.append(WORK_PREFIX);
			sql.append(altxtMetaDto.table);
			sql.append(".");
			sql.append(altxtMetaDto.keys.get(i));
			sql.append(" = ");
			sql.append(altxtMetaDto.table);
			sql.append(".");
			sql.append(altxtMetaDto.keys.get(i));
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

	public String makeMerge2WorkSql(AltxtMetaDto altxtMetaDto) {
		StringBuilder sql = new StringBuilder();
		sql.append("merge into ");
		sql.append(WORK_PREFIX);
		sql.append(altxtMetaDto.table);
		sql.append(" as A using (select ? as ");
		sql.append(altxtMetaDto.coldef.get(0).name);

		for(int i = 1; i < altxtMetaDto.coldef.size(); i ++) {
			sql.append(" , ? as ");
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

	public String[] createMergeVals(AltxtMetaDto altxtMetaDto, String[] vals) {
		String[] result = new String[vals.length];
		int resultIdx = 0;

		for (int i = 0; i < altxtMetaDto.coldef.size(); i ++) {

			// 集合項目の場合
			if ("true".equals(altxtMetaDto.coldef.get(i).aggregate)) {
				List<String> aggregates = new ArrayList<>();
				for (; i < altxtMetaDto.coldef.size() && "true".equals(altxtMetaDto.coldef.get(i).aggregate); i ++) {
					aggregates.add(vals[i]);
				}
				// 集合項目に対する処理
				// 先頭が@であるか
				if (isAt(aggregates.get(0))) {
					// 全項目を@に置換
					aggregates = replaceAllAt(aggregates.size());
				} else {
					// すべて空であるか
					if (isAllEmpty(aggregates)) {
						// 置換なし
					} else {
						// 空項目を@に置換
						aggregates = empty2At(aggregates);
					}
				}
				for(String aggregate: aggregates) {
					result[resultIdx ++] = aggregate;
				}
			}

			if (i < altxtMetaDto.coldef.size()) {
				// 通常項目の処理
				result[resultIdx ++] = mAt2sAt(vals[i]);
			}
		}


		return result;
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

	public List<String> replaceAllAt(int length) {
		List<String> result = new ArrayList<>();

		for (int i = 0; i < length; i ++) {
			result.add("@");
		}
		return result;
	}

	public boolean isAllEmpty(List<String> aggregates) {
		for(String aggregate: aggregates) {
			if (aggregate == null || !"".equals(aggregate)) {
				return false;
			}
		}
		return true;
	}

	public List<String> empty2At(List<String> aggregates) {
		List<String> result = new ArrayList<>();

		for (String aggregate: aggregates) {
			if (aggregate == null || "".equals(aggregate)) {
				result.add("@");
			} else {
				result.add(aggregate);
			}
		}
		return result;
	}

}
