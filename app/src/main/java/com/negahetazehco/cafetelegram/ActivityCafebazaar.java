package com.negahetazehco.cafetelegram;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.LocaleController;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.negahetazehco.cafetelegram.util.IabHelper;
import com.negahetazehco.cafetelegram.util.IabResult;
import com.negahetazehco.cafetelegram.util.Inventory;
import com.negahetazehco.cafetelegram.util.Purchase;

import java.util.ArrayList;


public class ActivityCafebazaar extends Activity {

    private final static String      TAG         = "ActivityCafebazaar";
    private static String            URL_WS_SERVER    = "http://ws.negahetazehco.com/";
    private static ArrayList<String> SKU_PREMIUM = new ArrayList<String>();
    private static final int                 RC_REQUEST  = 0;
    private static IabHelper mHelper;
    private static final String      APIKEY      = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwC/GyDDCXiRMjWem23hJvcJMOuowYUJ/byV4zmVnv1JTCK4Au1jWg+ybTZrMOuNWE9KwsNQPE2ATDtJK7VRFU27XsAJSp9cdmrAhTPV7Z/aVWscWI4D84z4Vcns3NHwtXmGPSQs1oMpE9VUOcwOQNubXHf9th4UitM3NAFfaLoY95644ttdIarXE40vyLftrtwMqxaXEJN6mbEqUHIxq1sTBBNk0Qs3t2mB5+3O4asCAwEAAQ==";

    private ProgressDialog            dialog;
    private String                   developerPayload;

