package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.ArrayList;

import edu.stanford.nlp.trees.GrammaticalStructure;

public class FakeNewsChallengeObject implements Comparable{
	
	private int id;
	private int articleId;
	private String stance;
	private String article;
	private String header;
	private String headerLemmas;
	private String articleLemmas;
	private ArrayList<GrammaticalStructure> gsArticle;
	private ArrayList<GrammaticalStructure> gsHeader;
	private String headerWithoutStopwords;
	private String articleWithoutStopwords;
	private String headerLemmasWS;
	private String articleLemmasWS;
	
	private int orderId;
	
	
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getId() {
		return id;
	}
	public void setArticleGS(ArrayList<GrammaticalStructure> gs){
		this.gsArticle = gs;
	}
	public void setHeaderGS(ArrayList<GrammaticalStructure> gs){
		this.gsHeader = gs;
	}
	public ArrayList<GrammaticalStructure> getArticleGS(){
		return gsArticle;
	}
	public ArrayList<GrammaticalStructure> getHeaderGS(){
		return gsHeader;
	}
	
	public String getHeaderLemmas() {
		return headerLemmas;
	}
	public void setHeaderLemmas(String headerLemmas) {
		this.headerLemmas = headerLemmas;
	}
	
	
	public String getHeaderWithoutStopwords() {
		return headerWithoutStopwords;
	}
	public void setHeaderWithoutStopwords(String headerWithoutStopwords) {
		this.headerWithoutStopwords = headerWithoutStopwords;
	}
	public String getArticleWithoutStopwords() {
		return articleWithoutStopwords;
	}
	public void setArticleWithoutStopwords(String articleWithoutStopwords) {
		this.articleWithoutStopwords = articleWithoutStopwords;
	}
	
	
	
//	public String getHeaderLemmasWS() {
//		return headerLemmasWS;
//	}
//	public void setHeaderLemmasWS(String headerLemmasWS) {
//		this.headerLemmasWS = headerLemmasWS;
//	}
//	public String getArticleLemmasWS() {
//		return articleLemmasWS;
//	}
//	public void setArticleLemmasWS(String articleLemmasWS) {
//		this.articleLemmasWS = articleLemmasWS;
//	}
	
	
	
	
	
	
	
	
	public String getArticleLemmas() {
		return articleLemmas;
	}
	public void setArticleLemmas(String articleLemmas) {
		this.articleLemmas = articleLemmas;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getArticleId() {
		return articleId;
	}
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	public String getStance() {
		return stance;
	}
	public void setStance(String stance) {
		this.stance = stance;
	}
	public String getArticle() {
		return article;
	}
	public void setArticle(String article) {
		this.article = article;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public FakeNewsChallengeObject(int id, int articleId, String stance, String article, String header) {
		super();
		this.id = id;
		this.articleId = articleId;
		this.stance = stance;
		this.article = article;
		this.header = header;
	}
	public FakeNewsChallengeObject(int id, int articleId, String stance, String article, String header, String headerLemmas, String articleLemmas) {
		super();
		this.id = id;
		this.articleId = articleId;
		this.stance = stance;
		this.article = article;
		this.header = header;
		this.headerLemmas = headerLemmas;
		this.articleLemmas = articleLemmas;
	}
	public FakeNewsChallengeObject() {
		super();
	}
	
	@Override
	public int compareTo(Object o) {
		FakeNewsChallengeObject o2 = (FakeNewsChallengeObject) o ;
		return orderId-o2.orderId;
	}

	

}
