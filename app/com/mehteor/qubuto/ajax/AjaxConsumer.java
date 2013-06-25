package com.mehteor.qubuto.ajax;

import com.mehteor.qubuto.ajax.action.AjaxAction;

import play.Logger;

/**
 * TODO
 * @author Rémy 'remeh' MATHIEU
 */
public class AjaxConsumer {
    public static void consume(AjaxAction action) {
        System.out.println(action.toJson().toString());
    }
}
