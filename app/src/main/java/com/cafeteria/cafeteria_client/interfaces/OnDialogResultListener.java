package com.cafeteria.cafeteria_client.interfaces;

import com.cafeteria.cafeteria_client.data.OrderedMeal;

/**
 * Created by anael on 16/09/16.
 */
public interface OnDialogResultListener {
    public abstract void onPositiveResult(OrderedMeal meal);
    public abstract void onNegativeResult(OrderedMeal meal);
}
