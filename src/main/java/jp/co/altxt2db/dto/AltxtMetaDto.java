package jp.co.altxt2db.dto;

import java.util.List;

/**
 * メタデータ格納DTO ルート
 * 
 */
public class AltxtMetaDto {
    /** マージ対象テーブル名 */
	public String table;
	/** 修正区分格納カラム名 */
	public String mod_colname;
	/** 修正区分の削除を意味する値 */
	public String mod_delval;
	/** マージ対象テーブルの主キーリスト */
	public List<String> keys;
	/** マージ対象テーブルのカラム定義 */
	public List<AltxtMetaColdefDto> coldef;
	/** マージ対象テーブルの子テーブル群 */
	public List<AltxtMetaChildDto> children;
}
