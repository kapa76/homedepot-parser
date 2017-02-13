package ru.homedepot.Entity;

public class Item {
    private String imageUrl;
    private String description;
    private String modelName;
    private String rating;
    private String priceSpecial;
    private String price;

    public Item(String imageUrl, String description, String modelName, String rating, String priceSpecial, String price) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.modelName = modelName;
        this.rating = rating;
        this.priceSpecial = priceSpecial;
        this.price = price;
    }

    public String getKey() {
        return modelName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPriceSpecial() {
        return priceSpecial;
    }

    public void setPriceSpecial(String priceSpecial) {
        this.priceSpecial = priceSpecial;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String toString() {
        return  "Image url: " + imageUrl + "|" +
                "Description: " + description + "|" +
                "ModelName: " + modelName + "|" +
                "Rating: " + rating + "|" +
                "SpecialPrice: " + priceSpecial + "|" +
                "Price: " + price;
    }
}
