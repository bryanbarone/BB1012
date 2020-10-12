insert into TOOL (CODE, BRAND, TYPE) values ('LADW', 'Werner', 'Ladder');
insert into TOOL (CODE, BRAND, TYPE) values ('CHNS', 'Stihl', 'Chainsaw');
insert into TOOL (CODE, BRAND, TYPE) values ('JAKR', 'Ridgid', 'Jackhammer');
insert into TOOL (CODE, BRAND, TYPE) values ('JAKD', 'DeWalt', 'Jackhammer');

insert into RENTAL_COST (TOOLTYPE_ID, CHARGE ,WEEKDAY, WEEKEND, HOLIDAY) values ('Ladder', 1.99, true, true, false);
insert into RENTAL_COST (TOOLTYPE_ID, CHARGE ,WEEKDAY, WEEKEND, HOLIDAY) values ('Chainsaw', 1.49, true, false, true);
insert into RENTAL_COST (TOOLTYPE_ID, CHARGE ,WEEKDAY, WEEKEND, HOLIDAY) values ('Jackhammer', 2.99, true, false, false);