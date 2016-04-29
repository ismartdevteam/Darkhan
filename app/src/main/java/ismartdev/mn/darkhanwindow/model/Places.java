package ismartdev.mn.darkhanwindow.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Places {
    @DatabaseField(uniqueIndex = true)
    private int id;
    @DatabaseField
    private String post_date;
    @DatabaseField
    private String post_content;
    @DatabaseField
    private String post_title;
    @DatabaseField
    private String item_phone;
    @DatabaseField
    private String item_address;
    @DatabaseField
    private String thumbnail_id;
    @DatabaseField
    private String detail_images;
    @DatabaseField
    private int comment_count;
    @DatabaseField
    private String comment_status;
    @DatabaseField
    private double lng;
    @DatabaseField
    private double lat;
    @DatabaseField
    private double rating_average;
    @DatabaseField
    private int rating_count;
    @DatabaseField
    private double distance;
    @DatabaseField
    private String videoID;
    @DatabaseField
    private String website;
    @DatabaseField
    private String email;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }


    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public String getComment_status() {
        return comment_status;
    }

    public void setComment_status(String comment_status) {
        this.comment_status = comment_status;
    }

    public String getDetail_images() {
        return detail_images;
    }

    public void setDetail_images(String detail_images) {
        this.detail_images = detail_images;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem_address() {
        return item_address;
    }

    public void setItem_address(String item_address) {
        this.item_address = item_address;
    }

    public String getItem_phone() {
        return item_phone;
    }

    public void setItem_phone(String item_phone) {
        this.item_phone = item_phone;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public double getRating_average() {
        return rating_average;
    }

    public void setRating_average(double rating_average) {
        this.rating_average = rating_average;
    }

    public int getRating_count() {
        return rating_count;
    }

    public void setRating_count(int rating_count) {
        this.rating_count = rating_count;
    }

    public String getThumbnail_id() {
        return thumbnail_id;
    }

    public void setThumbnail_id(String thumbnail_id) {
        this.thumbnail_id = thumbnail_id;
    }
}
