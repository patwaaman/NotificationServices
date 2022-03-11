package com.example.notificationService.Services;

import com.example.notificationService.Constant.Const;
import com.example.notificationService.Dto.Request.StartEndTime;
import com.example.notificationService.Dto.Request.TextSearchRequest;
import com.example.notificationService.Entities.ElasticSearch;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElasticSearchService {

    Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    private String SMS_INDEX = Const.ELASTICSEARCH_INDEX;
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public ElasticSearchService(final ElasticsearchOperations elasticsearchOperations) {
        super();
        this.elasticsearchOperations = elasticsearchOperations;
    }


    public void createElasticSearchIndex(ElasticSearch elasticSearch) {
        logger.info("Creating Elastic Index for {}", elasticSearch);
        IndexQuery indexQuery = new IndexQueryBuilder().withId(elasticSearch.getId()).withObject(elasticSearch).build();
        String documentId = elasticsearchOperations.index(indexQuery, IndexCoordinates.of(SMS_INDEX));
        logger.info("DocumentID = " + documentId);
    }


    public ResponseEntity<Map<String, Object>> findByStartEndTime( StartEndTime startEndTime ){
        logger.info("ElasticSearch for StartEndTime : {}", startEndTime);

        /*------ Start EndTime Validation Is Left -----*/
        /*---- Create search Criteria -------*/
        Criteria criteria = new Criteria("time")
                .greaterThanEqual(startEndTime.getStartTime())
                .lessThanEqual(startEndTime.getEndTime());

        Query searchQuery = new CriteriaQuery(criteria).setPageable(PageRequest.of(startEndTime.getPageNo(), startEndTime.getPageSize()).withSort(Sort.by("createdAt")));

        /*-------- Execute search -----------*/
        SearchHits<ElasticSearch> startEndTimeHits = elasticsearchOperations.search(searchQuery,
                        ElasticSearch.class,
                        IndexCoordinates.of(SMS_INDEX));


        /*---- Map searchHits to Elasticsearch list.------*/
        List<ElasticSearch> Hits = new ArrayList<>();
        startEndTimeHits.forEach(searchHits->{
            Hits.add(searchHits.getContent());
        });

        return new ResponseEntity<>(ResponseBuilder(startEndTime, Hits, String.valueOf(startEndTimeHits.getTotalHits())), HttpStatus.OK);
    }


    public ResponseEntity<Map<String,Object>> findSmsContainingText( TextSearchRequest textSearchRequest){
        logger.info("Elasticsearch for TextSearch : {}",textSearchRequest);
        /*---- Create search query on Phrases -------*/
        QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("message", textSearchRequest.getText().trim());

        Query searchQuery = new NativeSearchQueryBuilder().withPageable(PageRequest.of(textSearchRequest.getPageNo(), textSearchRequest.getPageSize()).withSort(Sort.by("createdAt")))
                .withQuery(queryBuilder)
                .build();

        /*-------- Execute search -----------*/
        SearchHits<ElasticSearch> textSearchHits = elasticsearchOperations.search(searchQuery,
                        ElasticSearch.class,
                        IndexCoordinates.of(SMS_INDEX));

        logger.info("total Hits : {}",String.valueOf(textSearchHits.getTotalHits()));
        /*---- Map searchHits to Elasticsearch list.------*/
        List<ElasticSearch> Hits = new ArrayList<>();
        textSearchHits.forEach(searchHits->{
            Hits.add(searchHits.getContent());
        });

        return new ResponseEntity<>(ResponseBuilder(textSearchRequest, Hits, String.valueOf(textSearchHits.getTotalHits())), HttpStatus.OK);
    }

    private Map<String, Object> ResponseBuilder( Object request, Object data, String TotalHits){
        Map<String, Object> response = new HashMap<>();
        response.put("SearchRequest",request);
        response.put("TotalHits", TotalHits);
        response.put("Results", data);

        return response;
    }



}
