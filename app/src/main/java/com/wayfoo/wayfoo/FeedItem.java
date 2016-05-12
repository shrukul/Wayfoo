package com.wayfoo.wayfoo;



public class FeedItem {
	private String Place;
	private String title;
    private String Ratings;
    private String thumbnail;
    private String b;
    private String b2,RSum,RNum;

    public int getAvail() {
        return avail;
    }

    public void setAvail(int avail) {
        this.avail = avail;
    }

    private int avail;

    public String getDisName() {
        return disName;
    }

    private String disName;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPlace() {
        return Place;
    }
    public void setPlace(String Place) {
        this.Place = Place;
    }
    public String getRatings() {
        return Ratings;
    }
    public void setRatings(String Ratings) {
        this.Ratings = Ratings;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public String getFav() {
       return b;
    }
    public void setFav(String b) {
        this.b=b;
    }
    public String getID() {
        return b2;
    }
    public void setID(String b2) {
        this.b2=b2;
    }

    public void setDisName(String disName) {
        this.disName = disName;
    }

    public void setRSum(String RSum) {
        this.RSum = RSum;
    }

    public String getRSum() {
        return RSum;
    }

    public String getRNum() {
        return RNum;
    }

    public void setRNum(String RNum) {
        this.RNum = RNum;
    }
}