package com.salim3dd.inappbilling;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.salim3dd.inappbilling.util.IabHelper;
import com.salim3dd.inappbilling.util.IabResult;
import com.salim3dd.inappbilling.util.Inventory;
import com.salim3dd.inappbilling.util.Purchase;

import java.util.Random;

public class InAppBillingActivity extends AppCompatActivity {

    private static final String TAG = "InAppBilling";
    IabHelper mHelper;
    static final String ITEM_SKU = "com.salim3dd.btnclickme";

    private Button clickButton;
    private Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_billing);

        buyButton = (Button) findViewById(R.id.buyButton);
        clickButton = (Button) findViewById(R.id.btnClickME);

        clickButton.setEnabled(false);
        clickButton.setVisibility(View.INVISIBLE);

        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhW5Q0OlZ9BW3rtylifmWcLabwamc/ztz8PfrxFttxoO44gynEigZbZgczvjz2uNqjtoGMK1I83nxPH7+qZnwyOY5ih9M6o/8MicnKd6yq2/4NwLD1eQxNr9E0J0RT00mj8JWiPGrwO3rDGu61s4o99CdaJRdRVzjnY/QNs0H2idXT12cbGdnIia8OEWQvE+SuHV6QN4Ofdu/drus/REnIHNPiXyZAlXmwezrQxatL6xJ95jJnTZtG1WlDsvbvAKQsHkRFAVLJFTzflgzYkMeujjDO+gIlBQ/iUHlkKg24TBWXRZAOinSlxLN2/zEd3ERJ8ex0pCIvJkgAI3aVcF74QIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               Log.d(TAG, "In-app Billing setup failed: " +
                                                       result);
                                           } else {
                                               Log.d(TAG, "In-app Billing is set up OK");
                                           }
                                       }
                                   });
    }

    public void buttonClicked(View view) {
        clickButton.setEnabled(false);
        buyButton.setEnabled(true);
    }

    public void buyClick(View view) {
        Random Rand = new Random();
        int Rndnum = Rand.nextInt(10000) + 1;
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "token-" + Rndnum);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
                buyButton.setEnabled(false);
            }

        }
    };

    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {

                        clickButton.setEnabled(true);
                        clickButton.setVisibility(View.VISIBLE);

                    } else {
                        // handle error
                    }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }


}
