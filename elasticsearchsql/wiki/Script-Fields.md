## Script Field
Read about how to turn this option on and more about it [here](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-script-fields.html)



### Syntax And Examples
1. for simple binary operation script (works with fields and literals) just use: 
```sql
select field1 + field2 from indexName where.. 
select field1 - 3 from indexName where.. 
```
or in aggregation <br>
```sql
select avg(insertion_time - recieved_time) from dataIndex 
```
2. for more complex script: <br>
```sql
select script('fieldReturnName','yourGroovyScript') ,moreFields from indexName where ...
```
3. if you want to change the language use: <br>
```sql
select script('fieldReturnName','language','yourLangScript') 
```

**To escape ' just use \'**

#### Web UI support
 * simple binaryOperator script
![image](https://cloud.githubusercontent.com/assets/2933669/10697905/fb476c8e-79b6-11e5-929a-a0ac2e84956e.png)
 * Metric aggregation script
![image](https://cloud.githubusercontent.com/assets/2933669/10697770/3edd145e-79b6-11e5-8442-9da297599a75.png)
 * full select field script
![image](https://cloud.githubusercontent.com/assets/2933669/10680007/b6d7317a-7924-11e5-8046-c1df3e3b358a.png)