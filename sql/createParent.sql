CREATE TABLE PARENT
(
    LKB                            VARCHAR(128) NULL,
    K_RID                          VARCHAR(128) NOT NULL,
    K_KCD                          VARCHAR(128) NOT NULL,
    K_BK                           VARCHAR(128) NOT NULL,
    YMD                            VARCHAR(128) NULL,
    MODIFY_KB                      VARCHAR(128) NULL,
    ND1                            VARCHAR(128) NULL,
    ND2                            VARCHAR(128) NULL,
    AD_CD                          VARCHAR(128) NULL,
    AD_NM                          VARCHAR(128) NULL
)
go
ALTER TABLE PARENT
    ADD CONSTRAINT PK_PARENT PRIMARY KEY  CLUSTERED (K_RID, K_KCD, K_BK)
go
