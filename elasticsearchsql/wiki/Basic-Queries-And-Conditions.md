## Query
The basic query syntax is:<br>
 ```sql
 SELECT fields from indexName WHERE conditions
 ```
To query a specific type of an index use:<br>
```sql
SELECT fields from indexName/type WHERE conditions
```
You can also query several types and indices at once, like this:<br>
```sql
SELECT fields from indexName/type1,indexName/type2 WHERE conditions
```
#### SQL Statements
*  SQL Select
*  SQL Delete
*  SQL Where
*  SQL Order By
*  SQL Group By
*  SQL Limit (default is 200)

#### Conditions:
*  SQL Like 
*  SQL AND & OR
*  SQL COUNT distinct
*  SQL In
*  SQL Between
*  SQL Aliases
*  SQL(ES) Date
*  SQL now()
*  SQL NOT 

#### Basic aggregations:
*  SQL avg()
*  SQL count()
*  SQL last()
*  SQL max()
*  SQL min()
*  SQL sum()

#### SQL Fields
Fields can be listed out by exact field name or used with the include/exclude syntax for use with wildcards.  
*  include('d*') - include all fields starting with "d"
*  exclude('age') - include all fields except "age"
*  include('*Name'), exclude('lastName') - include all fields that end with "Name" except "lastName"

### Examples:
```sql
SELECT * FROM bank WHERE age >30 AND gender = 'm'
SELECT * FROM bank/account ORDER BY balance DESC LIMIT 500
SELECT count(*),avg(balance) FROM bank/account
SELECT balance, include('*Name'), exclude('lastName') FROM bank
SELECT * FROM ['888afc4c-1b45-4e2b-bac7-e51635bd5f70-888afc4c-1b45-4e2b-bac7-e51635bd5f70-abc'] WHERE ['last name']='bar'
```
