/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.pakddo.BasicCrawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.List;
import java.util.regex.Pattern;

import net.pakddo.UrlFeatureExtractor.Constants;
import net.pakddo.utils.ResultFileWriter;

import org.apache.http.Header;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class BasicCrawler extends WebCrawler {

        private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
                        + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

        /**
         * You should implement this function to specify whether the given url
         * should be crawled or not (based on your crawling logic).
         */
        @Override
        public boolean shouldVisit(WebURL url) {
                String href = url.getURL().toLowerCase();
                return !FILTERS.matcher(href).matches() && href.startsWith("http://www.ics.uci.edu/");
        }

        /**
         * This function is called when a page is fetched and ready to be processed
         * by your program.
         */
        @Override
        public void visit(Page page) {
                int docid = page.getWebURL().getDocid();
                String url = page.getWebURL().getURL();
                String domain = page.getWebURL().getDomain();
                String path = page.getWebURL().getPath();
                String subDomain = page.getWebURL().getSubDomain();
                String parentUrl = page.getWebURL().getParentUrl();
                String anchor = page.getWebURL().getAnchor();

                System.out.println("Docid: " + docid);
                System.out.println("URL: " + url);
                System.out.println("Domain: '" + domain + "'");
                System.out.println("Sub-domain: '" + subDomain + "'");
                System.out.println("Path: '" + path + "'");
                System.out.println("Parent page: " + parentUrl);
                System.out.println("Anchor text: " + anchor);
                
                StringBuilder contentInfo = new StringBuilder();
                contentInfo.append("Docid: " + docid + "\r\n");
                contentInfo.append("URL: " + url + "\r\n");
                contentInfo.append("Domain: '" + domain + "'" + "\r\n");
                contentInfo.append("Sub-domain: '" + subDomain + "'" + "\r\n");
                contentInfo.append("Path: '" + path + "'" + "\r\n");
                contentInfo.append("Parent page: " + parentUrl + "\r\n");
                contentInfo.append("Anchor text: " + anchor + "\r\n");
                contentInfo.append("================================\r\n");
                
                Header[] responseHeaders = page.getFetchResponseHeaders();
                StringBuilder contentHeaders = new StringBuilder();
                if (responseHeaders != null) {
                        System.out.println("Response headers:");
                        for (Header header : responseHeaders) {
                                System.out.println("\t" + header.getName() + ": " + header.getValue());
                                contentHeaders.append("\t" + header.getName() + ": " + header.getValue()+"\r\n");
                        }
                        contentHeaders.append("================================\r\n");
                }
                
                if (page.getParseData() instanceof HtmlParseData) {
                        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                        String text = htmlParseData.getText();
                        String html = htmlParseData.getHtml();
                        List<WebURL> links = htmlParseData.getOutgoingUrls();

                        //CrawlingResult record
                        ResultFileWriter resultFileWriter = new ResultFileWriter(Constants.crawlOutputUrlFilePath, domain);
                        StringBuilder temp = new StringBuilder();
                        temp.append(contentInfo);
                        temp.append(contentHeaders);
                        temp.append(html);
                        resultFileWriter.writeFile(temp);
                        
                        System.out.println("Text length: " + text.length());
                        System.out.println("Html length: " + html.length());
                        System.out.println("Number of outgoing links: " + links.size());
                        
                }

               
                
                System.out.println("=============");
        }
}