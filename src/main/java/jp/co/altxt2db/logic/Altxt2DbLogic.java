package jp.co.altxt2db.logic;

import java.util.ArrayList;
import java.util.List;

import jp.co.altxt2db.constants.SystemConstants;
import jp.co.altxt2db.dto.AltxtMetaChildDto;
import jp.co.altxt2db.dto.AltxtMetaDto;

/**
 * AltxtをDBへ格納するバッチのロジック
 * 
 */
public class Altxt2DbLogic extends AbstractLogic implements SystemConstants  {

    /**
     * 
     * 一時テーブルdrop用SQL生成
     * 
     * IF OBJECT_ID(N'<work_table>', N'U') IS NOT NULL drop table <work_table>
     * 
     * @param table
     * @return
     */
    public String makeDropWorkSql(String table) {
        StringBuilder sql = new StringBuilder();
        sql.append("IF OBJECT_ID(N'");
        sql.append(WORK_PREFIX);
        sql.append(table);
        sql.append("', N'U') IS NOT NULL drop table ");
        sql.append(WORK_PREFIX);
        sql.append(table);
        
        return sql.toString();
    }
    
    /**
     * 
     * 一時テーブルcreate用SQL生成
     * 
     * select * into <work_table> from <table> where 0 = 1;
     * 
     * @param table
     * @return
     */
    public String makeCreateWorkSql(String table) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * into ");
        sql.append(WORK_PREFIX);
        sql.append(table);
        sql.append(" from ");
        sql.append(table);
        sql.append(" where 0 = 1;");
        
