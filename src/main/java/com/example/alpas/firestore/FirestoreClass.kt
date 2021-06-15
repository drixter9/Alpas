package com.example.alpas.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.alpas.*
import com.example.alpas.models.*
import com.example.alpas.ui.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    //User Registration
    fun registerUser(activity: RegisterActivity, userInfo: UserAlpas) {
        mFireStore.collection(Constants.USERS)
            .document(userInfo.uid)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userSendVerification()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user",
                    e
                )
            }
    }

    // Get the inputted Login Email UID
    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUserID != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(UserAlpas::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.ALPAS_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                // key: value = logged_in_username : firstName lastName
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.putString(
                    Constants.LOGGED_IN_USER_IMAGE,
                    user.image
                )
                editor.putString(
                    Constants.LOGGED_IN_USER_EMAIL,
                    user.email
                )
                editor.apply()
                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSharePrefSuccessful()
                    }
                }
            }.addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun getUserDetailsLogin(activity: LoginActivity) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(UserAlpas::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.ALPAS_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                // key: value = logged_in_username : firstName lastName
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.putString(
                    Constants.LOGGED_IN_USER_IMAGE,
                    user.image
                )
                editor.putString(
                    Constants.LOGGED_IN_USER_EMAIL,
                    user.email
                )
                editor.apply()
                activity.userLoggedInSuccess(user)

            }.addOnFailureListener {
                activity.hideProgressDialog()
            }
    }


    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccessful()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error updating the user details",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity, imageFileURI
            )
        )
        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            Log.e(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddConsultationActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddForumActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
        }
            .addOnFailureListener { exception ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddConsultationActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccessful()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.",
                    e
                )
            }
    }

    fun getProductList(activity: Activity) {
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("product list", document.documents.toString())

                val productList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productList.add(product)
                }
                when (activity) {
                    is MyProductsActivity -> {
                        activity.successProductListFromFireStore(productList)
                    }

                }
            }
    }

    fun getHomeItemsList(fragment: ProductListFragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->

                val productList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productList.add(product)
                    fragment.successHomeDashboardItemsList(productList)
                }
            }
            .addOnFailureListener { e ->
                Log.e(fragment.javaClass.simpleName, "Error While getting dashboard items list", e)
            }
    }

    fun deleteProduct(activity: MyProductsActivity, productID: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productID)
            .delete()
            .addOnSuccessListener {
                activity.productDeleteSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the product",
                    e
                )
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productID: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productID)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val product = document.toObject(Product::class.java)

                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error While getting the product details", e)
            }
    }

    fun addcartitems(activity: ProductDetailsActivity, addToCart: CartItem) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item",
                    e
                )
            }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productID: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productID)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                if (document.size() > 0) {
                    activity.productExistInCart()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",
                    e
                )
            }
    }

    fun getCartList(activity: Activity) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<CartItem> = ArrayList()

                for (i in document.documents) {
                    var cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id
                    list.add(cartItem)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemList(list)
                    }
                    is CheckoutActivity -> {
                        activity.successCartListFromFirestore(list)
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error in getting the list Item", e)
            }
    }

    fun getAllProductList(activity: Activity) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Product List", document.documents.toString())
                val productList: ArrayList<Product> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)

                    product!!.product_id

                    productList.add(product)
                }
                when (activity) {

                    is CartListActivity -> {
                        activity.successProductListFromFireStore(productList)
                    }
                    is CheckoutActivity -> {
                        activity.successProductListFromFirestore(productList)
                    }
                }


            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

            }
    }

    fun deleteItemFromCart(context: Context, cart_id: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemCartRemoveSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName, "Error removing the item in the cart list.",
                    e
                )
            }
    }

    fun updateCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {

                when (context) {
                    is CartListActivity -> {
                        context.updateCartSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName, "Error updating cart list.",
                    e
                )
            }
    }

    fun addAddress(activity: AddAddressActivity, addressInfo: Address) {
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.addAddressSuccess()

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName, "Error updating cart list.",
                    e
                )
            }

    }

    fun getAddressesList(activity: AddressListActivity) {
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val addressList: ArrayList<Address> = ArrayList()

                for (i in document.documents) {
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }
                activity.successAddressListFromFirestore(addressList)

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName, "Error displaying address list.",
                    e
                )
            }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {

                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName, "Error removing address in the list.",
                    e
                )
            }
    }

    fun updateAddress(activity: AddAddressActivity, addressInfo: Address, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {
        mFireStore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.successPlaceOrder()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error placing the order.",
                    e
                )
            }


    }

    fun updateProductDetailsAfterOrder(
        activity: CheckoutActivity,
        cartList: ArrayList<CartItem>,
        order: Order
    ) {
        val writeBatch = mFireStore.batch()

        for (cartItem in cartList) {
            val soldProduct = SoldProduct(
                cartItem.product_owner_id,
                cartItem.title,
                cartItem.price,
                cartItem.cart_quantity,
                cartItem.image,
                order.title,
                order.order_dateTime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address
            )
            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS)
                .document()
            writeBatch.set(documentReference, soldProduct)

        }

        for (cartItem in cartList) {

            val productHashMap = HashMap<String, Any>()

            productHashMap[Constants.STOCK_QUANTITY] =
                (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.PRODUCTS)
                .document(cartItem.product_id)

            writeBatch.update(documentReference, productHashMap)
        }

        for (cartItem in cartList) {
            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cartItem.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit()
            .addOnSuccessListener {
                activity.allDetailsUpdateSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error updating all the details after placing the order.",
                    e
                )
            }

    }

    fun getOrderList(activity: MyOrderActivity) {
        mFireStore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val list: ArrayList<Order> = ArrayList()

                for (i in document) {

                    val orderItem = i.toObject(Order::class.java)
                    orderItem.id = i.id

                    list.add(orderItem)
                }
                activity.populateOrderListInUI(list)

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error getting order list.",
                    e
                )
            }
    }

    fun getSoldProductList(activity: MySoldProductsActivity) {
        mFireStore.collection(Constants.SOLD_PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val list: ArrayList<SoldProduct> = ArrayList()

                for (i in document) {

                    val soldItem = i.toObject(SoldProduct::class.java)
                    soldItem.id = i.id
                    list.add(soldItem)
                }

                activity.successGetSoldProductsList(list)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error getting sold products list.",
                    e
                )

            }
    }

    fun uploadConsultationDetails(
        activity: AddConsultationActivity,
        consultationInfo: Consultation
    ) {
        mFireStore.collection(Constants.CONSULTATIONS)
            .document()
            .set(consultationInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.consultationUploadSuccessful()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the consultation details.",
                    e
                )
            }
    }

    fun getConsultationList(activity: Activity) {
        mFireStore.collection(Constants.CONSULTATIONS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val consultationList: ArrayList<Consultation> = ArrayList()
                for (i in document.documents) {
                    val consultation = i.toObject(Consultation::class.java)
                    consultation!!.consultation_id = i.id
                    consultationList.add(consultation)
                }
                when (activity) {
                    is MyConsultationActivity -> {
                        activity.successConsultationListFromFireStore(consultationList)
                    }

                }
            }
    }

    fun deleteConsultation(activity: MyConsultationActivity, consultationID: String) {
        mFireStore.collection(Constants.CONSULTATIONS)
            .document(consultationID)
            .delete()
            .addOnSuccessListener {
                activity.consultationDeleteSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the consultation",
                    e
                )
            }
    }

    fun getConsultationDetails(activity: ConsultationDetailsActivity, consultationID: String) {
        mFireStore.collection(Constants.CONSULTATIONS)
            .document(consultationID)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val consultation = document.toObject(Consultation::class.java)

                if (consultation != null) {
                    activity.consultationDetailsSuccess(consultation)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error While getting the consultation details",
                    e
                )
            }
    }

    fun checkIfItemExistInSaveConsultation(
        activity: ConsultationDetailsActivity,
        consultationID: String
    ) {
        mFireStore.collection(Constants.SAVED_CONSULTATION)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, consultationID)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                if (document.size() > 0) {
                    activity.consultationExistInSaved()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing saved list.",
                    e
                )
            }
    }

    fun addToSavedItems(activity: ConsultationDetailsActivity, addToSaved: SavedConsultation) {
        mFireStore.collection(Constants.SAVED_CONSULTATION)
            .document()
            .set(addToSaved, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToSavedSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for saved item",
                    e
                )
            }
    }

    fun getConsultationItemsList(fragment: ConsultationFragment) {
        mFireStore.collection(Constants.CONSULTATIONS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val consultationList: ArrayList<Consultation> = ArrayList()
                for (i in document.documents) {
                    val consultation = i.toObject(Consultation::class.java)
                    consultation!!.consultation_id = i.id
                    consultationList.add(consultation)
                    fragment.successConsultationDashboardItemsList(consultationList)
                }
            }
            .addOnFailureListener { e ->
                Log.e(fragment.javaClass.simpleName, "Error While getting dashboard items list", e)
            }

    }

    fun getAllConsultationList(activity: SavedConsultationActivity) {
        mFireStore.collection(Constants.CONSULTATIONS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Product List", document.documents.toString())
                val consultationList: ArrayList<Consultation> = ArrayList()

                for (i in document.documents) {
                    val consultation = i.toObject(Consultation::class.java)
                    consultation!!.consultation_id = i.id
                    consultationList.add(consultation)
                }
                activity.successConsultationListFromFireStore(consultationList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for saved item",
                    e
                )
            }
    }

    fun getSavedConsultationList(activity: SavedConsultationActivity) {
        mFireStore.collection(Constants.SAVED_CONSULTATION)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val savedConsultationList: ArrayList<SavedConsultation> = ArrayList()

                for (i in document.documents) {
                    var savedConsultation = i.toObject(SavedConsultation::class.java)
                    savedConsultation!!.saved_consultation_id = i.id
                    savedConsultationList.add(savedConsultation)
                }
                activity.successSavedConsultationList(savedConsultationList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error in getting the list Item", e)
            }
    }

    fun deleteSavedConsultation(activity: SavedConsultationActivity, saveConsultationID: String) {
        mFireStore.collection(Constants.SAVED_CONSULTATION)
            .document(saveConsultationID)
            .delete()
            .addOnSuccessListener {
                activity.savedConsultationDeleteSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the saved consultation",
                    e
                )
            }
    }

    fun getUserList(activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .get()
            .addOnSuccessListener { document ->
                val userList: ArrayList<UserAlpas> = ArrayList()
                for (i in document.documents) {
                    val user = i.toObject(UserAlpas::class.java)
                    user!!.uid

                    userList.add(user)
                }
                when (activity) {
                    is MessageActivity -> {
                        activity.successUserListFromFireStore(userList)
                    }
                    is SearchUserChatActivity -> {
                        activity.successUserListFromFireStoreSearch(userList)
                    }
                }

            }
            .addOnFailureListener {
                when (activity) {
                    is MessageActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun uploadThreadDetails(
        activity: AddForumActivity,
        threadInfo: Thread
    ) {
        mFireStore.collection(Constants.THREADS)
            .document()
            .set(threadInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.threadUploadSuccessful()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the thread details.",
                    e
                )
            }
    }

    fun getThreadsList(activity: MyThreadsActivity) {
        mFireStore.collection(Constants.THREADS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())
                val threadsList: ArrayList<Thread> = ArrayList()
                for (i in document.documents) {
                    val thread = i.toObject(Thread::class.java)
                    thread!!.thread_id = i.id
                    threadsList.add(thread)
                }
                activity.successThreadsListFromFireStore(threadsList)
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error getting the threads.",
                    e
                )
            }
    }

    fun deleteThread(activity: MyThreadsActivity, threadID: String) {
        mFireStore.collection(Constants.THREADS)
            .document(threadID)
            .delete()
            .addOnSuccessListener {
                activity.threadDeleteSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the thread",
                    e
                )
            }
    }

    fun getThreadItemsList(fragment: ForumFragment) {
        mFireStore.collection(Constants.THREADS)
            .get()
            .addOnSuccessListener { document ->
                val threadList: ArrayList<Thread> = ArrayList()
                for (i in document.documents) {
                    val thread = i.toObject(Thread::class.java)
                    thread!!.thread_id = i.id
                    threadList.add(thread)
                }
                fragment.successThreadItemsList(threadList)
            }
            .addOnFailureListener { e ->
                Log.e(fragment.javaClass.simpleName, "Error While getting thread items list", e)
            }
    }

    fun getAllThreadList(activity: SaveThreadsActivity) {
        mFireStore.collection(Constants.THREADS)
            .get()
            .addOnSuccessListener { document ->
                val threadList: ArrayList<Thread> = ArrayList()
                for (i in document.documents) {
                    val thread = i.toObject(Thread::class.java)
                    thread!!.thread_id = i.id
                    threadList.add(thread)
                }
                activity.successThreadsListFromFireStore(threadList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for saved item",
                    e
                )
            }
    }

    fun getSavedThreadsList(activity: SaveThreadsActivity) {
        mFireStore.collection(Constants.THREADS_SAVED)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val savedThreadList: ArrayList<SavedThread> = ArrayList()

                for (i in document.documents) {
                    var savedThread = i.toObject(SavedThread::class.java)
                    savedThread!!.saved_thread_id = i.id
                    savedThreadList.add(savedThread)
                }
                activity.successSavedThreadList(savedThreadList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error in getting the list Item", e)
            }
    }

    fun addToSavedThreadsItems(fragment: ForumFragment, addToSaved: SavedThread) {
        mFireStore.collection(Constants.THREADS_SAVED)
            .document()
            .set(addToSaved, SetOptions.merge())
            .addOnSuccessListener {
                fragment.addToSavedSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while creating the document for saved item",
                    e
                )
            }
    }

    fun addToSavedThreadsItemsActivity(activity: ThreadDetailsActivity, addToSaved: SavedThread) {
        mFireStore.collection(Constants.THREADS_SAVED)
            .document()
            .set(addToSaved, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToSavedSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
            }
    }

    fun getSavedThreadsListToCheck(fragment: Fragment) {
        mFireStore.collection(Constants.THREADS_SAVED)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val savedThreadList: ArrayList<SavedThread> = ArrayList()

                for (i in document.documents) {
                    var savedThread = i.toObject(SavedThread::class.java)
                    savedThread!!.saved_thread_id = i.id
                    savedThreadList.add(savedThread)
                }
                when (fragment) {
                    is ForumFragment -> {
                        fragment.successGettingSavedThreads(savedThreadList)
                    }
                    is NotificationsFragment -> {
                        fragment.successGettingSavedThreads(savedThreadList)
                    }
                }

            }
            .addOnFailureListener { e ->
                when (fragment) {
                    is ForumFragment -> {
                        fragment.hideProgressDialog()
                    }
                    is NotificationsFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
                Log.e(fragment.javaClass.simpleName, "Error in getting the list Item", e)
            }
    }

    fun getThreadDetail(activity: ThreadDetailsActivity, threadID: String) {
        mFireStore.collection(Constants.THREADS)
            .document(threadID)
            .get()
            .addOnSuccessListener { document ->
                val thread = document.toObject(Thread::class.java)

                if (thread != null) {
                    thread.thread_id = document.id
                    activity.threadDetailsSuccess(thread)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error While getting the consultation details",
                    e
                )
            }
    }

    fun updateThreadLike(
        fragment: ForumFragment,
        thread_id: String,
        itemHashMap: HashMap<String, Any>
    ) {
        mFireStore.collection(Constants.THREADS)
            .document(thread_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                fragment.updateLikeToThreadSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName, "Error updating like in the thread.",
                    e
                )
            }
    }

    fun getThreadDetailFragment(fragment: ForumFragment, threadID: String, addLike: Boolean) {
        mFireStore.collection(Constants.THREADS)
            .document(threadID)
            .get()
            .addOnSuccessListener { document ->
                val thread = document.toObject(Thread::class.java)
                if (thread != null) {
                    thread.thread_id = document.id
                    fragment.successThreadLike(thread, addLike)
                }
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error While getting the fragment details",
                    e
                )
            }
    }

    fun getThreadDetailActivity(
        activity: ThreadDetailsActivity,
        threadID: String,
        addLike: Boolean
    ) {
        mFireStore.collection(Constants.THREADS)
            .document(threadID)
            .get()
            .addOnSuccessListener { document ->
                val thread = document.toObject(Thread::class.java)
                if (thread != null) {
                    thread.thread_id = document.id
                    activity.successThreadLike(thread, addLike)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error While getting the fragment details",
                    e
                )
            }
    }

    fun updateThreadLikeActivity(
        activity: ThreadDetailsActivity,
        thread_id: String,
        itemHashMap: HashMap<String, Any>
    ) {
        mFireStore.collection(Constants.THREADS)
            .document(thread_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                activity.updateLikeToThreadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName, "Error updating like in the thread.",
                    e
                )
            }
    }

    fun getUserDetailsChat(activity: Activity, userID: String) {
        mFireStore.collection(Constants.USERS)
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserAlpas::class.java)!!
                user.uid = document.id

                when (activity) {
                    is ChatActivity -> {
                        activity.successUserDetails(user)
                    }
                    is BaseActivity -> {
                        activity.userInfoSuccess(user)
                    }
                }

            }
            .addOnFailureListener {
                when (activity) {
                    is ChatActivity -> {
                        activity.hideProgressDialog()
                    }
                    is BaseActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun getUserInfo(fragment: BaseFragment, userID: String) {
        mFireStore.collection(Constants.USERS)
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserAlpas::class.java)!!
                user.uid = document.id
                fragment.userInfoSuccessFragment(user)
            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
            }
    }

    fun deleteSavedThread(activity: SaveThreadsActivity, saveThreadID: String) {
        mFireStore.collection(Constants.THREADS_SAVED)
            .document(saveThreadID)
            .delete()
            .addOnSuccessListener {
                activity.savedThreadDeleteSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the saved thread",
                    e
                )
            }
    }

    fun getUserInfoProfile(fragment: ProfileFragment, userID: String) {
        mFireStore.collection(Constants.USERS)
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserAlpas::class.java)!!
                user.uid = document.id

                fragment.userInfoSuccessProfile(user)

            }
            .addOnFailureListener {
                fragment.hideProgressDialog()
            }
    }
}