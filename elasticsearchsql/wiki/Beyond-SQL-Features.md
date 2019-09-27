### Some more features using ElasticSearch capabilities
1. ES TopHits
2. ES MISSING
3. ES STATS
4. ES EXTENDED_STATS
5. ES PERCENTILES
4. ES TERMS/TERM
5. ES IDS syntax: `IDS_QUERY(type, ids..)`
6. ES SCRIPTED_METRIC (read about it on Aggregations page)
7. ES QUERY_STRING

#### examples
```sql
SELECT * FROM account where nickname is not missing
SELECT * FROM account where nickname is missing
SELECT * FROM account where name = IN_TERMS(hattie,alis)
SELECT * FROM account where age = TERM(4)
SELECT * FROM account where _id = IDS_QUERY(account,1,2,5)
SELECT stats(age) FROM account
SELECT extended_stats(age) FROM account
SELECT percentiles(age) FROM accounts
SELECT address FROM bank where q=query('address:880 Holmes Lane')
SELECT * FROM dog where dog_name = REGEXP_QUERY('sn.*', 'INTERSECTION|COMPLEMENT|EMPTY', 10000)
```