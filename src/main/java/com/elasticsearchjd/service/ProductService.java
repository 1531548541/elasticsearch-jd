package com.elasticsearchjd.service;

import com.alibaba.fastjson.JSON;
import com.elasticsearchjd.entity.Product;
import com.elasticsearchjd.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ProductService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 将爬取的数据存入es中
     * @param keyword
     * @return
     * @throws IOException
     */
    public boolean parseProdcut(String keyword) throws IOException {
        List<Product> productList = HtmlParseUtil.parseJD(keyword);
        //存入es
        BulkRequest bulkRequest=new BulkRequest();
        bulkRequest.timeout("2m");
        for (Product product : productList) {
            bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(product), XContentType.JSON));
        }
        BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !response.hasFailures();
    }

    /**
     * 分页搜索
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    public List<Map<String,Object>> searchPage(String keyword, int page, int pageSize) throws IOException {
        //条件搜索
        SearchRequest searchRequest=new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(page);
        sourceBuilder.size(pageSize);

        //精准匹配
        TermQueryBuilder termQuery = QueryBuilders.termQuery("name", keyword);
        sourceBuilder.query(termQuery);

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.requireFieldMatch(false); //多个高亮
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析结果
        List<Map<String,Object>> list=new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            //解析高亮
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField name = highlightFields.get("name");//高亮字段的值
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//原来的值
            if(name!=null){
                String newName="";
                Text[] fragments = name.fragments();
                for (Text fragment : fragments) {
                    newName+=fragment;
                }
                sourceAsMap.put("name",newName);
            }
            list.add(sourceAsMap);
        }
        return list;
    }
}
