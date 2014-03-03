1���½�MySQL��ݿ�tac
2����sentence.sql���뵽tac��
3����tac���½���ݱ�last
create table last(
id  int NOT NULL  PRIMARY KEY ,
date  varchar(10) NOT NULL ,
sentences  text NULL ,
termWeight  text NULL ,
energy  text NULL ,
summary  text NULL 
);
4.����src/main/process/SummaryRun.java�еĲ��Է������ɣ�
���е��Զ���ժ��������һ��������SummaryRun�в��ԣ���Ӧ��ϵ���£�
AIL        ��ӦtestAIL()
AIL1      ��ӦtestAIL_noenergy()
AIL2      ��ӦtestAIL_nolsa()
Allan      ��ӦtestAllan()
Centroid ��ӦtestCentroid()

