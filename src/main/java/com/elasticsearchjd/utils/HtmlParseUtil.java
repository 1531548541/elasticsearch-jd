package com.elasticsearchjd.utils;

import com.elasticsearchjd.entity.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 爬数据
 */
public class HtmlParseUtil {

    public static void main(String[] args) throws IOException {
        String url="https://search.jd.com/Search?keyword=java&enc=utf-8";
        Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 5.1; zh-CN) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/535.12").timeout(30000).get();
        System.out.println(document);
        Element element = document.getElementById("J_goodsList");
        Elements li = element.getElementsByTag("li");
        for (int i = 0; i<li.size(); i++) {
            String img = li.get(i).getElementsByTag("img").attr("data-lazy-img");
            String price = Arrays.asList(li.get(i).getElementsByClass("p-price").eq(0).text().split("￥")).get(1);
            String title = li.get(i).getElementsByClass("p-name").eq(0).text();
            System.out.println(img);
            System.out.println(price);
            System.out.println(title);
        }

    }

    /**
     * 根据商品name，爬数据
     * @param keyword
     * @return
     */
    public static List<Product> parseJD(String keyword) throws IOException {
        String url="https://search.jd.com/Search?keyword="+keyword+"&enc=utf-8";
        Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 5.1; zh-CN) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/22.0.1229.79 Safari/535.12").timeout(30000).get();
        Element element = document.getElementById("J_goodsList");
        Elements li = element.getElementsByTag("li");
        List<Product> list=new ArrayList<>();
        for (int i = 0; i<li.size(); i++) {
            String img = li.get(i).getElementsByTag("img").attr("data-lazy-img");
            String price = Arrays.asList(li.get(i).getElementsByClass("p-price").eq(0).text().split("￥")).get(1);
            String name = li.get(i).getElementsByClass("p-name").eq(0).text();
            System.out.println(img);
            System.out.println(price);
            System.out.println(name);
            Product product = new Product();
            product.setImg(img);
            product.setName(name);
            product.setPrice(price);
            list.add(product);
        }
        return list;
    }
}