        return sql.toString();
    }
    
    /**
     * 
     * 子テーブル削除用SQL生成
     * 
     * update 子 set 修正区分 = 'C' 
     *     where exists ( select * 
     *                    from 親一時テーブル
     *                    where 修正区分 = 'C'
     *                          キー1 = 子.キー1
     *                          AND キー2 = 子.キー2
     *                              ・
     *                              ・
     *                              ・
     *           );
     * 
     * @param altxtMetaDto
     * @return
     */
	public List<String> makeDeleteChildSql(AltxtMetaDto altxtMetaDto) {
		if (altxtMetaDto.children == null) {
			return new ArrayList<String>();
		}

		List<String> result = new ArrayList<>();

		for (AltxtMetaChildDto child: altxtMetaDto.children) {
			StringBuilder sql = new StringBuilder();
			sql.append("update ");
			sql.append(child.table);
			sql.append(" set ");
			sql.append(altxtMetaDto.mod_colname);
			sql.append(" = '");
			sql.append(altxtMetaDto.mod_delval);
			sql.append("' where exists ( select * from ");
			sql.append(WORK_PREFIX);
			sql.append(altxtMetaDto.table);
			sql.append(" where ");
			sql.append(altxtMetaDto.mod_colname);
			sql.append(" = '");
			sql.append(altxtMetaDto.mod_delval);
			sql.append("'");

			for (int i = 0; i < child.keymap.size(); i ++) {
				sql.append(" and ");
				sql.append(child.keymap.get(i).parent);
				sql.append(" = ");
				sql.append(child.table);
				sql.append(".");
				sql.append(child.keymap.get(i).child);
			}

			sql.append(");");

			result.add(sql.toString());
		}
		return result;
	}

	/**
	 * 
	 *  本テーブルマージ用SQL生成
	 *  
	 *  merge into 本テーブル as A
	 *     using (select isnull(case 一時テーブル.カラム1 when '@' then '' when '' then 本テーブル.カラム1 else 一時テーブル.カラム1 end, '') as カラム1
	 *                   , isnull(case 一時テーブル.カラム2 when '@' then '' when '' then 本テーブル.カラム2 else 一時テーブル.カラム2 end, '') as カラム2
	 *                      ・
	 *                      ・
	 *                      ・
	 *            from 一時テーブル
	 *                left outer join 本テーブル
	 *                    on 一時テーブル.キー1 = 本テーブル.キー1
	 *                        and 一時テーブル.キー2 = 本テーブル.キー2
	 *                            ・
	 *                            ・
	 *                            ・
	 *     ) as B
	 *     on (A.キー1 = B.キー1
	 *         and A.キー2 = B.キー2
	 *             ・
	 *             ・
	 *             ・
	 *     )
	 *     when matched then update set
	 *         カラム1 = B.カラム1
	 *         , カラム2 = B.カラム2
	 *             ・
	 *             ・
	 *             ・
	 *     when not matched then insert (
	 *         カラム1
	 *         , カラム2
	 *             ・
	 *             ・
	 *             ・
	 *     ) values (
	 *         B.カラム1
	 *         , B.カラム2
	 *             ・
	 *             ・
	 *             ・
	 *     );
	 *  
	 * @param altxtMetaDto
	 * @return
	 */
	public String makeMerge2MainSql(AltxtMetaDto altxtMetaDto) {
		StringBuilder sql = new StringBuilder();

		sql.append("merge into ");
		sql.append(altxtMetaDto.table);
		sql.append(" as A using (select isnull(case ");
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
		sql.append(" end, '') as ");
		sql.append(altxtMetaDto.coldef.get(0).name);

		for(int i = 1; i < altxtMetaDto.coldef.size(); i ++) {
			sql.append(" , isnull(case ");
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
			sql.append(" end, '') as ");
			sql.append(altxtMetaDto.coldef.get(i).name);
		}

		sql.append(" from ");
		sql.append(WORK_PREFIX);
		sql.append(altxtMetaDto.table);
		sql.append(" left outer join ");
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

		sql.append(" when not matched then insert (");
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

	/**
	 * 
     *  一時テーブルマージ用SQL生成
     *  
     *  merge into 一時テーブル as A
     *     using (select isnull(case txt.カラム1 when '' then work.カラム1 else txt.カラム1 end, '') as カラム1
     *                   , isnull(case txt.カラム2 when '' then work.カラム2 else txt.カラム2 end, '') as カラム2
     *                      ・
     *                      ・
     *                      ・
     *            from (select ? as カラム1
     *                         , ? as カラム2
     *                             ・
     *                             ・
     *                             ・
     *                 ) as txt
     *                     left outer join 一時テーブル as work
     *                         on txt.キー1 = work.キー1
     *                         and txt.キー2 = work.キー2
     *                             ・
     *                             ・
     *                             ・
     *     ) as B
     *     on (A.キー1 = B.キー1
     *         and A.キー2 = B.キー2
     *             ・
     *             ・
     *             ・
     *     )
     *     when matched then update set
     *         カラム1 = B.カラム1
     *         , カラム2 = B.カラム2
     *             ・
     *             ・
     *             ・
     *     when not matched then insert (
     *         カラム1
     *         , カラム2
     *             ・
     *             ・
     *             ・
     *     ) values (
     *         B.カラム1
     *         , B.カラム2
     *             ・
     *             ・
     *             ・
     *     );
     *     
	 * @param altxtMetaDto
	 * @return
	 */
	public String makeMerge2WorkSql(AltxtMetaDto altxtMetaDto) {
		StringBuilder sql = new StringBuilder();
		sql.append("merge into ");
		sql.append(WORK_PREFIX);
		sql.append(altxtMetaDto.table);
		sql.append(" as A using (select isnull(case txt.");
		sql.append(altxtMetaDto.coldef.get(0).name);
		sql.append(" when '' then work.");
		sql.append(altxtMetaDto.coldef.get(0).name);
		sql.append(" else txt.");
		sql.append(altxtMetaDto.coldef.get(0).name);
		sql.append(" end, '') as ");
		sql.append(altxtMetaDto.coldef.get(0).name);
		
		for (int i = 1; i < altxtMetaDto.coldef.size(); i ++) {
		    sql.append(" , isnull(case txt.");
	        sql.append(altxtMetaDto.coldef.get(i).name);
	        sql.append(" when '' then work.");
	        sql.append(altxtMetaDto.coldef.get(i).name);
	        sql.append(" else txt.");
	        sql.append(altxtMetaDto.coldef.get(i).name);
	        sql.append(" end, '') as ");
	        sql.append(altxtMetaDto.coldef.get(i).name);
		}
		
		sql.append(" from (select ? as ");
		sql.append(altxtMetaDto.coldef.get(0).name);

		for(int i = 1; i < altxtMetaDto.coldef.size(); i ++) {
			sql.append(" , ? as ");
			sql.append(altxtMetaDto.coldef.get(i).name);
		}
		
		sql.append(" ) as txt left outer join ");
        sql.append(WORK_PREFIX);
        sql.append(altxtMetaDto.table);
        sql.append(" as work on txt.");
        sql.append(altxtMetaDto.keys.get(0));
        sql.append(" = work.");
        sql.append(altxtMetaDto.keys.get(0));
        
        for (int i = 1; i < altxtMetaDto.keys.size(); i ++) {
            sql.append(" and txt.");
            sql.append(altxtMetaDto.keys.get(i));
            sql.append(" = work.");
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

		sql.append(" when not matched then insert (");
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

	/**
	 * 
	 * 集合項目ルール　→　一般項目ルールへのマージ及び、＠→@置換などを行う
	 * 
	 * @param altxtMetaDto
	 * @param vals
	 * @return
	 */
	public String[] createMergeVals(AltxtMetaDto altxtMetaDto, String[] vals) {
		String[] result = new String[vals.length];
		int resultIdx = 0;

		for (int i = 0; i < altxtMetaDto.coldef.size(); i ++) {

			// 集合項目の場合
			if (TRUE.equals(altxtMetaDto.coldef.get(i).aggregate)) {
				List<String> aggregates = new ArrayList<>();
				for (; i < altxtMetaDto.coldef.size() && TRUE.equals(altxtMetaDto.coldef.get(i).aggregate); i ++) {
					aggregates.add(vals[i]);
				}
				// 集合項目に対する処理
				// 先頭が@であるか
				if (isAt(aggregates.get(0))) {
					// 全項目を@で生成しなおす
					aggregates = createAtList(aggregates.size());
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
}
