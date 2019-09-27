### Union & Minus queries
For your convenience we implemented union and minus queries.
We also implemented several strategies for how the minus takes place , you can choose strategy with hints.

To use Union & Minus your first query field names should be the same as the second query field names<br>
To achieve this you can use aliases if necessary <br>

#### Implementations
##### Union  - Nothing sophisticated 
Query first table for the limit on first query (default is 200) </br>
Query the second table for the limit on second query (default is 200) </br>
combine the results and send them to the client
##### Minus 
There are three implementations for minus queries:
 1. basic implementation <br>
on basic implementation we run the first query on elasticsearch and add it to a set (we remove duplicates)
after that we run the second query on elasticsearch.
for each result on second query , we check if it exists on first result set and if it does we remove it from the set.
And than we finally return the set as hits.
 2. scrolling <br>
 We use scrolling in order for better performance when second data set is large
 In order to use scrolling you need to add this hint:<br>
 ```sql 
 /*! MINUS_SCROLL_FETCH_AND_RESULT_LIMITS(maxFetchOnFirstTable,maxFetchOnSecondTable,docsFetchFromShardOnEachScroll) */
 ```
 How it work?
  * scroll on first table till fetch all / reach table limit.
  * remove duplicates and put it on set S
  * scroll on second table , for each scroll:
   * get result and try to remove items from the set S
  * return the set S as hits

 3. Scrolling and Terms optimization <br>
 <b> it is only available when only one field on minus </b>
 We use this optimization when both tables are large and when we want to keep our heap low<br>
 In order to use it you need to add 2 hints , the scroll hint and a new one:<br>
 ```sql
 /*! MINUS_SCROLL_FETCH_AND_RESULT_LIMITS(maxFetchOnFirstTable,maxFetchOnSecondTable,docsFetchFromShardOnEachScroll) */ 
/*! MINUS_USE_TERMS_OPTIMIZATION(true)*/
 ```
 How it work?
   * create set S
   * scroll on first table , for each scroll:
    * remove duplicates and put result on set S'
    * scroll on second table while adding <b> terms filter </b> on all results from S', for each scroll:
     * get result and try to remove items from the set S'
    * put results from S' to S
   * return set S as hits

### EXAMPLES

1. union example (with alias):
```sql
SELECT firstname FROM myIndex/account WHERE firstname = 'Amber'  
union all 
SELECT dog_name as firstname FROM myIndex/dog WHERE dog_name = 'rex'
```
2. minus examples:
 * simple
```sql
SELECT  pk FROM myIndex/systems WHERE system_name = 'A'
minus 
SELECT pk FROM myIndex/systems WHERE system_name = 'B'
```
 * two fields and aliases
```sql
SELECT  pk , letter  FROM myIndex/systems WHERE system_name = 'C'
 minus
SELECT myId as pk , myLetter as letter FROM myIndex/systems WHERE system_name = 'E'
```

* two fields and aliases and scrolling hint
```sql 
SELECT /*! MINUS_SCROLL_FETCH_AND_RESULT_LIMITS(100000,10000000,5000) */ 
 pk , letter  FROM myIndex/systems WHERE system_name = 'C'
 minus
SELECT myId as pk , myLetter as letter FROM myIndex/systems WHERE system_name = 'E'
```
* terms optimization
```sql 
SELECT /*! MINUS_SCROLL_FETCH_AND_RESULT_LIMITS(100000,10000000,5000) */ 
/*! MINUS_USE_TERMS_OPTIMIZATION(true)*/
 pk FROM myIndex/systems WHERE system_name = 'A'
minus 
SELECT pk FROM myIndex/systems WHERE system_name = 'B'
```
