package edu.carleton.comp4601.model;

import java.io.Serializable;
import java.util.Map;

import edu.carleton.comp4601.analyzers.SentimentAnalyzer;

public class Review implements Serializable, Cloneable{

	private static final long serialVersionUID = 4L;
	private String pageid;
	private String userid;
	private String content;
	private String genre;
	private Float score;
	private Map<String, Integer> sentimentScores;
	private Sentiment sentiment;
	
	public Review(String id) {
		pageid = id;
	}
	
	public Review(String id, String userid, String content, float score) {
		this.pageid = id;
		this.userid = userid;
		this.content = content;
		this.score = score;
	}
	
	public String getId() {
		return userid+"-"+pageid;
	}
	
	public void setContent(String text) {
		this.content = text;
	}
	
	public String getContent() {
		return content;
	}
	
	public float getScore() {
		return score;
	}
	
	public Map<String, Integer> getSentimentScores() {
		return sentimentScores;
	}
	
	public void setSentimentScores(Map<String, Integer> scores) {
		sentimentScores = scores;
		sentiment = SentimentAnalyzer.calculateSentiment(scores);
	}
	
	public Sentiment getSentiment() {
		return sentiment;
	}
	
	public void setSentiment(Sentiment s) {
		sentiment = s;
	}
	
	public String getPageId() {
		return pageid;
	}
	
	public String getUserId() {
		return userid;
	}
	
	public String getCategory() {
		return genre;
	}
	
	public void setCategory(String s) {
		genre = s;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return userid+"-"+pageid+".html";
	}
}
