package com.shopify.shopifyapp.yotporewards.earnrewards

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shopify.shopifyapp.MyApplication
import com.shopify.shopifyapp.R
import com.shopify.shopifyapp.basesection.activities.NewBaseActivity
import com.shopify.shopifyapp.databinding.ActivityEarnRewardsBinding
import com.shopify.shopifyapp.databinding.RedeemdialogBinding
import com.shopify.shopifyapp.utils.ApiResponse
import com.shopify.shopifyapp.utils.Constant
import com.shopify.shopifyapp.utils.ViewModelFactory
import com.shopify.shopifyapp.yotporewards.earnrewards.adapter.EarnRewardAdapter
import com.shopify.shopifyapp.yotporewards.earnrewards.model.EarnRewardModel
import com.shopify.shopifyapp.yotporewards.earnrewards.model.EarnRewardModelItem
import com.shopify.shopifyapp.yotporewards.referfriend.ReferFriendActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import javax.inject.Inject

class EarnRewardsActivity : NewBaseActivity() {
    private var binding: ActivityEarnRewardsBinding? = null
    private var earnRewardsViewModel: EarnRewardsViewModel? = null

    @Inject
    lateinit var earnRewardAdapter: EarnRewardAdapter

    @Inject
    lateinit var factory: ViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_earn_rewards, group, true)
        (application as MyApplication).mageNativeAppComponent!!.doEarnRewadsInjection(this)
        showBackButton()
        showTittle(getString(R.string.earn_points))
        earnRewardsViewModel = ViewModelProvider(this, factory).get(EarnRewardsViewModel::class.java)
        earnRewardsViewModel?.context = this
        earnRewardsViewModel?.earnrewards?.observe(this, Observer { this.consumeEarnRewards(it) })
        earnRewardsViewModel?.earnRewards()
    }

    private fun consumeEarnRewards(response: ApiResponse?) {
        if (response?.data != null) {
            val collectionType: Type = object : TypeToken<List<EarnRewardModelItem>>() {}.getType()
            var earnrewardModel = Gson().fromJson<List<EarnRewardModelItem>>(response?.data.toString(), collectionType).toList()

            earnRewardAdapter.setData(earnrewardModel, object : EarnRewardAdapter.ClickEarnCallback {
                override fun earnRewardCallback(earnRewardModelItem: EarnRewardModelItem) {
                    var dialog = Dialog(this@EarnRewardsActivity, R.style.WideDialog)
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                    var redeemdialogBinding = DataBindingUtil.inflate<RedeemdialogBinding>(layoutInflater, R.layout.redeemdialog, null, false)
                    redeemdialogBinding.headerPrice?.text = earnRewardModelItem.title
                    redeemdialogBinding.description.text = earnRewardModelItem.details
                    if (TextUtils.isEmpty(earnRewardModelItem.ctaText)) {
                        redeemdialogBinding.butRedeem.visibility = View.GONE
                    } else {
                        redeemdialogBinding.butRedeem.visibility = View.VISIBLE
                        redeemdialogBinding.butRedeem.text = earnRewardModelItem.ctaText
                    }

                    dialog.setContentView(redeemdialogBinding.root)
                    redeemdialogBinding.butRedeem.setOnClickListener {
                        if (earnRewardModelItem.type.equals("ReferralCampaign")) {
                            startActivity(Intent(this@EarnRewardsActivity, ReferFriendActivity::class.java))
                            Constant.activityTransition(this@EarnRewardsActivity)
                            finish()
                        }
                    }
                    redeemdialogBinding.cancelBut.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }
            })
            binding?.earnrewardList?.adapter = earnRewardAdapter
        }
    }
}