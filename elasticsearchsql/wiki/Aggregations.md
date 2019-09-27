## Elasticsearch-sql Aggregations

We support several elasticsearch aggregations
#### Metrics
* min
* max
* sum
* count
* avg
* stats
* percentiles
* extended_stats <br>
  just use like this on numeric fields: 
  ```sql
  SELECT stats(age) FROM account
  ```
* scripted_metric (read below on how to use)



#### buckets
1. terms aggregation 
 * Use group by fieldName , you can also put multiple fields
 * examples<br>
   ```sql
   SELECT COUNT(*) FROM account GROUP BY gender 
   SELECT COUNT(*) FROM account GROUP BY gender, age
   ```
2. multiple aggregations
 * Use group by (fieldName),(fieldName, fieldName)
 * Each field in parenthesis is given its own aggregation
 * Each list of fields in parenthesis is its own aggregation with sub aggregations
 * examples<br>
   ```sql
   SELECT * FROM account GROUP BY (gender),(age)
   SELECT * FROM account GROUP BY (gender, state),(age)
   SELECT * FROM account GROUP BY (gender, state, age),(state),(age)
   ```
3. range aggregation
 * put fieldName followed by your ranges<br>
 * example , if you want to range: age with groups 20-25,25-30,30-35,35-40<br>
   ```sql
   SELECT COUNT(age) FROM bank GROUP BY range(age, 20,25,30,35,40)
   ```
4. date histogram aggregation
 * put fieldName and interval <br>
 * alias is optional
 * example<br>
   ```sql
   SELECT online FROM online GROUP BY date_histogram(field='insert_time','interval'='1d','alias'='yourAlias','extended_bounds'='{"min":"1547083500000","max":"1547343000000"}',format='epoch_millis')
   ```
5. date range aggregation
 * put your fieldName and special intervals with format <br>
 * alias is optional
 * example<br>
   ```sql
   SELECT online FROM online GROUP BY date_range('alias'='yourAlias',field='insert_time','format'='yyyy-MM-dd' ,'2014-08-18','2014-08-17','now-8d','now-7d','now-6d','now')
   ```
  


#### Scripted metric example:
 You can write your own metric agg!<br>
 Works only when enabled on elastic configuration. read more about it  [here](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-scripted-metric-aggregation.html)<br>
Syntax:<br>
```sql
SELECT scripted_metric('map_script'='yourMapScript','init_script'='yourInitScript','combine_script'='yourCombineScript','reduce_script'='yourReduceScript') FROM index
```
You can use x_script_file or x_script_id if you usually store them. <br>
You must provide map_script. <br>

**example: CONCAT string implementation**
![image](https://cloud.githubusercontent.com/assets/2933669/10862642/89acbc7e-7fb9-11e5-9268-cc00c2a03c4d.png)
```sql
select scripted_metric('init_script' = '_agg["concat"]=[] ', 'map_script'='_agg.concat.add(doc["name.firstname"].value)' , 'combine_script'='return _agg.concat.join(";");', 'reduce_script'='_aggs.removeAll(""); return _aggs.join(";")') as all_characters from got/chars
```
