### Join
As you might now, elasticsearch does not supports join<br>
on elasticsearch-sql we implemented a limited support on join queries!<br>
We also implemented some hints to help with your join<br>
We supports both JOIN and LEFT_JOIN

To use Join you must specified JOIN or LEFT_JOIN on your query. (commas join not supported)<br>
There are two types of implementations , Hash_Join and Nested_Loops<br>

#### Implementations
##### Nested Loops
Query first table and for each X (deafult is 100 you can change with NL_MULTISEARCH_SIZE hint ) results create a multiSearch request on second table. </br>
The first query will request with scrolls each time ,  if first table limit is over 10k documents  (use JOIN_TABLES_LIMIT hint)
##### Hash join
Query first table until limit and construct a Map Map<key,List<Documents>> <br>
The key is combined from string representations on all values from "ON" conditions. <br>
Queries second table (with scroll if second table limit is more than 10k) and for each result costruct a key and check if exists on map. And if it does - combine the documents with returned fields <br> 

**Results combined documents id is firstDocID|secondDocID and the type is firstDocType|secondDocType.**

#### Limitations
1. Only 2 Tables(indices/types) Join support
2. On "ON" you can only use "AND" connections.
3. You must use aliases for tables (acounts a ) 
4. On Where , don't combine decision trees combining both tables.
	For example 
	This will work:<br>  `WHERE  (a.key1>3 OR a.key1<0) AND (b.key2 > 4 OR  b.key2<-1)`<br>
	But we do not support  this:<br>
		`WHERE  (a.key1>3 OR b.key2<0) AND (a.key1 > 4 OR  b.key2<-1)`
5. No group by and order by support for result
6. Limit is allowed without offsets.

#### Hints
###### To use hints use them as comment , you can use multiple hints one after another with separate comments
1. USE_NESTED_LOOPS / USE_NL
 * use this hint if you want to make sure the join will be with nested loops implementation.<br>
 if not all combined conditions are EQUAL it will use Nested Loops anyway
 * example <br>
   ```sql
   SELECT /*! USE_NL */ c.gender , h.name,h.words FROM got/chars c JOIN %s/house h ON h.name = c.house
   ```

2. HASH_WITH_TERMS_FILTER
 * use this Optimization if the 2nd table have lot of values and 1st table has low amount of unique values on join conditions
 * works only on HASH JOIN implementaion
 * example (will query elastic for dogs with filter age>1 and holdersName in (names from first fetch) ) <br>
   ```sql
   SELECT /*! HASH_WITH_TERMS_FILTER*/ a.firstname ,a.lastname , a.gender ,d.name  FROM people a JOIN dog d on d.holdersName = a.firstname WHERE  (a.age > 10 OR a.balance > 2000) AND d.age > 1 
   ```
3. JOIN_TABLES_LIMIT(firstLimit,secondLimit) deafult is 200,200
 * use this hint to limit results fetched from each table, use null for no limit. <br>
   if you want to limit combined results , just use LIMIT on query.
 * example (will only fetch 2 houses ) <br> 
   ```sql
   select /*! JOIN_TABLES_LIMIT(null,2) */ c.name.firstname,c.parents.father , h.name,h.words from got/char c JOIN got/house h 
   ```
4. NL_MULTISEARCH_SIZE(multiSize) , default is 100<br> 
 * use this hint to change the multisearch size on nested loops query. read about multisearch ![here](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-multi-search.html)  
 * works only on NL , you can combine it with USE_NL hint
 * example:<br>
   ```sql
   SELECT /*! USE_NL*/ /*! NL_MULTISEARCH_SIZE(2)*/ c.name.firstname,c.parents.father , h.name FROM got got/char c JOIN got/house h 
   ```

#### Example from UI
![join](https://cloud.githubusercontent.com/assets/2933669/9706301/ea995aec-54e9-11e5-97b7-a7615bc5c34a.png)