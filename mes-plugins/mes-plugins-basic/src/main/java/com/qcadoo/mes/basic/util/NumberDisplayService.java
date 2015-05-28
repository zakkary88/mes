package com.qcadoo.mes.basic.util;

import java.text.NumberFormat;

import org.springframework.stereotype.Service;

@Service
public class NumberDisplayService {
	
	private final static int PRICE_PRECISSION = 2;
	
	public String formatValue(Double value){
		NumberFormat nf = NumberFormat.getInstance(); 
		nf.setMaximumFractionDigits(PRICE_PRECISSION); 
		return nf.format(value);
	}
}

