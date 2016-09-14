package com.cafeteria.cafeteria_client.data;


/**
 *
 * @author Shira Elitzur
 *
 * An instance of this class represents Drink item.
 *
 */
public class Drink extends Item {

    /**
     * Item can stand alone or not but Drink always can, so i hide the super class field
     */
    private boolean isStandAlone = true;

}
