package com.elasticsearchjd.controller;

import com.elasticsearchjd.service.ProductService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 将爬取的数据存入es中
     * @param keyword
     * @return
     * @throws IOException
     */
    @GetMapping("/parse/{keyword}")
    @ResponseBody
    public boolean parse(@PathVariable String keyword) throws IOException {
        return productService.parseProdcut(keyword);
    }

    /**
     * 分页搜索
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/searchPage/{keyword}/{page}/{pageSize}")
    @ResponseBody
    public List<Map<String,Object>> searchPage(@PathVariable("keyword") String keyword,
                                               @PathVariable("page") int page,
                                               @PathVariable("pageSize") int pageSize) throws IOException {
        return productService.searchPage(keyword, page, pageSize);
    }
}
