package com.cap.cheapstays

class Hotel {
    var hotelImageURL: String? = null
    var hotelName: String? = null
   var rating: String? = null
    var price: String?=null
    constructor(hotelImageURL: String, hotelName: String,rating:String,price : String) {
        this.hotelImageURL = hotelImageURL
        this.hotelName = hotelName
        this.rating=rating
        this.price=price
    }
    constructor() {}
}