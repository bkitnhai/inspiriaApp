package com.google.samples.apps.topeka.support_class;

/**
 * Created by Nguyen Hai on 1/14/2016.
 */
public class RowItem {
    private int imageId;
    private String title;

    public RowItem(int imageId, String title ) {
        this.imageId = imageId;
        this.title = title;
    }



    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n";
    }
}