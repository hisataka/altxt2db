create table parent (
	LKB varchar(3),
	K_RID varchar(2),
	K_KCD varchar(3),
	K_BK varchar(1),
	YMD char(8),
	MKB char(1),
	ND1 varchar(128),
	ND2 varchar(128),
	AD_CD varchar(3),
	AD_NM varchar(128),
	primary key (K_RID,K_KCD,K_BK)
)