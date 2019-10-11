## Elasticsearch-sql Jdbc

We support elasticsearch jdbc
#### aggs Buckets - jdbc Array
        "key2" : {
          "doc_count_error_upper_bound" : 0,
          "sum_other_doc_count" : 0,
          "buckets" : [
            {
              "key" : 13,
              "doc_count" : 2,
              "COUNT(*)" : {
                "value" : 2
              }
            },
            {
              "key" : 12,
              "doc_count" : 1,
              "COUNT(*)" : {
                "value" : 1
              }
            }
          ]
        }
        
对应jdbc代码

        Array key2 = resultSet1.getArray("key2"+org.nlpcn.es4sql.Util.BUCKS_NAME);
        ResultSet key2rs = key2.getResultSet();
        while(key2rs.next){
            String key = key2rs.getLong(org.nlpcn.es4sql.Util.KEY_NAME);//value=13,12
            String count = key2rs.getLong(org.nlpcn.es4sql.Util.COUNT_NAME);//value=2,1
            String value = key2rs.getLong("COUNT(*)");//value=2,1
        }