CREATE TABLE IF NOT EXISTS part (
  P_PARTKEY     INTEGER NOT NULL,
  P_NAME        VARCHAR(55) NOT NULL,
  P_MFGR        CHAR(25) NOT NULL,
  P_BRAND       CHAR(10) NOT NULL,
  P_TYPE        VARCHAR(25) NOT NULL,
  P_SIZE        INTEGER NOT NULL,
  P_CONTAINER   CHAR(10) NOT NULL,
  P_RETAILPRICE DECIMAL(15,2) NOT NULL,
  P_COMMENT     VARCHAR(23) NOT NULL 
)
UNIQUE KEY(P_PARTKEY)
DISTRIBUTED BY HASH(P_PARTKEY) BUCKETS 1
PROPERTIES (
  "enable_mow_light_delete" = "true",
  "replication_num" = "1"
)

