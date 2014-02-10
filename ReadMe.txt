1、新建MySQL数据库tac
2、把sentence.sql导入到tac中
3、在tac中新建数据表last
create table last(
id  int NOT NULL  PRIMARY KEY ,
date  varchar(10) NOT NULL ,
sentences  text NULL ,
termWeight  text NULL ,
energy  text NULL ,
summary  text NULL ,
);
4.运行src/main/process/SummaryRun.java中的测试方法即可：
所有的自动文摘方法都在一个测试类SummaryRun中测试，对应关系如下：
AIL        对应testAIL()
AIL1      对应testAIL_noenergy()
AIL2      对应testAIL_nolsa()
Allan      对应testAllan()
Centroid 对应testCentroid()

