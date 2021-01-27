package com.shopify.shopifyapp.basesection.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

class FeaturesModel : BaseObservable() {
    @Bindable
    var in_app_wishlist: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.in_app_wishlist)
        }

    @Bindable
    var real_time_sync: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.real_time_sync)
        }

    @Bindable
    var rtl_support: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.rtl_support)
        }

    @Bindable
    var white_labled_app: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.white_labled_app)
        }

    @Bindable
    var push_notification: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.push_notification)
        }

    @Bindable
    var product_share: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.product_share)
        }

    @Bindable
    var multi_currency: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.multi_currency)
        }

    @Bindable
    var multi_language: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.multi_language)
        }

    @Bindable
    var abandoned_cart_compaigns: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.abandoned_cart_compaigns)
        }

    @Bindable
    var allwebsite_getway_supported: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.allwebsite_getway_supported)
        }

    @Bindable
    var scheduled_pushnotification: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.scheduled_pushnotification)
        }

    @Bindable
    var applive_preview: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.applive_preview)
        }

    @Bindable
    var product_reviews: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.product_reviews)
        }

    @Bindable
    var ardumented_reality: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.ardumented_reality)
        }

}