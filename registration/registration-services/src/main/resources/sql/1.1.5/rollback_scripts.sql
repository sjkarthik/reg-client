DROP TABLE IF EXISTS reg.ca_cert_store CASCADE;
DROP CONSTRAINT IF EXISTS PK_CACS_ID;
ALTER TABLE "REG"."SYNC_JOB_DEF" DROP COLUMN "JOB_TYPE";