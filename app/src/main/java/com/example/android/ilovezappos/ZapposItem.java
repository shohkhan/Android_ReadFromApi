package com.example.android.ilovezappos;

import android.graphics.Bitmap;

public class ZapposItem {
    Bitmap thumbnailImage;
    String brandName;
    String productId;
    String originalPrice;
    String styleId;
    String colorId;
    String price;
    String percentOff;
    String productUrl;
    String productName;
    public ZapposItem(Bitmap thumbnailImage,
                      String brandName,
                      String productId,
                      String originalPrice,
                      String styleId,
                      String colorId,
                      String price,
                      String percentOff,
                      String productUrl,
                      String productName){
        this.thumbnailImage = thumbnailImage;
        this.brandName = brandName;
        this.productId = productId;
        this.originalPrice = originalPrice;
        this.styleId = styleId;
        this.colorId = colorId;
        this.price = price;
        this.percentOff = percentOff;
        this.productUrl = productUrl;
        this.productName = productName;
    }
}
