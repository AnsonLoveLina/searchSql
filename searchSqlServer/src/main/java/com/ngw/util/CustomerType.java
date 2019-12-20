package com.ngw.util;

/**
 * 后台存到是fieldName而非stringValue
 */
public enum CustomerType {
    USER("user"),GROUP("group");
    private String customerType;

    CustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
}
