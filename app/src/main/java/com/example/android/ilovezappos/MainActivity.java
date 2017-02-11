package com.example.android.ilovezappos;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private ImageView mFirstItemImage;
    private TextView mBrandName;
    private TextView mProductName;
    private TextView mOriginalPrice;
    private TextView mPrice;
    private TextView mPriceLabel;

    private String currentItemLocation = "";

    private FloatingActionButton mAddToCartButton;
    private android.support.v7.widget.SearchView mSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAddToCartButton = (FloatingActionButton) findViewById(R.id.add_to_cart);
        mAddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.added_to_cart), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mSearchView = (SearchView) findViewById(R.id.sv_search_box);
        mSearchView.setQueryHint(getString(R.string.query_hint));
        mSearchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        makeSearchQuery(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                }
        );
        mFirstItemImage = (ImageView) findViewById(R.id.iv_first_item);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mBrandName = (TextView) findViewById(R.id.tv_brand_name);
        mProductName = (TextView) findViewById(R.id.tv_product_name);
        mOriginalPrice = (TextView) findViewById(R.id.tv_original_price);
        mPrice = (TextView) findViewById(R.id.tv_price);
        mPriceLabel = (TextView) findViewById(R.id.tv_label_price);
        initiateVisibilities();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            if (currentItemLocation != null && currentItemLocation != ""){
                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("CurrentItemLocation", currentItemLocation);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "URL has been copied to the clipboard", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeSearchQuery(String query) {
        //String query = mSearchBoxEditText.getText().toString();
        URL url = NetworkUtils.buildUrl(query, getString(R.string.base_url),
                getString(R.string.param_query), getString(R.string.key),
                getString(R.string.key_value));
        new ZapposItemQueryTask().execute(url);
    }

    private void initiateVisibilities() {
        mErrorMessageDisplay.setVisibility(View.GONE);
        mFirstItemImage.setVisibility(View.GONE);
        mBrandName.setVisibility(View.GONE);
        mProductName.setVisibility(View.GONE);
        mOriginalPrice.setVisibility(View.GONE);
        mPrice.setVisibility(View.GONE);
        mPriceLabel.setVisibility(View.GONE);
        mAddToCartButton.setVisibility(View.GONE);
        currentItemLocation = "";
    }

    private void showZapposItem() {
        mErrorMessageDisplay.setVisibility(View.GONE);
        mFirstItemImage.setVisibility(View.VISIBLE);
        mBrandName.setVisibility(View.VISIBLE);
        mProductName.setVisibility(View.VISIBLE);
        mOriginalPrice.setVisibility(View.VISIBLE);
        mPrice.setVisibility(View.VISIBLE);
        mPriceLabel.setVisibility(View.VISIBLE);
        mAddToCartButton.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mFirstItemImage.setVisibility(View.GONE);
        mBrandName.setVisibility(View.GONE);
        mProductName.setVisibility(View.GONE);
        mOriginalPrice.setVisibility(View.GONE);
        mPrice.setVisibility(View.GONE);
        mPriceLabel.setVisibility(View.GONE);
        mAddToCartButton.setVisibility(View.GONE);
    }


    public class ZapposItemQueryTask extends AsyncTask<URL, Void, ZapposItem> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ZapposItem doInBackground(URL... params) {
            URL searchUrl = params[0];
            ZapposItem zapposItem = null;
            try {
                String serachResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                JSONObject jresponse = new JSONObject(serachResults);
                JSONArray arr = new JSONArray(jresponse.getString("results"));
                JSONObject jObj = arr.getJSONObject(0);
                String imageLocation = jObj.getString("thumbnailImageUrl");
                InputStream in = new java.net.URL(imageLocation).openStream();
                zapposItem = new ZapposItem(
                        BitmapFactory.decodeStream(in),
                        jObj.getString("brandName"),
                        jObj.getString("productId"),
                        jObj.getString("originalPrice"),
                        jObj.getString("styleId"),
                        jObj.getString("colorId"),
                        jObj.getString("price"),
                        jObj.getString("percentOff"),
                        jObj.getString("productUrl"),
                        jObj.getString("productName"));

            } catch (IOException | JSONException e) {
                showErrorMessage();
                e.printStackTrace();
            }
            return zapposItem;
        }

        @Override
        protected void onPostExecute(ZapposItem zapposItem) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (zapposItem != null) {
                showZapposItem();
                mFirstItemImage.setImageBitmap(zapposItem.thumbnailImage);
                mBrandName.setText(zapposItem.brandName);
                mProductName.setText(zapposItem.productName);
                mPrice.setText(zapposItem.price);
                mOriginalPrice.setText(zapposItem.originalPrice);
                mOriginalPrice.setPaintFlags(mOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                currentItemLocation = zapposItem.productUrl;
            } else {
                showErrorMessage();
            }
        }
    }

}
