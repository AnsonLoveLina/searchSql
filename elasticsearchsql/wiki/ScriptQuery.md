# Script Query
If you want to filter your documents according to scripts you can do it! <br>
read about it [here](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-script-query.html)

### how to use?
 * The syntax is script('yourScript') /  script('yourScript','param1'=value1,..,'paramN'=valueN)
 * examples
```sql
SELECT insert_time FROM elast*/online where script('doc["insert_time"].date.hourOfDay==16')
```
```sql
SELECT insert_time FROM elast*/online where script('doc[field].date.hourOfDay==x','x'=16,'field'='insert_time')
```
### Files or Indexed script support
**Importent! this feature only works on elastic2.x plugin versions.** <br>
Just add the 'script_type'='file' or 'script_type'='indexed' to your parameters <br><br>
Example:
```sql
SELECT insert_time FROM elast*/online where script('hour','x'=16,'field'='insert_time','script_type'='file')
```
