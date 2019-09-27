# Welcome to the elasticsearch-sql wiki!

With this plugin you can query elasticsearch using familiar SQL syntax. 
You can also use ES functions in SQL.

### There are two ways to use this plugin:

1. Using the rest api <br>
 ````
 http://localhost:9200/_sql?sql=select * from indexName limit 10
 ````
<br>
2. Web UI available at http://localhost:9200/_plugin/sql
<br>
<img src="https://cloud.githubusercontent.com/assets/9518816/5555009/ebe4b53c-8c93-11e4-88ad-96d805cc698f.png" alt="Web frontend overview" width ="75%" height = "55%"/>

### Guides:
* [[Installation Guide|Installation Guide]]
* [[Building and testing Guide|Building and Testing Guide]]

### Features:
* [[Basic Queries And Conditions|Basic Queries And Conditions]]
* [[Geographic queries|Geographic Queries]]
* [[Aggregations|Aggregations]]
* [[Beyond SQL Features|Beyond SQL Features]]
* [[Scan and scroll|Scroll]]
* [[Limited join support|Join]]
* [[Show Commands|Show Commands]]
* [[Script Fields|Script Fields]]
* [[NestedTypes support|NestedTypes queries]]
* [[Union & Minus support|Union and Minus]]