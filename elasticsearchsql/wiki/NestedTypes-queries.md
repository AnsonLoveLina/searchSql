## Nested Types
Read about NestedTypes and what they are good for [here](https://www.elastic.co/guide/en/elasticsearch/reference/2.1/nested.html)

From 1.4.7/2.0.2/2.1.0 version of elasticsearch-sql we have support for using nestedTypes.

We are supporting queries and aggregations!
### Query nested fields
 * Simple Query (one field)<br>
In order to query a nested field all you need to do is add the "nested" function on the field.<br> 
```sql 
SELECT * FROM myIndex where nested(comments.message)='hello'
```
 If you want to specify the path implicitly use:<br>
 ```sql 
SELECT * FROM myIndex where nested(comments.message,'comments')='hello'
```

* Complex Query (more than one field)<br>
  The syntax is simply `nested('nested_path',where condition)` for example:<br>
```sql
SELECT * FROM myIndex where nested('comments', comments.message = 'hello' and comments.likes > 3)
```
   `nested('nested_path',where condition,inner_hits)` for example:<br>
```sql
SELECT * FROM myIndex where nested('comments', comments.message = 'hello' and comments.likes > 3,'{"from":0}')
```

### Aggregate on nested fields
* **Simple term aggregation** <br>
Wrap the string field with nested function
```sql
SELECT count(*) as numOfComments FROM myIndex where nested(comments.age) > now-1d GROUP BY nested(comments.author)
```
* **Metric aggregations**<br>
Just wrap the nested field with nested function
```sql
SELECT sum(nested(comments.likes)) as sumOfInnerLikes FROM myIndex
```
* **Buckets aggregation**<br>
 Add the **'nested'** option like this:
the value  should be the nested path.
```sql
select count(*) from index group by date_histogram('field'='message.date','interval'='1d','alias'='day', 'nested' ='message')
```
### Reverse nested aggregations
Read about the need for reverse-nested aggregation [here](https://www.elastic.co/guide/en/elasticsearch/reference/2.0/search-aggregations-bucket-reverse-nested-aggregation.html)
use it like you use nested aggregation<br>
Be sure you know where to jump <br>
examples:
* jump back to root
```sql
SELECT sum(reverse_nested(someField)) alias FROM index GROUP BY nested(message.info)
```
 * jump to another nested object which is inside your current nested path
```sql
SELECT sum(reverse_nested(message.otherField,'message')) alias FROM index GROUP BY nested(message.info)
```
 * jump to another nested object which is outside your current nested path (jumps back to root and do a nested agg)
```sql
SELECT sum(reverse_nested(otherNested.otherField,'~otherNested')) alias FROM index GROUP BY nested(message.info)
```

* use it on buckets with the 'reverse_nested' on which you should add the path
```sql
SELECT COUNT(*) FROM index GROUP BY  nested(message.info),histogram('field'='comment.likes','reverse_nested'='~comment','interval'='2' , 'alias' = 'someAlias' )
```

