# Installation Guide

Versions
------------

| elasticsearch version | latest version | remarks                        | branch       |
| --------------------- | -------------  | -----------------------------  | ------------ |
| 6.x	                | 6.1.1.1        |                                | master       |
| 5.x	                | 5.6.5.1        | delete commands not supported  | elastic5.6.5 |
| 2.1.0                 | 2.1.0          | delete commands not supported  | elastic2.1   |
| 2.0.0                 | 2.0.2          | delete commands not supported  | elastic2.0   |
| 1.X                   | 1.4.7          | tested against elastic 1.4-1.6 | elastic1.x   |

### Elasticsearch 1.X
````
./bin/plugin -u https://github.com/NLPchina/elasticsearch-sql/releases/download/1.4.7/elasticsearch-sql-1.4.7.zip --install sql
````
### Elasticsearch 2.0.0
````
./bin/plugin install https://github.com/NLPchina/elasticsearch-sql/releases/download/2.0.2/elasticsearch-sql-2.0.2.zip 
````
### Elasticsearch 2.1.0
````
./bin/plugin install https://github.com/NLPchina/elasticsearch-sql/releases/download/2.1.0/elasticsearch-sql-2.1.0.zip 
````
After doing this, you need to restart the Elasticsearch server. Otherwise you may get errors like `Invalid index name [sql], must not start with '']; ","status":400}`.


If you are having trouble with direct install you can always download the zip file for your version
from the link
for example:
https://github.com/NLPchina/elasticsearch-sql/releases/download/1.4.7/elasticsearch-sql-1.4.7.zip

save it to your computer and run:
````
./bin/plugin -u file:///home/yourFolder/elasticsearch-sql-1.4.7.zip --install sql
````

### Elasticsearch 5.x/6.x
+ run
````
./bin/elasticsearch-plugin install file:///home/yourFolder/elasticsearch-sql-x.x.x.x.zip
````
+ Use node to run site / just click on index.html (make sure to enable cors on elasticsearch.yml)
1.  download [es-sql-site-standalone.zip](https://github.com/NLPchina/elasticsearch-sql/releases/download/5.4.1.0/es-sql-site-standalone.zip) and unzip it
2.  install node.js if you are not having one
3.  run the following commands and then visit http://yourHost:8080  (You can change port under site_configuration.json)
```sh
> cd site-server
> npm install express --save
> node node-server.js
```
+ Use [elasticsearch sql site chrome extension](https://github.com/shi-yuan/elasticsearch-sql-site-chrome) (make sure to enable cors on elasticsearch.yml)