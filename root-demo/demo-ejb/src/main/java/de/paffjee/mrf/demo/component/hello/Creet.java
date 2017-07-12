package de.paffjee.mrf.demo.component.hello;

import lombok.Value;

/**
 * Created by philippbeyerlein on 11.07.17.
 */
@Value
public class Creet {

    private String creetingFormula;

    Creet(String creetingFormula)
    {
        this.creetingFormula = creetingFormula;
    }




}
