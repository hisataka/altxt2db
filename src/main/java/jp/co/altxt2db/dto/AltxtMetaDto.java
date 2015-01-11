package jp.co.altxt2db.dto;

import java.util.List;

public class AltxtMetaDto {
	public String table;
	public List<AltxtMetaAggregateDto> aggregate;
	public List<AltxtMetaColdefDto> coldef;
}
