package com.example.groceryapp;

import android.os.Bundle;

public class ShopActivity extends GeneralPage {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUserData(R.layout.activity_shop);
    }
}