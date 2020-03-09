/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.krzyczak.camelbeans;

import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;

/**
 *
 * @author krzyczak
 */
class Utils {
//    public void readCsv(Exchange exchange) throws ClassNotFoundException {
//        GenericFile file = (GenericFile) exchange.getIn().getBody();
//        System.out.println("DATA: ---" + file.split("\n"));
//    }
    
    public void standardizeCase(Exchange exchange) throws ClassNotFoundException {
        java.util.ArrayList zzz = (java.util.ArrayList) exchange.getIn().getBody();
        String element = (String) zzz.get(0).toString().toLowerCase();
        char aaa = element.charAt(0);
        String bbb = ("" + aaa).toUpperCase();
        zzz.set(0, bbb + element.substring(1));
        exchange.getIn().setBody(zzz);
    }
}
