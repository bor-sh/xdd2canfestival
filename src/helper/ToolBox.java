/**
 * Copyright (c) 2010-2012 Miko Kinski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in 
 * the Software without restriction, including without limitation the rights to 
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies 
 * of the Software, and to permit persons to whom the Software is furnished to do 
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */

package helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolBox {

	public final static int transformHex2IntAll(String aString){
		return Integer.parseInt(aString,16);
	}
	
	public final static String expandXTimes(String enter, Integer times){
		
		String val = "";
		
		for(int i = 0; i<times;i++){
			if(i==times-1){
				val = val+enter;	
			} else {
				val = val+enter+" ,";
			}
		}
			
		return val;
	}
	
	private static int calculateIndex(String[] canObjectList, String min, String max, int flag){
		int fidx = 0;
		int lidx = 0;
		int idx  = 0;
		Pattern pattern 		= Pattern.compile(",index=.*?,");
		Matcher m;
		Boolean taken			= false;
		Boolean taken2			= false;
		int counter				= 0;
		int index				= 0;
		int minrange			= Integer.valueOf(min,16);
		int maxrange			= Integer.valueOf(max,16);
//		int[] indexValues 		= new int[2];

		for(int i=0;i<canObjectList.length;i++){
				m						= pattern.matcher(canObjectList[i]);			
				if(m.find()){
					index = transformHex2IntAll(m.group().substring(7, m.group().length()-1));
//					System.out.println(index);
//					System.out.println(maxrange);					
					if( index >=minrange && index <=maxrange && !taken){
						fidx = counter;
						taken = true;
					} else if(index > minrange && index > maxrange && !taken2){
//						System.out.println("test");
						if(taken)lidx   = counter-1;
						taken2 = true;
					}
					counter++;
				}
	   	}
		
		if(flag == 0)
			idx	= fidx;
		else
			idx	= lidx;
		
		return idx;
	}
	
	public final static int quickIndex(String list, String part, Integer flag) {
		//flag = 0 -> firtIndex
		//flag = 1 -> lastIndex
		
		String[] canObjectList 	= list.split("caNopen::CANopenObjectType");
				
		if(part.matches("SDO_SVR")){ // range (0x1200) - (0x127F)
			return calculateIndex(canObjectList,"1200","127F",flag);
		}
		if(part.matches("SDO_CLT")){ // range (0x1280) - (0x12FF)
			return calculateIndex(canObjectList,"1280","12FF",flag);
		}	
		if(part.matches("PDO_RCV")){ // range (0x1400) - (0x15FF)
			return calculateIndex(canObjectList,"1400","15FF",flag);
		}	
		if(part.matches("PDO_RCV_MAP")){ // range (0x1600) - (0x17FF)
			return calculateIndex(canObjectList,"1600","17EF",flag);
		}	
		if(part.matches("PDO_TRS")){ // range (1800) - (0x19FF)
			return calculateIndex(canObjectList,"1800","19FF",flag);
		}	
		if(part.matches("PDO_TRS_MAP")){ // range (0x1A00) - (0x1BFF)
			return calculateIndex(canObjectList,"1A00","1BFF",flag);
		}	
					
		return 0;
	}
	
	public static boolean isinCommunicationList(String list, String idx){

		Pattern pattern 		= Pattern.compile(",index=.*?,");
		Matcher m;
		String[] canObjectList 	= list.split("caNopen::CANopenObjectType");
		Boolean found 			= false;
		String index				= "";

//        System.out.println(list);

		for(int i=0;i<canObjectList.length;i++){
				m						= pattern.matcher(canObjectList[i]);			
				if(m.find()){
					index = m.group().substring(7, m.group().length()-1);
					//System.out.println(index);
					//System.out.println(idx);
					if( index.matches(idx)){
						found = true;
					}
				}
	   	}
		
		return found;
	}	
}
