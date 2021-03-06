package com.shopify.shopifyapp.dependecyinjection

import com.shopify.shopifyapp.addresssection.activities.AddressList
import com.shopify.shopifyapp.basesection.activities.NewBaseActivity
import com.shopify.shopifyapp.basesection.activities.Splash
import com.shopify.shopifyapp.basesection.fragments.LeftMenu
import com.shopify.shopifyapp.cartsection.activities.CartList
import com.shopify.shopifyapp.checkoutsection.activities.CheckoutWeblink
import com.shopify.shopifyapp.checkoutsection.activities.OrderSuccessActivity
import com.shopify.shopifyapp.collectionsection.activities.CollectionList
import com.shopify.shopifyapp.collectionsection.activities.CollectionListMenu
import com.shopify.shopifyapp.homesection.activities.HomePage
import com.shopify.shopifyapp.homesection.viewmodels.HomePageViewModel
import com.shopify.shopifyapp.jobservicessection.JobScheduler
import com.shopify.shopifyapp.productsection.activities.JudgeMeCreateReview
import com.shopify.shopifyapp.loginsection.activity.LoginActivity
import com.shopify.shopifyapp.loginsection.activity.RegistrationActivity
import com.shopify.shopifyapp.ordersection.activities.OrderDetails
import com.shopify.shopifyapp.ordersection.activities.OrderList
import com.shopify.shopifyapp.productsection.activities.*
import com.shopify.shopifyapp.quickadd_section.activities.QuickAddActivity
import com.shopify.shopifyapp.searchsection.activities.AutoSearch
import com.shopify.shopifyapp.userprofilesection.activities.UserProfile
import com.shopify.shopifyapp.utils.Urls
import com.shopify.shopifyapp.wishlistsection.activities.WishList
import com.shopify.shopifyapp.yotporewards.earnrewards.EarnRewardsActivity
import com.shopify.shopifyapp.yotporewards.earnrewards.FaqsActivity
import com.shopify.shopifyapp.yotporewards.getrewards.GetRewardsActivity
import com.shopify.shopifyapp.yotporewards.myrewards.MyRewardsActivity
import com.shopify.shopifyapp.yotporewards.referfriend.ReferFriendActivity
import com.shopify.shopifyapp.yotporewards.rewarddashboard.RewardDashboard
import com.shopify.shopifyapp.yotporewards.withoutlogin.RewardsPointActivity

import javax.inject.Singleton

import dagger.Component

@Component(modules = [UtilsModule::class])
@Singleton
interface MageNativeAppComponent {

    fun doSplashInjection(splash: Splash)
    fun doFilterInjection(product: FilterActivity)
    fun doProductListInjection(product: ProductList)
    fun doCollectionInjection(collectionList: CollectionList)
    fun doCollectionInjection(collectionList: CollectionListMenu)
    fun doProductViewInjection(product: ProductView)
    fun doJudgeMeReviewInjection(judgeMeCreateReview: JudgeMeCreateReview)
    fun doYotpoReviewInjection(WriteAReview: WriteAReview)
    fun doReviewListInjection(reviewListActivity: AllReviewListActivity)
    fun doAllJudgeMeReviewListInjection(judgeMeReviews: AllJudgeMeReviews)
    fun doAllAliReviewListInjection(aliReviews: AllAliReviewsListActivity)
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
    fun doRewarsPointsInjection(rewardsPointActivity: RewardsPointActivity)
    fun doRewarsDashbordInjection(rewardsDashboard: RewardDashboard)
    fun doGetRewadsInjection(getRewardsActivity: GetRewardsActivity)
    fun doEarnRewadsInjection(earnRewardsActivity: EarnRewardsActivity)
    fun doReferFriendInjection(referFriendActivity: ReferFriendActivity)
    fun doMyRewardInjection(myRewardsActivity: MyRewardsActivity)
    fun doFaqsInjection(faqsActivity: FaqsActivity)
}
