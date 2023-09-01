package com.fin_group.aslzar.api

object Constants {
    const val BASE_URL = "http://192.168.1.60/AslZar/hs/Data/"

    //login
    const val LOGIN = "Auth"
    const val GET_TOKEN = "GetToken"

    //products
    const val GET_ALL_PRODUCTS = "GetAllProducts"
    const val GET_PRODUCT_BY_ID = "GetProduct/{id}"
    const val SIMILAR_PRODUCTS = "SimilarProducts/{id}"

    //categories
    const val GET_CATEGORY = "GetCategory"

}