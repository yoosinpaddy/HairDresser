package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.storage.repositories.CartRepository;
import apps.trichain.hairdresser.user.models.Image;
import apps.trichain.hairdresser.user.models.Service;
import apps.trichain.hairdresser.utils.AppUtils;
import apps.trichain.hairdresser.utils.SharedPrefManager;
import apps.trichain.hairdresser.utils.badge.BadgeView;

import static apps.trichain.hairdresser.utils.AppUtils.displayToast;

public class ServiceDetailActivity extends AppCompatActivity  implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener, View.OnClickListener {
    private TextView servicedesc,service_title,tv_service_price;
    ArrayList<String> imgaurls ;
    private static final String TAG = "Service Detail";
    private SliderLayout sliderLayout;
    BadgeView badgeView;
    ImageView cart,back;
    MaterialButton addtocart;
    private CartRepository cartRepository;
    private  Bitmap bitmapimage=null;
    private Integer pricecrt;
    private String titlecrt;
    private String idcrt;
    private AtomicInteger itemcount = new AtomicInteger();
    SharedPrefManager sharedPrefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        initUI();

       Service service = (Service) getIntent().getSerializableExtra("serviceitem");
        assert service != null;
        servicedesc.setText(service.getDescription());
        service_title.setText(service.getTitle());
        tv_service_price.setText(AppUtils.FormatCurrency(Integer.parseInt(service.getPrice())));
        pricecrt = Integer.parseInt(service.getPrice());
        titlecrt = service.getTitle();
        idcrt = service.getId();
        loadProductImgSlider(service.getImage());
        initSlider();
    }


    private void initUI(){
        servicedesc =findViewById(R.id.service_desc);
        service_title =findViewById(R.id.service_title);
        tv_service_price =findViewById(R.id.tv_service_price);
        sliderLayout =findViewById(R.id.serviceimageslider);
        addtocart =findViewById(R.id.addtocart);
        back =findViewById(R.id.back);
        cart =findViewById(R.id.cart);
        back.setOnClickListener(this);
        addtocart.setOnClickListener(this);
        badgeView = new BadgeView(this);
        cartRepository = new CartRepository(this);
        sharedPrefManager = SharedPrefManager.getInstance(this);
        cart.setOnClickListener(this);
        cartRepository.getItems().observe(ServiceDetailActivity.this,allitems ->{
            Integer count =allitems.size();
            setCartCount(count);
        });
        badgeView.setOnClickListener(view -> {
            startActivity(new Intent(ServiceDetailActivity.this,CartItemsActivity.class));
            overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addtocart:
                if (bitmapimage ==null){
                    bitmapimage = BitmapFactory.decodeResource(this.getResources(), R.drawable.logo);
                }
                cartRepository.getService(idcrt).observe((LifecycleOwner) ServiceDetailActivity.this, carst -> {
                    if (carst.size() <=0 ){
                        cartRepository.insertItem(idcrt,titlecrt, pricecrt,AppUtils.getImageBytes(bitmapimage));
                        cartRepository.getService(idcrt).removeObservers(ServiceDetailActivity.this);
                        Integer items = Integer.parseInt(badgeView.getBadgeText());
                        items ++;
                        setCartCount( Integer.parseInt(String.valueOf(items)));
                     displayToast(ServiceDetailActivity.this,true,"Successfully added to cart");
                    }if (carst.size()>0){
                        displayToast(ServiceDetailActivity.this,false,"Service already in cart!");
                    }
                });
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.cart:
                startActivity(new Intent(ServiceDetailActivity.this,CartItemsActivity.class));
                overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
                break;
        }
    }


    private void setCartCount(Integer count) {
        badgeView.bindTarget(cart).setBadgeText( "" + count);
       badgeView.setBadgeTextSize(10,true);
       badgeView.setBadgeTextColor(getResources().getColor(R.color.colorAccent));
       badgeView.setBadgeBackgroundColor(getResources().getColor(R.color.colorWhite));
    }

    public void loadProductImgSlider(List<Image> imageList){
        imgaurls = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            Image images= imageList.get(i);
            if (images.getType().equals("gallery")){
                imgaurls.add(images.getImage_path());
                Log.e("Msg: image: ", "----- "+images.getImage_path());
            }else if (images.getType().equals("main_image")){
                Thread thread = new Thread(() -> {
                    try {
                        URL url = new URL(images.getImage_path());
                        bitmapimage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        if (bitmapimage ==null){
                            bitmapimage = BitmapFactory.decodeResource(this.getResources(), R.drawable.logo);
                            displayToast(ServiceDetailActivity.this,false,"nulling");
                        }
                    } catch(IOException e) {
                        System.out.println(e);
                    }
                });
                thread.start();

            }else {
                Thread thread = new Thread(() -> {
                    bitmapimage = BitmapFactory.decodeResource(this.getResources(), R.drawable.logo);
                });
                thread.start();
            }
        }
    }

    private void initSlider() {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.placeholder(R.drawable.placeholder)
        //.error(R.drawable.placeholder);

        for (int i = 0; i < imgaurls.size(); i++) {
            DefaultSliderView sliderView = new DefaultSliderView(this);
            // if you want show image only / without description text use DefaultSliderView instead

            // initialize SliderLayout
            sliderView
                    .image(imgaurls.get(i))
//                    .description(imgaurls.get(i))
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(this);

            //add your extra information
            sliderView.bundle(new Bundle());
//            sliderView.getBundle().putString("extra", listName.get(i));
            sliderLayout.addSlider(sliderView);
        }

        // set Slider Transition Animation
        // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);

        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
        sliderLayout.addOnPageChangeListener(this);
        sliderLayout.stopCyclingWhenTouch(false);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        cartRepository.getItems().observe(ServiceDetailActivity.this,allitems ->{
            Integer count =allitems.size();
            setCartCount(count);
        });
    }
}
