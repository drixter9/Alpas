package com.example.alpas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {


    //collections
    const val USERS: String = "users"
    const val PRODUCTS: String =  "products"
    const val CONSULTATIONS: String =  "consultations"
    const val THREADS: String =  "threads"
    const val THREADS_SAVED: String =  "threads_saved"
    const val COMMENT: String =  "comment"
    const val LIKE: String =  "like"
    const val NOTIFICATION: String = "notification"

    const val ALPAS_PREFERENCES: String = "AlpasPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val LOGGED_IN_USER_IMAGE: String = "logged_in_user_image"
    const val LOGGED_IN_USER_EMAIL: String = "logged_in_user_email"
    const val EXTRA_USER_DETAILS : String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE  = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val MALE : String = "male"
    const val FEMALE : String = "female"
    const val FIRST_NAME : String = "firstName"
    const val LAST_NAME : String = "lastName"
    const val PREFER_NOT_TO_SAY : String = "prefer not to say"
    const val GENDER : String = "gender"
    const val MOBILE : String = "mobile"
    const val IMAGE : String = "image"
    const val USER_PROFILE_IMAGE:String = "User_Profile_Image"
    const val COMPLETE_PROFILE :String = "profileCompleted"

    const val USER_ID: String = "user_id"
    const val THREAD_ID: String = "thread_id"

    const val PRODUCT_IMAGE :String = "Product_Image"

    const val EXTRA_PRODUCT_ID:String = "extra_product_id"
    const val EXTRA_PRODUCT_OWNER_ID : String = "extra_product_owner_id"

    const val DEFAULT_CART_QUANTITY: String = "1"

    const val CART_ITEMS: String = "cart_items"

    const val PRODUCT_ID : String = "product_id"
    const val CART_QUANTITY: String = "cart_quantity"

    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"

    const val ADDRESSES: String = "addresses"
    const val EXTRA_ADDRESS_DETAILS:String = "extra_address_details"

    const val EXTRA_SELECTED_ADDRESS:String = "extra_selected_address"
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    const val ORDERS: String = "orders"

    const val STOCK_QUANTITY: String = "stock_quantity"

    const val EXTRA_ORDER_DETAILS: String = "extra_order_details"

    const val SOLD_PRODUCTS: String = "sold_products"

    const val EXTRA_SOLD_PRODUCT_DETAILS: String = "extra_sold_product_details"

    const val SELECT_PROGRAM_OF_STUDY:String= "Select program of study:"
    const val SELECT_FIELD_OF_STUDY:String= "Select Field of Study:"
    const val SELECT_DEGREE_TYPE:String = "Select degree type:"

    const val CONSULTATION_IMAGE :String = "Consultation_Image"

    const val ADD_CONSULTATION_REQUEST_CODE: Int = 122
    const val ADD_THREADS_REQUEST_CODE: Int = 123
    const val ADD_PRODUCTS_REQUEST_CODE: Int = 124

    const val EXTRA_CONSULTATION_ID:String = "extra_consultation_id"
    const val EXTRA_CONSULTATION_OWNER_ID : String = "extra_consultation_owner_id"

    const val SAVED_CONSULTATION:String = "saved_consultation"

    const val FORUM_IMAGE :String = "Forum_Image"
    const val DEFAULT_LIKE_QUANTITY: Long = 0
    const val DEFAULT_COMMENT_QUANTITY: Long = 0
    const val EXTRA_THREAD_DETAILS:String = "extra_thread_details"
    const val EXTRA_SAVED_THREAD_DETAILS:String = "extra_saved_thread_details"
    const val THREAD_lIKE:String = "thread_like"
    const val COMMENT_LIKE:String= "comment_like"

    const val NOTIFICATION_CHAT: Int = 0
    const val NOTIFICATION_THREADS: Int = 1
    const val NOTIFICATION_PRODUCTS: Int = 2
    const val NOTIFICATION_ORDER: Int = 3
    const val NOTIFICATION_COMMENTS: Int = 4
    const val NOTIFICATION_COMMENTS_LIKE: Int = 5

    const val STATUS_PENDING: String = "Pending"
    const val STATUS_PROCESSING: String = "In process"
    const val STATUS_DELIVER: String = "Delivered"

    const val CHAT: String =  "Chat"
    const val EXTRA_USER_DETAILS_CHAT = "user_details"
    const val EXTRA_USERNAME_CHAT = "user_name"

    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "AAAAu2LIZhw:APA91bFkM2Ldf3xNh5gTxSFRQPX-tkSpQhtBtEko2soOso5XMLzY7fWDUjhALMXnott9bB1w3g7Tso2aEah97vJ0cEed25xOdujR_GF8BTxhVDsGkkX7lYjhbslh8LMjxWrg5ci4lTxZ"
    const val CONTENT_TYPE = "application/json"

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    fun getFileExtension(activity: Activity,uri: Uri?):String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}
