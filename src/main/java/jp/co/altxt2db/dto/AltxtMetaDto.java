package jp.co.altxt2db.dto;

import java.util.List;

public class AltxtMetaDto {
	public String table;
	public List<String> keys;
	public List<AltxtMetaColdefDto> coldef;
	public List<AltxtMetaChildDto> children;
}
