## Hint is a known SQL feature 
**the syntax for hints is adding it on comment before the fields after the select keyword.**
```sql
select /*! YOUR_HINT*/ yourField from yourIndex
```

### Some of the Available hints:
* IGNORE_UNAVAILABLE -  read about it [here](https://www.elastic.co/guide/en/elasticsearch/reference/current/multi-index.html)<br>
```sql
SELECT /*! IGNORE_UNAVAILABLE */ * FROM index1,index2 
```
* DOCS_WITH_AGGREGATION(arg0, arg1)  
Use it if you want docs to return on aggregation query.  
  * 0 Arguments: ```from``` set to ```0``` and ```size``` set to ```0```
  * 1 Arguments: ```from``` set to ```0``` and ```size``` set from ```arg0```
  * 2 Arguments: ```from``` set from ```arg0``` and ```size``` set from ```arg1```

Examples:  
  * ```{from: 0, size: 0}```  
    ```sql
    SELECT /*! DOCS_WITH_AGGREGATION() */ count(*) from account
    ```
  * ```{from: 0, size: 10}```  
    ```sql
    SELECT /*! DOCS_WITH_AGGREGATION(10) */ count(*) from account
    ```
  * ```{from: 10, size: 20}```  
    ```sql
    SELECT /*! DOCS_WITH_AGGREGATION(10, 20) */ count(*) from account
    ```
