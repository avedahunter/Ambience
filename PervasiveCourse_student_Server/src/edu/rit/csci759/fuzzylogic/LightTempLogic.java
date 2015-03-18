package edu.rit.csci759.fuzzylogic;

import edu.rit.csci759.rspi.RpiIndicatorImpl;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class LightTempLogic {
	
	RpiIndicatorImpl objRpi;
	LightTempLogic(RpiIndicatorImpl _objRpi){
		objRpi = _objRpi;
	}
	
	public int getBlindPostion(int temp, int ambLight){
		int blindPos =0;
		
		return blindPos;
	}
	
	public static void main(String[] args) throws Exception {
	


		

		
		
	
	
		}
////		// Get default function block
////		FunctionBlock fb = fis.getFunctionBlock(null);
////
////		// Set inputs
////		int temp = objRpi.read_temperature();
////		int ambLight = objRpi.read_temperature();
////		
////		fb.setVariable("ambientTemp", temp);
////		fb.setVariable("ambientLight", ambLight);
////
////		// Evaluate
////		fb.evaluate();
////		
////
////		// Show output variable's chart
////		fb.getVariable("blindPosition").defuzzify();
////
////		// Print ruleSet
////		System.out.println(fb);
////		System.out.println("Ambient Light: " + fb.getVariable("ambientLight").getValue());
////		System.out.println("Ambient Temp: " + fb.getVariable("ambientTemp").getValue());
////		System.out.println("BlindPosition: " + fb.getVariable("blindPosition").getValue() );
//
//		
////		RpiIndicatorImpl.GPIOshutDown();
	

}
