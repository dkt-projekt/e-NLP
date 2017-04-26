package de.dkt.eservices.erattlesnakenlp.linguistic;


public class FakeNewsChallengeItem {
	
	private int id;
	private int articleId;
	private String stance;
	private String article;
	private String header;
	private String headerLemmas;
	private String articleLemmas;
	public int getId() {
		return id;
	}
	public String getHeaderLemmas() {
		return headerLemmas;
	}
	public void setHeaderLemmas(String headerLemmas) {
		this.headerLemmas = headerLemmas;
	}
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
	public FakeNewsChallengeItem(int id, int articleId, String stance, String article, String header) {
		super();
		this.id = id;
		this.articleId = articleId;
		this.stance = stance;
		this.article = article;
		this.header = header;
	}
	public FakeNewsChallengeItem(int id, int articleId, String stance, String article, String header, String headerLemmas, String articleLemmas) {
		super();
		this.id = id;
		this.articleId = articleId;
		this.stance = stance;
		this.article = article;
		this.header = header;
	}
	public FakeNewsChallengeItem() {
		super();
	}

	

}
