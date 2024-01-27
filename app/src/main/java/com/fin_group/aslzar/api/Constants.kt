package com.fin_group.aslzar.api

object Constants {
    const val BASE_URL = "http://192.168.1.10:8080/AslZar/hs/Data/"
//    const val BASE_URL = "http://95.142.94.22:8080/AslZar/hs/Data/"
//    const val BASE_URL = "http://192.168.1.10:8080/AslzarSRV/hs/Data/"
//    const val BASE_URL = "http://95.142.94.22:8080/AslzarSRV/hs/Data/"

    //profile
    const val LOGIN = "Auth"
    const val GET_SALES_PLAN = "GetSalesPlan"

    //products
    const val GET_ALL_PRODUCTS = "GetAllProducts"
    const val GET_PRODUCT_BY_ID = "GetProduct/{id}"
    const val SIMILAR_PRODUCTS = "SimilarProducts/{id}"
    const val SET_IN_PRODUCT = "GetSetProduct/{id}"
    const val SALES_PRODUCTS = "GetSalesProduct"
    const val NEW_PRODUCTS = "GetNewProducts"

    //categories
    const val GET_CATEGORY = "GetCategory"

    //clients
    const val GET_CLIENTS = "GetClients"

    //
    const val GET_COEFFICIENT = "GetСoefficientPlan"

    //password
    const val FORGOT_PASSWORD_WITH_MAIL = "FgtPass/{mail}"
    const val CHANGE_PASSWORD = "ChangePassword/{password}"
    const val FORGOT_PASSWORD = "ForgetPassword"

    const val SCANNER = "GetProductQr/{id}"
}