## Elasticsearch-sql scroll
In elasticsearch-sql you can use the scan & scroll feature to retrieve large numbers of documents from Elasticsearch efficiently.
Read about Scan and scroll on [elasticsearch official guide](https://www.elastic.co/guide/en/elasticsearch/guide/current/scan-scroll.html)
### How to use?
1. If you want to use scroll on your queries simply add this hint <br>
  ```sql
  SELECT /*! USE_SCROLL*/ firstname , balance FROM accounts
  ```
 This will cause a scroll with default values 50,60k to start <br>
 You can specify the values like this:
 ```sql
 SELECT /*! USE_SCROLL(100,30000)*/ firstname , balance FROM accounts
 ``` 

 The first value is number of documents per shard in each scroll (so in default if you have 3 shards you'll get 150 per fetch) <br>
 The second value is the how long keep the search context open (in milliseconds - so default is 1minute)
2. If you are using the Web UI you can simply check the always scroll and change the size of scroll and it will add the hint to your query automatically <br>
On first fetch you'll see the number of results your going to get, and the scroll buttons will be enabled.
![image](https://cloud.githubusercontent.com/assets/2933669/11022646/8e9f418e-866c-11e5-84cb-8db752bfc22a.png)
Clicking on this button will get you the next scroll.
![image](https://cloud.githubusercontent.com/assets/2933669/11022666/d7ca863e-866c-11e5-83f0-cceb5bb90a71.png)

Clicking on this button will scroll you till the end (one scroll at a time)
![image](https://cloud.githubusercontent.com/assets/2933669/11022671/f855ac4e-866c-11e5-9616-87ae9bd35803.png)


### Need to know
 * The combination of limit and use_scroll hint is not supported
 * On scan & scroll the first result only returns a scrollId and the total number of hits matching the query.