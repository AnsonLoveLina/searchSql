## Highlights
Read about highlights [here](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-highlighting.html)

**On 1.x version it will only work on queries so make sure you order by _score**

#### How to use?
 * put the field that you want to highlight on an hint <br>
  ``` /*! HIGHLIGHT(fieldName,option1 : value1, option2 : value2)*/ ```
 * Have more than one field? just add more hints
```sql
select /*! HIGHLIGHT(field1,pre_tags : ['<b>'], post_tags : ['</b>']  ) */ 
/*! HIGHLIGHT(field2,pre_tags : ['<b>'], post_tags : ['</b>']  ) */ * from myIndex
```
 * The options are:
  * boundary_chars
  * boundary_max_scan
  * force_source
  * fragmenter
  * fragment_offset
  * fragment_size
  * highlight_filter
  * matched_fields
  * no_match_size
  * num_of_fragments
  * order
  * phrase_limit
  * post_tage
  * pre_tags
  * require_field_match


