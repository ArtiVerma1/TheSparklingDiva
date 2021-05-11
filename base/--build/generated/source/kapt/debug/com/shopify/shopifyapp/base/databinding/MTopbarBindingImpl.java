package com.kumaoni.blessings.databinding;
import com.kumaoni.blessings.R;
import com.kumaoni.blessings.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
@javax.annotation.Generated("Android Data Binding")
public class MTopbarBindingImpl extends MTopbarBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.overlay, 2);
        sViewsWithIds.put(R.id.fullsearch, 3);
        sViewsWithIds.put(R.id.bannersection, 4);
        sViewsWithIds.put(R.id.card, 5);
        sViewsWithIds.put(R.id.bannerss, 6);
        sViewsWithIds.put(R.id.indicators, 7);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public MTopbarBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 8, sIncludes, sViewsWithIds));
    }
    private MTopbarBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (androidx.appcompat.widget.AppCompatImageView) bindings[1]
            , (android.widget.RelativeLayout) bindings[4]
            , (androidx.viewpager.widget.ViewPager) bindings[6]
            , (androidx.cardview.widget.CardView) bindings[5]
            , (com.kumaoni.blessings.customviews.MageNativeTextView) bindings[3]
            , (com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator) bindings[7]
            , (android.view.View) bindings[2]
            );
        this.backImage.setTag(null);
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x4L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.commondata == variableId) {
            setCommondata((com.kumaoni.blessings.basesection.models.CommanModel) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setCommondata(@Nullable com.kumaoni.blessings.basesection.models.CommanModel Commondata) {
        updateRegistration(0, Commondata);
        this.mCommondata = Commondata;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.commondata);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeCommondata((com.kumaoni.blessings.basesection.models.CommanModel) object, fieldId);
        }
        return false;
    }
    private boolean onChangeCommondata(com.kumaoni.blessings.basesection.models.CommanModel Commondata, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        else if (fieldId == BR.imageurl) {
            synchronized(this) {
                    mDirtyFlags |= 0x2L;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        com.kumaoni.blessings.basesection.models.CommanModel commondata = mCommondata;
        java.lang.String commondataImageurl = null;

        if ((dirtyFlags & 0x7L) != 0) {



                if (commondata != null) {
                    // read commondata.imageurl
                    commondataImageurl = commondata.getImageurl();
                }
        }
        // batch finished
        if ((dirtyFlags & 0x7L) != 0) {
            // api target 1

            com.kumaoni.blessings.basesection.models.CommanModel.loadImage(this.backImage, commondataImageurl);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): commondata
        flag 1 (0x2L): commondata.imageurl
        flag 2 (0x3L): null
    flag mapping end*/
    //end
}