    private String                   API_TYPE;
    private int                      SKU_INDEX   = 0;
    private Boolean                  resultForBack;
    private Handler HANDLER = null;
    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafebazaar);
        context = getApplicationContext();
        SKU_PREMIUM.add("cafegram");
        HANDLER = new Handler();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("API_TYPE")) {
                API_TYPE = extras.getString("API_TYPE");
            }
            if (extras.containsKey("SKU_INDEX")) {
                SKU_INDEX = extras.getInt("SKU_INDEX");
            }
        }

        if (Helper.isConnectingToInternet(context) && Helper.isPackageInstalled(context, "com.farsitel.bazaar")) {

            mHelper = new IabHelper(this, APIKEY);
            Log.d(TAG, "Starting setup.");
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

                @Override
                public void onIabSetupFinished(IabResult result) {
                    Log.d(TAG, "Setup finished.");

                    if ( !result.isSuccess()) {
                        // Oh noes, there was a problem.
                        Toast.makeText(context, R.string.premiumSetupError, Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Problem setting up In-app Billing: " + result);
                        resultForBack = false;
                        returnBack();

                    } else {
                        // Hooray, IAB is fully set up!
                        Log.d(TAG, "Start For Billing ");

                        if ("Consumable".equals(API_TYPE)) {
                            // kharidhaye masraf shodani
                            try {
                                developerPayload = Helper.getRandomString();
                                mHelper.launchPurchaseFlow(ActivityCafebazaar.this, SKU_PREMIUM.get(SKU_INDEX), RC_REQUEST, mPurchaseFinishedListener, developerPayload);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "Start For Consumable  Billing Failed");
                                Toast.makeText(context, context.getString(R.string.PremiumFail), Toast.LENGTH_SHORT).show();
                                resultForBack = false;
                                returnBack();
                            }

                        } else if ("CheckConsumable".equals(API_TYPE)) {
                            // masraf kardane kharid haye ghabli karbar
                            mHelper.queryInventoryAsync(ConsumableInventoryListener);

                        } else if ("NonConsumable".equals(API_TYPE)) {
                            try {
                                // kharidhaye masraf nashodani  
                                developerPayload = Helper.getUniquePsuedoID();
                                mHelper.launchPurchaseFlow(ActivityCafebazaar.this, SKU_PREMIUM.get(SKU_INDEX), RC_REQUEST, mPurchaseFinishedListener, developerPayload);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "Start For NonConsumable Billing Failed");
                                Toast.makeText(context, context.getString(R.string.PremiumFail), Toast.LENGTH_SHORT).show();
                                resultForBack = false;
                                returnBack();
                            }
                        } else if ("CheckNonConsumable".equals(API_TYPE)) {
                            // barresi vaziyate kharid
                            developerPayload = Helper.getUniquePsuedoID();
                            mHelper.queryInventoryAsync(mGotInventoryListener);
                        }

                    }

                }
            });
        } else if ( !Helper.isPackageInstalled(context, "com.farsitel.bazaar")) {
            Toast.makeText(context, context.getString(R.string.bazaarInstall), Toast.LENGTH_LONG).show();
            resultForBack = false;
            returnBack();
        } else {
            Toast.makeText(context, context.getString(R.string.ConnectToNet), Toast.LENGTH_LONG).show();
            resultForBack = false;
            returnBack();
        }

    }


    private void returnBack() {
        if(resultForBack) {
            Setting.set(Setting.HIDDEN_OPTION_STATUS_LABLE, "sadwad254w84dsdf6");
            final DialogAlert dialog = new DialogAlert(ActivityCafebazaar.this, R.style.DialogAnimation);
            dialog.setCancelable(false);
            dialog.txtDesc.setText(LocaleController.getString("premiumActive", R.string.premiumActive));
            dialog.txtDesc.setGravity(Gravity.RIGHT | Gravity.CENTER);
            dialog.positive.setText(LocaleController.getString("OK", R.string.OK));
            dialog.positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.resetApp((Activity) context);
                }
            });
            dialog.negative.setVisibility(View.GONE);
            dialog.show();
        } else {
            ActivityCafebazaar.this.finish();
        }
    }

    private int                              checkConsumable              = 0;
    IabHelper.QueryInventoryFinishedListener ConsumableInventoryListener  = new IabHelper.QueryInventoryFinishedListener() {

                                                                              @Override
                                                                              public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                                                                                  Log.d(TAG, "ConsumableInventoryListener finished.");

                                                                                  if (result.isFailure()) {
                                                                                      Log.d(TAG, "Failed to ConsumableInventoryListener: " + result);
                                                                                      resultForBack = false;
                                                                                      returnBack();
                                                                                      return;
                                                                                  }
                                                                                  else {
                                                                                      Log.d(TAG, "ConsumableInventoryListener was successful.");

                                                                                      //for (int i = 0; i < SKU_PREMIUM.size(); i++) {
                                                                                      if (inventory.hasPurchase(SKU_PREMIUM.get(checkConsumable))) {
                                                                                          Purchase purchase = inventory.getPurchase(SKU_PREMIUM.get(checkConsumable));
                                                                                          Purchase purchase1 = null;
                                                                                          try {
                                                                                              purchase1 = new Purchase(purchase.getItemType(), purchase.getOriginalJson(), purchase.getSignature());
                                                                                              mHelper.consumeAsync(purchase1, consumeFinishedListenerMulti);
                                                                                          }
                                                                                          catch (JSONException e) {
                                                                                              e.printStackTrace();
                                                                                              checkConsumable++;
                                                                                          }
                                                                                      } else {
                                                                                          checkConsumable++;
                                                                                          if (checkConsumable < SKU_PREMIUM.size()) {
                                                                                              mHelper.queryInventoryAsync(ConsumableInventoryListener);

                                                                                          } else {
                                                                                              resultForBack = true;
                                                                                              returnBack();
                                                                                              return;
                                                                                          }
                                                                                      }

                                                                                      Log.d(TAG, "ConsumableInventoryListener End");
                                                                                      return;
                                                                                  }

                                                                              }

                                                                          };
    IabHelper.OnConsumeFinishedListener      consumeFinishedListenerMulti = new IabHelper.OnConsumeFinishedListener() {

                                                                              public void onConsumeFinished(Purchase purchase, IabResult result) {
                                                                                  if (result.isSuccess()) {}
                                                                                  checkConsumable++;
                                                                                  if (checkConsumable < SKU_PREMIUM.size()) {
                                                                                      mHelper.queryInventoryAsync(ConsumableInventoryListener);
                                                                                  } else {
                                                                                      resultForBack = true;
                                                                                      returnBack();
                                                                                      return;
                                                                                  }
                                                                              }
                                                                          };

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener        = new IabHelper.QueryInventoryFinishedListener() {

                                                                              @Override
                                                                              public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                                                                                  Log.d(TAG, "Query inventory finished.");

                                                                                  if (result.isFailure()) {
                                                                                      Log.d(TAG, "Failed to query inventory: " + result);
                                                                                      resultForBack = false;
                                                                                      returnBack();
                                                                                      return;
                                                                                  }
                                                                                  else {

                                                                                      checkBuyToken(inventory.getPurchase(SKU_PREMIUM.get(SKU_INDEX)).getToken(), null, null, null);

                                                                                      /*Log.d(TAG, "Query inventory was successful.");
                                                                                      resultForBack = inventory.hasPurchase(SKU_PREMIUM.get(SKU_INDEX));
                                                                                      Log.d(TAG, "User is " + (resultForBack ? "PREMIUM" : "NOT PREMIUM"));
                                                                                      Toast.makeText(getApplicationContext(), resultForBack ? R.string.premium : R.string.notpremium, Toast.LENGTH_LONG).show();
                                                                                      returnBack();
                                                                                      Log.d(TAG, "Initial inventory query finished; enabling main UI.");
                                                                                      return;
                                                                                      */
                                                                                  }

                                                                              }

                                                                          };

    IabHelper.OnIabPurchaseFinishedListener  mPurchaseFinishedListener    = new IabHelper.OnIabPurchaseFinishedListener() {

                                                                              @Override
                                                                              public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                                                                                  Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
                                                                                  if (result.isFailure()) {
                                                                                      Log.d(TAG, "Error purchasing: " + result);
                                                                                      Toast.makeText(context, R.string.PremiumFail, Toast.LENGTH_LONG).show();
                                                                                      resultForBack = false;
                                                                                      returnBack();
                                                                                      return;
                                                                                  } else {
                                                                                      Log.d(TAG, "Purchase successful.");
                                                                                      if (purchase.getSku().equals(SKU_PREMIUM.get(SKU_INDEX))) {
                                                                                          Log.i(TAG, "back of bazaar ok.");

                                                                                          if ("NonConsumable".equals(API_TYPE)) {
                                                                                              checkBuyToken(purchase.getToken(), null, null, null);
                                                                                          } else if ("Consumable".equals(API_TYPE)) {

                                                                                              checkBuyToken(purchase.getToken(), purchase.getItemType(), purchase.getSignature(), purchase.getOriginalJson());

                                                                                          }

                                                                                      } else {
                                                                                          Log.i(TAG, "back of bazaar Fail.");
                                                                                          resultForBack = false;
                                                                                          returnBack();
                                                                                          return;
                                                                                      }
                                                                                  }
                                                                              }
                                                                          };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if ( !mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Destroying helper.");
        if (mHelper != null)
            mHelper.dispose();
        mHelper = null;
    }

    private int counterCheckBuy = 1;


    private void checkBuyToken(final String token, final String itemType, final String signature, final String json) {

        ArrayList<String> _lable = new ArrayList<String>();
        ArrayList<String> _value = new ArrayList<String>();

        _lable.add("action");
        _value.add("checkBuyToken");

        _lable.add("package");
        _value.add(context.getPackageName());

        _lable.add("product");
        _value.add(SKU_PREMIUM.get(SKU_INDEX));

        _lable.add("tokenid");
        _value.add(token);

        String url = URL_WS_SERVER + "cafebazaar/token/";

        C2D2 dataList = new C2D2()
                .label(_lable)
                .value(_value)
                .url(url)
                .context(context)
                .multiTime(false)
                .listener(new C2D2.Listener() {

                    @Override
                    public void onDataReceive(String data, Boolean status) {
                        if (status) {
                            Log.i("Data_Check_Buy", data);
                            parseCheckBuyToken(data, itemType, signature, json);
                        } else {
                            Log.i("Data_Check_Buy2", "faild.");
                            if (counterCheckBuy < 4) {
                                counterCheckBuy++;
                                checkBuyToken(token, itemType, signature, json);
                            } else {
                                HANDLER.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), R.string.PremiumCheckFail, Toast.LENGTH_LONG).show();
                                        resultForBack = false;
                                        returnBack();
                                    }
                                });

                            }
                        }
                    }
                }).start();
    }


    private void parseCheckBuyToken(String data, final String itemType, final String signature, final String json) {
        Log.d(TAG, "parseCheckBuyToken Start.");

        try {
            if (data.length() > 5) {
                JSONObject object = new JSONObject(data);
                if (object != null) {
                    // int consumptionState = object.getInt("consumptionState");
                    String kind = object.getString("kind");
                    String dPayload = object.getString("developerPayload");
                    Log.d(TAG, "dPayload: " + dPayload);

                    if (kind.equals("androidpublisher#inappPurchase") && dPayload.equals(developerPayload)) {
                        Log.d(TAG, "parseCheckBuyToken true.");

                        HANDLER.post(new Runnable() {

                            @Override
                            public void run() {
                                if ("NonConsumable".equals(API_TYPE) || "CheckNonConsumable".equals(API_TYPE)) {
                                    Toast.makeText(getApplicationContext(), R.string.premiumActive, Toast.LENGTH_LONG).show();
                                    resultForBack = true;
                                    returnBack();
                                    return;
                                } else if ("Consumable".equals(API_TYPE)) {
                                    Toast.makeText(getApplicationContext(), R.string.premiumActive, Toast.LENGTH_LONG).show();
                                    resultForBack = true;
                                    try {
                                        Purchase purchase1 = new Purchase(itemType, json, signature);
                                        mHelper.consumeAsync(purchase1, consumeFinishedListener);
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                        returnBack();
                                        return;
                                    }
                                }
                            }
                        });
                    } else {
                        HANDLER.post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.PremiumFail, Toast.LENGTH_LONG).show();
                                resultForBack = false;
                                returnBack();
                                return;
                            }
                        });
                    }
                } else {
                    HANDLER.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.PremiumCheckFail, Toast.LENGTH_LONG).show();
                            resultForBack = false;
                            returnBack();
                            return;
                        }
                    });
                }
            } else {
                HANDLER.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.PremiumCheckFail, Toast.LENGTH_LONG).show();
                        resultForBack = false;
                        returnBack();
                        return;
                    }
                });

            }
        }
        catch (JSONException e) {
            e.printStackTrace();

        }

    }

    IabHelper.OnConsumeFinishedListener consumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {

                                                                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                                                                        if (result.isSuccess()) {
                                                                            Log.d(TAG, "consumeFinishedListener true.");
                                                                        } else
                                                                            Log.d(TAG, "consumeFinishedListener false.");
                                                                        returnBack();
                                                                    }
                                                                };

    /*
    @Override
    public void onBackPressed() {
        resultForBack = false;
        returnBack();

    }
    */
}
