package com.kumaoni.blessings.dependecyinjection;

import android.content.Context;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import com.shopify.buy3.GraphClient;
import com.kumaoni.blessings.addresssection.activities.AddressList;
import com.kumaoni.blessings.addresssection.activities.AddressList_MembersInjector;
import com.kumaoni.blessings.addresssection.adapters.AddressListAdapter;
import com.kumaoni.blessings.basesection.activities.BaseActivity;
import com.kumaoni.blessings.basesection.activities.NewBaseActivity;
import com.kumaoni.blessings.basesection.activities.NewBaseActivity_MembersInjector;
import com.kumaoni.blessings.basesection.activities.Splash_MembersInjector;
import com.kumaoni.blessings.basesection.adapters.RecylerAdapter;
import com.kumaoni.blessings.basesection.fragments.LeftMenu;
import com.kumaoni.blessings.basesection.fragments.LeftMenu_MembersInjector;
import com.kumaoni.blessings.cartsection.activities.CartList;
import com.kumaoni.blessings.cartsection.activities.CartList_MembersInjector;
import com.kumaoni.blessings.cartsection.adapters.CartListAdapter;
import com.kumaoni.blessings.checkoutsection.activities.CheckoutWeblink;
import com.kumaoni.blessings.checkoutsection.activities.CheckoutWeblink_MembersInjector;
import com.kumaoni.blessings.collectionsection.activities.CollectionList;
import com.kumaoni.blessings.collectionsection.activities.CollectionList_MembersInjector;
import com.kumaoni.blessings.collectionsection.adapters.CollectionRecylerAdapter;
import com.kumaoni.blessings.dbconnection.database.AppDatabase;
import com.kumaoni.blessings.homesection.activities.HomePage;
import com.kumaoni.blessings.homesection.activities.HomePage_MembersInjector;
import com.kumaoni.blessings.homesection.adapters.CollectionGridAdapter;
import com.kumaoni.blessings.homesection.adapters.CollectionSliderAdapter;
import com.kumaoni.blessings.homesection.adapters.ProductGridAdapter;
import com.kumaoni.blessings.homesection.adapters.ProductListAdapter;
import com.kumaoni.blessings.homesection.adapters.ProductSliderAdapter;
import com.kumaoni.blessings.homesection.viewmodels.HomePageViewModel;
import com.kumaoni.blessings.homesection.viewmodels.HomePageViewModel_MembersInjector;
import com.kumaoni.blessings.jobservicessection.JobScheduler;
import com.kumaoni.blessings.jobservicessection.JobScheduler_MembersInjector;
import com.kumaoni.blessings.loginsection.activity.LoginActivity;
import com.kumaoni.blessings.loginsection.activity.LoginActivity_MembersInjector;
import com.kumaoni.blessings.loginsection.activity.RegistrationActivity;
import com.kumaoni.blessings.loginsection.activity.RegistrationActivity_MembersInjector;
import com.kumaoni.blessings.ordersection.activities.OrderList;
import com.kumaoni.blessings.ordersection.activities.OrderList_MembersInjector;
import com.kumaoni.blessings.ordersection.adapters.OrderListAdapter;
import com.kumaoni.blessings.productsection.activities.ProductList;
import com.kumaoni.blessings.productsection.activities.ProductList_MembersInjector;
import com.kumaoni.blessings.productsection.activities.ProductView;
import com.kumaoni.blessings.productsection.activities.ProductView_MembersInjector;
import com.kumaoni.blessings.productsection.adapters.ProductRecylerGridAdapter;
import com.kumaoni.blessings.productsection.adapters.VariantAdapter;
import com.kumaoni.blessings.repositories.Repository;
import com.kumaoni.blessings.searchsection.activities.AutoSearch;
import com.kumaoni.blessings.searchsection.activities.AutoSearch_MembersInjector;
import com.kumaoni.blessings.searchsection.adapters.SearchRecylerAdapter;
import com.kumaoni.blessings.userprofilesection.activities.UserProfile;
import com.kumaoni.blessings.userprofilesection.activities.UserProfile_MembersInjector;
import com.kumaoni.blessings.utils.ApiCallInterface;
import com.kumaoni.blessings.utils.Urls;
import com.kumaoni.blessings.utils.Urls_MembersInjector;
import com.kumaoni.blessings.utils.ViewModelFactory;
import com.kumaoni.blessings.wishlistsection.activities.WishList;
import com.kumaoni.blessings.wishlistsection.activities.WishList_MembersInjector;
import com.kumaoni.blessings.wishlistsection.adapters.WishListAdapter;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DaggerMageNativeAppComponent implements MageNativeAppComponent {
  private Provider<Gson> provideGson$base_debugProvider;

  private Provider<OkHttpClient> getRequestHeader$base_debugProvider;

  private Provider<Retrofit> provideRetrofit$base_debugProvider;

  private Provider<ApiCallInterface> getApiCallInterface$base_debugProvider;

  private Provider<Context> provideContext$base_debugProvider;

  private Provider<AppDatabase> getAppDatabase$base_debugProvider;

  private Provider<GraphClient> getGraphClient$base_debugProvider;

  private Provider<Repository> getRepository$base_debugProvider;

  private DaggerMageNativeAppComponent(UtilsModule utilsModuleParam) {

    initialize(utilsModuleParam);
  }

  public static Builder builder() {
    return new Builder();
  }

  private ViewModelFactory getViewModelFactory() {
    return new ViewModelFactory(getRepository$base_debugProvider.get());}

  @SuppressWarnings("unchecked")
  private void initialize(final UtilsModule utilsModuleParam) {
    this.provideGson$base_debugProvider = DoubleCheck.provider(UtilsModule_ProvideGson$base_debugFactory.create(utilsModuleParam));
    this.getRequestHeader$base_debugProvider = DoubleCheck.provider(UtilsModule_GetRequestHeader$base_debugFactory.create(utilsModuleParam));
    this.provideRetrofit$base_debugProvider = DoubleCheck.provider(UtilsModule_ProvideRetrofit$base_debugFactory.create(utilsModuleParam, provideGson$base_debugProvider, getRequestHeader$base_debugProvider));
    this.getApiCallInterface$base_debugProvider = DoubleCheck.provider(UtilsModule_GetApiCallInterface$base_debugFactory.create(utilsModuleParam, provideRetrofit$base_debugProvider));
    this.provideContext$base_debugProvider = DoubleCheck.provider(UtilsModule_ProvideContext$base_debugFactory.create(utilsModuleParam));
    this.getAppDatabase$base_debugProvider = DoubleCheck.provider(UtilsModule_GetAppDatabase$base_debugFactory.create(utilsModuleParam, provideContext$base_debugProvider));
    this.getGraphClient$base_debugProvider = DoubleCheck.provider(UtilsModule_GetGraphClient$base_debugFactory.create(utilsModuleParam, provideContext$base_debugProvider, getRequestHeader$base_debugProvider));
    this.getRepository$base_debugProvider = DoubleCheck.provider(UtilsModule_GetRepository$base_debugFactory.create(utilsModuleParam, getApiCallInterface$base_debugProvider, getAppDatabase$base_debugProvider, getGraphClient$base_debugProvider));
  }

  @Override
  public void doSplashInjection(Splash splash) {
    injectSplash(splash);}

  @Override
  public void doProductListInjection(ProductList product) {
    injectProductList(product);}

  @Override
  public void doCollectionInjection(CollectionList collectionList) {
    injectCollectionList(collectionList);}

  @Override
  public void doProductViewInjection(ProductView product) {
    injectProductView(product);}

  @Override
  public void doBaseActivityInjection(BaseActivity base) {
  }

  @Override
  public void doBaseActivityInjection(NewBaseActivity base) {
    injectNewBaseActivity(base);}

  @Override
  public void doWishListActivityInjection(WishList wish) {
    injectWishList(wish);}

  @Override
  public void doCartListActivityInjection(CartList cart) {
    injectCartList(cart);}

  @Override
  public void doCheckoutWeblinkActivityInjection(CheckoutWeblink cart) {
    injectCheckoutWeblink(cart);}

  @Override
  public void doAutoSearchActivityInjection(AutoSearch cart) {
    injectAutoSearch(cart);}

  @Override
  public void doLoginActivtyInjection(LoginActivity loginActivity) {
    injectLoginActivity(loginActivity);}

  @Override
  public void doRegistrationActivityInjection(RegistrationActivity registrationActivity) {
    injectRegistrationActivity(registrationActivity);}

  @Override
  public void doLeftMeuInjection(LeftMenu left) {
    injectLeftMenu(left);}

  @Override
  public void doUserProfileInjection(UserProfile profile) {
    injectUserProfile(profile);}

  @Override
  public void doOrderListInjection(OrderList profile) {
    injectOrderList(profile);}

  @Override
  public void doAddressListInjection(AddressList addressList) {
    injectAddressList(addressList);}

  @Override
  public void doHomePageInjection(HomePage home) {
    injectHomePage(home);}

  @Override
  public void doHomePageModelInjection(HomePageViewModel home) {
    injectHomePageViewModel(home);}

  @Override
  public void doServiceInjection(JobScheduler job) {
    injectJobScheduler(job);}

  @Override
  public void doURlInjection(Urls urls) {
    injectUrls(urls);}

  @CanIgnoreReturnValue
  private Splash injectSplash(Splash instance) {
    Splash_MembersInjector.injectViewModelFactory(instance, getViewModelFactory());
    return instance;
  }

  @CanIgnoreReturnValue
  private ProductList injectProductList(ProductList instance) {
    ProductList_MembersInjector.injectFactory(instance, getViewModelFactory());
    ProductList_MembersInjector.injectAdapter(instance, new ProductRecylerAdapter());
    return instance;
  }

  @CanIgnoreReturnValue
  private CollectionList injectCollectionList(CollectionList instance) {
    CollectionList_MembersInjector.injectFactory(instance, getViewModelFactory());
    CollectionList_MembersInjector.injectAdapter(instance, new CollectionRecylerAdapter());
    return instance;
  }

  @CanIgnoreReturnValue
  private ProductView injectProductView(ProductView instance) {
    ProductView_MembersInjector.injectFactory(instance, getViewModelFactory());
    ProductView_MembersInjector.injectAdapter(instance, new VariantAdapter());
    return instance;
  }

  @CanIgnoreReturnValue
  private NewBaseActivity injectNewBaseActivity(NewBaseActivity instance) {
    NewBaseActivity_MembersInjector.injectViewModelFactory(instance, getViewModelFactory());
    NewBaseActivity_MembersInjector.injectAdapter(instance, new RecylerAdapter());
    return instance;
  }

  @CanIgnoreReturnValue
  private WishList injectWishList(WishList instance) {
    WishList_MembersInjector.injectFactory(instance, getViewModelFactory());
    WishList_MembersInjector.injectAdapter(instance, new WishListAdapter());
    return instance;
  }

  @CanIgnoreReturnValue
  private CartList injectCartList(CartList instance) {
    CartList_MembersInjector.injectFactory(instance, getViewModelFactory());
    CartList_MembersInjector.injectAdapter(instance, new CartListAdapter());
    return instance;
  }

  @CanIgnoreReturnValue
  private CheckoutWeblink injectCheckoutWeblink(CheckoutWeblink instance) {
    CheckoutWeblink_MembersInjector.injectFactory(instance, getViewModelFactory());
    return instance;
  }

  @CanIgnoreReturnValue
  private AutoSearch injectAutoSearch(AutoSearch instance) {
    AutoSearch_MembersInjector.injectFactory(instance, getViewModelFactory());
    AutoSearch_MembersInjector.injectAdapter(instance, new SearchRecylerAdapter());
    return instance;
  }

  @CanIgnoreReturnValue
  private LoginActivity injectLoginActivity(LoginActivity instance) {
    LoginActivity_MembersInjector.injectFactory(instance, getViewModelFactory());
    return instance;
  }

  @CanIgnoreReturnValue
  private RegistrationActivity injectRegistrationActivity(RegistrationActivity instance) {
    RegistrationActivity_MembersInjector.injectFactory(instance, getViewModelFactory());
    return instance;
  }

  @CanIgnoreReturnValue
  private LeftMenu injectLeftMenu(LeftMenu instance) {
    LeftMenu_MembersInjector.injectViewModelFactory(instance, getViewModelFactory());
    return instance;
  }

  @CanIgnoreReturnValue
  private UserProfile injectUserProfile(UserProfile instance) {
    UserProfile_MembersInjector.injectFactory(instance, getViewModelFactory());
    return instance;
  }

  @CanIgnoreReturnValue
  private OrderList injectOrderList(OrderList instance) {
    OrderList_MembersInjector.injectFactory(instance, getViewModelFactory());
    OrderList_MembersInjector.injectAdapter(instance, new OrderListAdapter());
    return instance;
  }

  @CanIgnoreReturnValue
  private AddressList injectAddressList(AddressList instance) {
    AddressList_MembersInjector.injectFactory(instance, getViewModelFactory());
    AddressList_MembersInjector.injectAdapter(instance, new AddressListAdapter());
    return instance;
  }

  @CanIgnoreReturnValue
  private HomePage injectHomePage(HomePage instance) {
    NewBaseActivity_MembersInjector.injectViewModelFactory(instance, getViewModelFactory());
    NewBaseActivity_MembersInjector.injectAdapter(instance, new RecylerAdapter());
    HomePage_MembersInjector.injectFactory(instance, getViewModelFactory());
    return instance;
  }

  @CanIgnoreReturnValue
  private HomePageViewModel injectHomePageViewModel(HomePageViewModel instance) {
    HomePageViewModel_MembersInjector.injectHomeadapter(instance, new ProductSliderAdapter());
    HomePageViewModel_MembersInjector.injectProductListAdapter(instance, new ProductListAdapter());
    HomePageViewModel_MembersInjector.injectAdapter(instance, new CollectionGridAdapter());
    HomePageViewModel_MembersInjector.injectSlideradapter(instance, new CollectionSliderAdapter());
    HomePageViewModel_MembersInjector.injectGridAdapter(instance, new ProductGridAdapter());
    return instance;
  }

  @CanIgnoreReturnValue
  private JobScheduler injectJobScheduler(JobScheduler instance) {
    JobScheduler_MembersInjector.injectRepository(instance, getRepository$base_debugProvider.get());
    return instance;
  }

  @CanIgnoreReturnValue
  private Urls injectUrls(Urls instance) {
    Urls_MembersInjector.injectRepository(instance, getRepository$base_debugProvider.get());
    return instance;
  }

  public static final class Builder {
    private UtilsModule utilsModule;

    private Builder() {
    }

    public Builder utilsModule(UtilsModule utilsModule) {
      this.utilsModule = Preconditions.checkNotNull(utilsModule);
      return this;
    }

    public MageNativeAppComponent build() {
      Preconditions.checkBuilderRequirement(utilsModule, UtilsModule.class);
      return new DaggerMageNativeAppComponent(utilsModule);
    }
  }
}
