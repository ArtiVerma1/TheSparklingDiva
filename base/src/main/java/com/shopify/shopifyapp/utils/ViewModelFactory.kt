package com.shopify.shopifyapp.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.shopify.shopifyapp.addresssection.viewmodels.AddressModel
import com.shopify.shopifyapp.basesection.viewmodels.LeftMenuViewModel
import com.shopify.shopifyapp.basesection.viewmodels.SplashViewModel
import com.shopify.shopifyapp.cartsection.viewmodels.CartListViewModel
import com.shopify.shopifyapp.checkoutsection.viewmodels.CheckoutWebLinkViewModel
import com.shopify.shopifyapp.collectionsection.viewmodels.CollectionMenuViewModel
import com.shopify.shopifyapp.collectionsection.viewmodels.CollectionViewModel
import com.shopify.shopifyapp.homesection.viewmodels.HomePageViewModel
import com.shopify.shopifyapp.loginsection.viewmodels.LoginViewModel
import com.shopify.shopifyapp.loginsection.viewmodels.RegistrationViewModel
import com.shopify.shopifyapp.ordersection.viewmodels.OrderDetailsViewModel
import com.shopify.shopifyapp.ordersection.viewmodels.OrderListViewModel
import com.shopify.shopifyapp.personalised.viewmodels.PersonalisedViewModel
import com.shopify.shopifyapp.productsection.viewmodels.ProductListModel
import com.shopify.shopifyapp.productsection.viewmodels.ProductViewModel
import com.shopify.shopifyapp.repositories.Repository
import com.shopify.shopifyapp.searchsection.viewmodels.SearchListModel
import com.shopify.shopifyapp.userprofilesection.viewmodels.UserProfileViewModel
import com.shopify.shopifyapp.wishlistsection.viewmodels.WishListViewModel
import com.shopify.shopifyapp.yotporewards.earnrewards.EarnRewardsViewModel
import com.shopify.shopifyapp.yotporewards.getrewards.GetRewardsViewModel
import com.shopify.shopifyapp.yotporewards.myrewards.MyRewardsViewModel
import com.shopify.shopifyapp.yotporewards.referfriend.ReferFriendViewModel
import com.shopify.shopifyapp.yotporewards.rewarddashboard.RewardDashbordViewModel

import javax.inject.Inject

class ViewModelFactory @Inject
constructor(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(LeftMenuViewModel::class.java)) {
            return LeftMenuViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ProductListModel::class.java)) {
            return ProductListModel(repository) as T
        }
        if (modelClass.isAssignableFrom(CollectionViewModel::class.java)) {
            return CollectionViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(CollectionMenuViewModel::class.java)) {
            return CollectionMenuViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            return RegistrationViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(WishListViewModel::class.java)) {
            return WishListViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(CartListViewModel::class.java)) {
            return CartListViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(CheckoutWebLinkViewModel::class.java)) {
            return CheckoutWebLinkViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(SearchListModel::class.java)) {
            return SearchListModel(repository) as T
        }
        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
            return UserProfileViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(OrderListViewModel::class.java)) {
            return OrderListViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(OrderDetailsViewModel::class.java)) {
            return OrderDetailsViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(AddressModel::class.java)) {
            return AddressModel(repository) as T
        }
        if (modelClass.isAssignableFrom(HomePageViewModel::class.java)) {
            return HomePageViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(PersonalisedViewModel::class.java)) {
            return PersonalisedViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(GetRewardsViewModel::class.java)) {
            return GetRewardsViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(EarnRewardsViewModel::class.java)) {
            return EarnRewardsViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(MyRewardsViewModel::class.java)) {
            return MyRewardsViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(RewardDashbordViewModel::class.java)) {
            return RewardDashbordViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ReferFriendViewModel::class.java)) {
            return ReferFriendViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}
