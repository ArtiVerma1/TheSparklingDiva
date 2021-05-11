package com.kumaoni.blessings.dependecyinjection

import com.kumaoni.blessings.addresssection.activities.AddressList
import com.kumaoni.blessings.basesection.activities.NewBaseActivity
import com.kumaoni.blessings.basesection.activities.Splash
import com.kumaoni.blessings.basesection.fragments.LeftMenu
import com.kumaoni.blessings.cartsection.activities.CartList
import com.kumaoni.blessings.checkoutsection.activities.CheckoutWeblink
import com.kumaoni.blessings.checkoutsection.activities.OrderSuccessActivity
import com.kumaoni.blessings.collectionsection.activities.CollectionList
import com.kumaoni.blessings.collectionsection.activities.CollectionListMenu
import com.kumaoni.blessings.homesection.activities.HomePage
import com.kumaoni.blessings.homesection.viewmodels.HomePageViewModel
import com.kumaoni.blessings.jobservicessection.JobScheduler
import com.kumaoni.blessings.productsection.activities.JudgeMeCreateReview
import com.kumaoni.blessings.loginsection.activity.LoginActivity
import com.kumaoni.blessings.loginsection.activity.RegistrationActivity
import com.kumaoni.blessings.ordersection.activities.OrderDetails
import com.kumaoni.blessings.ordersection.activities.OrderList
import com.kumaoni.blessings.productsection.activities.*
import com.kumaoni.blessings.quickadd_section.activities.QuickAddActivity
import com.kumaoni.blessings.searchsection.activities.AutoSearch
import com.kumaoni.blessings.userprofilesection.activities.UserProfile
import com.kumaoni.blessings.utils.Urls
import com.kumaoni.blessings.wishlistsection.activities.WishList

import javax.inject.Singleton

import dagger.Component

@Component(modules = [UtilsModule::class])
@Singleton
interface MageNativeAppComponent {

    fun doSplashInjection(splash: Splash)
    fun doProductListInjection(product: ProductList)
    fun doCollectionInjection(collectionList: CollectionList)
    fun doCollectionInjection(collectionList: CollectionListMenu)
    fun doProductViewInjection(product: ProductView)
    fun doJudgeMeReviewInjection(judgeMeCreateReview: JudgeMeCreateReview)
    fun doReviewListInjection(reviewListActivity: AllReviewListActivity)
    fun doAllJudgeMeReviewListInjection(judgeMeReviews: AllJudgeMeReviews)
    fun doZoomActivityInjection(base: ZoomActivity)
    fun doBaseActivityInjection(base: NewBaseActivity)
    fun doWishListActivityInjection(wish: WishList)
    fun doCartListActivityInjection(cart: CartList)
    fun doCheckoutWeblinkActivityInjection(cart: CheckoutWeblink)
    fun doAutoSearchActivityInjection(cart: AutoSearch)
    fun doLoginActivtyInjection(loginActivity: LoginActivity)
    fun doRegistrationActivityInjection(registrationActivity: RegistrationActivity)
    fun doLeftMeuInjection(left: LeftMenu)
    fun doUserProfileInjection(profile: UserProfile)
    fun doOrderListInjection(profile: OrderList)
    fun doOrderDetailsInjection(orderDetails: OrderDetails)
    fun doAddressListInjection(addressList: AddressList)
    fun doHomePageInjection(home: HomePage)
    fun doHomePageModelInjection(home: HomePageViewModel)
    fun orderSuccessInjection(orderSuccessActivity: OrderSuccessActivity)
    fun quickAddInjection(quickAddActivity: QuickAddActivity)
    fun doServiceInjection(job: JobScheduler)
    fun doURlInjection(urls: Urls)
}
