CREATE TABLE S_LOGINFO
  (
     MODULE     NVARCHAR(2000),
     ACTION     NVARCHAR(2000),
     MESSAGE    CLOB,
     EXCEPTION  NVARCHAR(2000),
     LEVEL      NVARCHAR(2000),
     RESULT     NVARCHAR(2000),
     STARTTIME  NVARCHAR(2000),
     FINISHTIME NVARCHAR(2000),
     OPERATOR   NVARCHAR(2000),
     OPERATORID NVARCHAR(2000),
     BIZMODULE  NVARCHAR(2000),
     BIZID      NVARCHAR(2000),
     STACK      NVARCHAR(2000),
     THREADNAME NVARCHAR(2000),
     CONTEXT    NVARCHAR(2000)
  ) 