package org.elasticsearch.plugin.nlpcn;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.internal.InternalSearchHit;
import org.elasticsearch.search.internal.InternalSearchHits;
import org.nlpcn.es4sql.Util;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.query.multi.MultiQueryRequestBuilder;

import java.io.IOException;
import java.util.*;

/**
 * Created by Eliran on 21/8/2016.
 */
public class UnionExecutor implements ElasticHitsExecutor {

    private MultiQueryRequestBuilder multiQueryBuilder;
    private SearchHits results;
    private Client client;
    private int currentId;

    public UnionExecutor(Client client,MultiQueryRequestBuilder builder) {
        multiQueryBuilder = builder;
        client = client;
        currentId = 0;
    }

    @Override
    public void run() throws IOException, SqlParseException {
        SearchResponse firstResponse = this.multiQueryBuilder.getFirstSearchRequest().get();
        SearchHit[] hits = firstResponse.getHits().hits();
        List<InternalSearchHit> unionHits = new ArrayList<>(hits.length);
        fillInternalSearchHits(unionHits,hits,this.multiQueryBuilder.getFirstTableFieldToAlias());
        SearchResponse secondResponse = this.multiQueryBuilder.getSecondSearchRequest().get();
        fillInternalSearchHits(unionHits,secondResponse.getHits().hits(),this.multiQueryBuilder.getSecondTableFieldToAlias());
        int totalSize = unionHits.size();
        InternalSearchHit[] unionHitsArr = unionHits.toArray(new InternalSearchHit[totalSize]);
        this.results = new InternalSearchHits(unionHitsArr, totalSize,1.0f);
    }

    private void fillInternalSearchHits(List<InternalSearchHit> unionHits, SearchHit[] hits, Map<String, String> fieldNameToAlias) {
        for(SearchHit hit : hits){
            InternalSearchHit searchHit = new InternalSearchHit(currentId,hit.getId().toString(), new Text(hit.getType()), hit.fields());
            searchHit.sourceRef(hit.getSourceRef());
            searchHit.sourceAsMap().clear();
            Map<String, Object> sourceAsMap = hit.sourceAsMap();
            if(!fieldNameToAlias.isEmpty()){
                updateFieldNamesToAlias(sourceAsMap, fieldNameToAlias);
            }
            searchHit.sourceAsMap().putAll(sourceAsMap);
            currentId++;
            unionHits.add(searchHit);
        }
    }


    private void updateFieldNamesToAlias(Map<String, Object> sourceAsMap, Map<String, String> fieldNameToAlias) {
        for(Map.Entry<String,String> fieldToAlias : fieldNameToAlias.entrySet()){
            String fieldName = fieldToAlias.getKey();
            Object value = null;
            Map<String,Object> deleteFrom = null;
            if(fieldName.contains(".")){
                String[] split = fieldName.split("\\.");
                String[] path = Arrays.copyOf(split, split.length - 1);
                Object placeInMap = Util.searchPathInMap(sourceAsMap, path);
                if(placeInMap != null){
                    if(!Map.class.isAssignableFrom(placeInMap.getClass())){
                        continue;
                    }
                }
                deleteFrom = (Map<String,Object>) placeInMap;
                value = deleteFrom.get(split[split.length-1]);
            }
            else if(sourceAsMap.containsKey(fieldName)){
                value = sourceAsMap.get(fieldName);
                deleteFrom = sourceAsMap;
            }
            if(value!=null){
                sourceAsMap.put(fieldToAlias.getValue(),value);
                deleteFrom.remove(fieldName);
            }
        }
        Util.clearEmptyPaths(sourceAsMap);
    }

    @Override
    public SearchHits getHits() {
        return results;
    }
}
