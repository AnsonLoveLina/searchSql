## Geographic Queries
Elasticsearch-sql supports all elastic spatial filters , we also support the geo hash grid aggregations.

### Filters
##### Use them in WHERE conditions
1. **Bounding box** filter (works on points)
 * [elastic api link](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-bounding-box-filter.html)
 * Syntax <br>
`GEO_BOUNDING_BOX(fieldName,topLeftLongitude,topLeftLatitude,bottomRightLongitude,bottomRightLatitude)`
 * example <br>
   ```sql
   SELECT * FROM location WHERE GEO_BOUNDING_BOX(center,100.0,1.0,101,0.0)
   ```
2. **Distance** filter (works on points)
 * [elastic api link](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-distance-filter.html)
 * Syntax <br>
`GEO_DISTANCE(fieldName,distance,fromLongitude,fromLatitude)`
 * example <br>
   ```sql
   SELECT * FROM location WHERE GEO_DISTANCE(center,'1km',100.5,0.5)
   ```
3. **Range Distance** filter (works on points)
 * [elastic api link](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-distance-range-filter.html)
 * Syntax <br>
`GEO_DISTANCE_RANGE(fieldName,distanceFrom,distanceTo,fromLongitude,fromLatitude)`
 * example <br>
   ```sql
   SELECT * FROM location WHERE GEO_DISTANCE_RANGE(center,'1m','1km',100.5,0.50001)
   ```
4. **Polygon** filter (works on points)
 * [elastic api link](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-polygon-filter.html)
 * Syntax <br>
`GEO_POLYGON(fieldName,lon1,lat1,lon2,lat2,lon3,lat3,...)`
 * example <br>
   ```sql
   SELECT * FROM location WHERE GEO_POLYGON(center,100,0,100.5,2,101.0,0)
   ```
5. **GeoShape Intersects** filter (works on geoshapes)
 * [elastic api link](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geo-shape-filter.html)
 * Syntax - We use [WKT](https://en.wikipedia.org/wiki/Well-known_text) to represent shapes on query  <br>
GEO_INTERSECTS(fieldName,'WKT')
 * example <br>
   ```sql
   SELECT * FROM location WHERE GEO_INTERSECTS(place,'POLYGON ((102 2, 103 2, 103 3, 102 3, 102 2))
   ```
6. **GeoCell** filter (works on points)
 * [elastic api link](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-geohash-cell-filter.html)
 * Syntax <br>
GEO_CELL(fieldName,longitude,latitude,precision,neighbors(optional))
 * example <br>
   ```sql
   SELECT * FROM locations WHERE GEO_CELL(center,100.5,0.50001,7)
   SELECT * FROM locations WHERE GEO_CELL(center,100.5,0.50001,7,true)
   ```

### Aggregation
GeoHash aggregation support
* [elastic api](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-geohashgrid-aggregation.html)
* Syntax <br>
GROUP BY geohash_grid(field=fieldName,precision=requiredPrecision,'alias'='yourAlias')
* alias is optional
* example - will show you geohash to count <br>
  ```sql
  SELECT count(*) FROM location GROUP BY geohash_grid(field='center',precision=5)
  ```
