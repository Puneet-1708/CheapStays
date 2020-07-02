package com.cap.cheapstays

class Hotel {
    var hotelImageURL: String? = null
    var hotelName: String? = null
   var rating: String? = null
    constructor(hotelImageURL: String, hotelName: String,rating:String) {
        this.hotelImageURL = hotelImageURL
        this.hotelName = hotelName
        this.rating=rating
    }
    constructor() {}
}