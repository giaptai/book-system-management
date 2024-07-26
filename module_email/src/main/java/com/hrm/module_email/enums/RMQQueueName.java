package com.hrm.module_email.enums;

import lombok.Getter;

@Getter
public enum RMQQueueName {
    REGISTER("REGISTER"),
    CHANGE_PASSWORD("CHANGE-PASSWORD"),
    CHANGE_EMAIL("CHANGE-EMAIL"),
    PURCHASE("PURCHASE"),
    PURCHASE_STATE("PURCHASE-STATE"),
    ;
    final String value;

    RMQQueueName(String value) {
        this.value = value;
    }

}
