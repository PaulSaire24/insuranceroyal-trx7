package com.bbva.pisd.lib.r030.impl.factory;

import com.bbva.pisd.lib.r030.impl.util.Constants;

public class FactoryRequestQuotation {

    public static RequestSchedule getRequestRimac(String insuranceBusinessName, String productShortDesc){
        if(insuranceBusinessName.equals(Constants.BUSINESS_NAME_VIDA)){
            return new RequestLife(productShortDesc);
        }else{
            return new RequestNoLife();
        }
    }

}
