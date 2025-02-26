# Sportsclub database java application

This piece of code is an example on how to apply CRUD operations on
a Postgres database from Java through jdbc.

The code is written for 2. semester on the Datamatiker education in Lyngby.

The code is NOT refactored.

## Exercises

Your task is to continue the work that has been started.
We need to get some useful queries built into the system, and also
datamappers for some of the other entities in the database.

### Get started

1) Begin by cloning the project
2) After cloning, you can remove the .git folder by typing `rm -rf .git`. 
3) Open the project in IntelliJ
4) Create the database 'sportsclub' in Postgres via pgAdmin
5) Open the file "src/main/resources/sportsclub.sql" in pgAdmin and execute it to create the sportsclub tables and fill them with data.
6) Update the login credentials in the Main class to reflect your local system
7) Run it and get to know the code.

After completing the following tasks, you should test them from your main-method. 
### Add some nice queries

Add some of these methods to the MemberMapper class. As many as you can, and begin
querying in pgAdmin to fine tune your sql-statements:

7) Find the number of participants on each team
8) Find the number of participants for each sport
9) Find the number of men and women in the club
10) Find the total sum of income for all teams
11) Find the total sum of income for each team
12) Find the average payment for each team

Hint: It might be useful to create a view with lots of joins

### Add a RegistrationMapper

13) Add a new class in the persistence package called "RegistrationMapper"
14) Add a method that can be used to add a new registration to a member. For example as `addToTeam(int member_id, String team_id, int price)`. 
    Your test could be to add the team called "ten01" to member 13. You should probably let your self be inspired 
    by the insertMember method in the MemberMapper.
15) Add a new method to the RegistrationMapper called getAllRegistrations() that returns an arraylist of registrations.
16) You might want to pimp the getAllRegistrations so that you can print out more
    than just the (member_id, team_id) data. It would be lovely with a list of member names
    and team name etc.

### Add some DTO-classes and methods
17) Add a new package called dto
18) In your dto package add a new class called "MemberAndSportsDTO". The class should have an attribute called member and an attribute called sports. The latter is a list of sports that the member plays
19) Add construcktor and getter/setters to your class
20) Make a method getMemberAndSportsById(int id) to your MemberMapper
21) The method should return a MemberAndSportsDTO with a member and a list of the sports that the member plays

### The ER diagram

![](src/main/resources/sportsclub.png)
