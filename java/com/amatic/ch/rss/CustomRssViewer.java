package com.amatic.ch.rss;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.amatic.ch.dto.SampleContent;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Guid;
import com.sun.syndication.feed.rss.Item;

public class CustomRssViewer extends AbstractRssFeedView {

    @Value("#{application['domain']}")
    String DOMAIN;

    @Value("#{application['brand']}")
    String BRAND;

    @Value("#{application['description']}")
    String DESCRIPTION;

    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Channel feed,
	    HttpServletRequest request) {

	feed.setTitle(BRAND);
	feed.setDescription(DESCRIPTION);
	feed.setLink("http://www." + DOMAIN);
	feed.setLanguage("es");
	feed.setCopyright("Copyright "
		+ Calendar.getInstance().get(Calendar.YEAR) + " " + BRAND);
	feed.setFeedType("rss_2.0");
	feed.setEncoding("UTF-8");
	super.buildFeedMetadata(model, feed, request);
    }

    @Override
    protected List<Item> buildFeedItems(Map<String, Object> model,
	    HttpServletRequest request, HttpServletResponse response)
	    throws Exception {

	@SuppressWarnings("unchecked")
	List<SampleContent> listContent = (List<SampleContent>) model
		.get("feedContent");
	List<Item> items = new ArrayList<Item>(listContent.size());

	for (SampleContent tempContent : listContent) {

	    Item item = new Item();
	    Content content = new Content();
	    content.setValue(tempContent.getSummary());
	    item.setContent(content);

	    item.setTitle(tempContent.getTitle());
	    item.setLink(tempContent.getUrl());
	    item.setAuthor(tempContent.getAuthor());
	    item.setComments(tempContent.getComments());
	    item.setCategories(tempContent.getCategories());
	    Description desc = new Description();
	    desc.setValue(tempContent.getDescription());
	    item.setDescription(desc);
	    item.setPubDate(tempContent.getCreatedDate());
	    Guid guid = new Guid();
	    guid.setPermaLink(false);
	    guid.setValue(tempContent.getGuid());
	    item.setGuid(guid);
	    items.add(item);
	}

	return items;
    }
}
