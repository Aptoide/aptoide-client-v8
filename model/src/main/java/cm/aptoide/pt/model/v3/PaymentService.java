/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.model.v3;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Created by rmateus on 21-05-2014.
 */
@Data
public class PaymentService {

    private int id;

    @JsonProperty("short_name")
    private String shortName;

    private String name;

    private ArrayList<PaymentType> types;

    private double price;

    private String currency;

    private double taxRate;

    private String sign;

    @Data
    public static class PaymentType {

        private String reqType;
        private String label;
    }
}