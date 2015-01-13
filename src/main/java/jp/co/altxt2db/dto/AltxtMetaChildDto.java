package jp.co.altxt2db.dto;

import java.util.List;

/**
 * メタデータ格納DTO 子テーブル
 * 
 * @author tie302852
 *
 */
public class AltxtMetaChildDto {
    /** 子テーブル名 */
	public String table;
	/** 親テーブルと結合する際のキーマップ */
	public List<AltxtMetaKeymapDto> keymap;
}
