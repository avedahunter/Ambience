package edu.rit.csci759.fuzzylogic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleExpression;
import net.sourceforge.jFuzzyLogic.rule.RuleTerm;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethod;
import edu.rit.csci759.rspi.RpiIndicatorImpl;

public class RspiBlinds {
	
	private double blindPos;
	private RpiIndicatorImpl objRpi;
	
	private static final String filename = "FuzzyLogic/LightTemp.fcl";
	private static final FIS fis = FIS.load(filename, true);
	private static final String ruleBlockName = "No1"; 
	
	// Get default function block
	private static final FunctionBlock fb = fis.getFunctionBlock(null);
	
	public RspiBlinds(RpiIndicatorImpl _objRpi) {
		
		objRpi = _objRpi;

		synchronized (objRpi) {
			int currentLightItensity = objRpi.read_ambient_light_intensity();
			int currentTemp = objRpi.read_temperature();

			blindPos = evaluateFuzzylogic(currentLightItensity, currentTemp);
			highlightAppropriateLED(blindPos);
		}

	}

	//Return the double value of the blind position
	public double getBlindPos(){
		return blindPos;
	}
	
	//Set the double value of the blind position
	public void setBlindPos(double val){
		blindPos = val;
		highlightAppropriateLED(val);
	}
	
	//Get the list of current fuzzy logic rules
	public ArrayList<String> getFuzzyLogicRules() {
		
		ArrayList<String> retArr = new ArrayList<String>();
		
		synchronized (fb) {
			
			RuleBlock objRB = fb.getFuzzyRuleBlock(ruleBlockName);
			String str;
			for(Rule objRule : objRB.getRules()){
				
				str = objRule.toString();
				int beginIndex = str.indexOf("if");
				int endIndex = str.indexOf("[");
				str = str.substring(beginIndex, endIndex);
				
				retArr.add(str);
			}
			System.out.println("Fuzzy logic rules retrieved: \n"+ objRB);
		}
		
		return retArr;
	}
	
	//Delete a rule specified by the index in the params of the JSONRPC2Request
	public String deleteRule(JSONRPC2Request req, RpiIndicatorImpl _objRPI) {
		
		String retStr = "";
		synchronized (fb) {
			
			int index = Integer.parseInt(req.getNamedParams().get("index").toString() );
			RuleBlock objRB = fb.getFuzzyRuleBlock(ruleBlockName);	
					
			objRB.remove(objRB.getRules().get(index) );
			System.out.println("Rule at index: "+ index + " deleted.");
			
			synchronized (_objRPI) {
				retStr = highlightAppropriateLED( (evaluateFuzzylogic(_objRPI.read_ambient_light_intensity(), _objRPI.read_temperature()) ) );
			}
			
		}
		return retStr;
	}
	
	
	//Add an additional rule to the fuzzy logic block and evaluate the new fuzzy logic
	//and highlight the appropriate LED according to the new blind position
	public String addRule(JSONRPC2Request req, RpiIndicatorImpl _objRPi){
		
		if(req.getNamedParams().size() != 4){
			System.out.println("Insufficient parameters supplied by client");
			return "Insufficient parameters supplied by client";
		}
		
		synchronized (fb) {
					
			RuleBlock objRB = fb.getFuzzyRuleBlock(ruleBlockName);
			
			//Create a new rule
			Rule objRule = new Rule(Integer.toString( objRB.getRules().size()+1), objRB);
			RuleExpression objAntecedent = new RuleExpression();
			
			//Extract parameters sent by the client
			Map<String, Object>tempMapOfParams = req.getNamedParams();	
			Object [] arrOfVars = tempMapOfParams.keySet().toArray();
			
			boolean singleVar = false;
			
			//Check for the temperature variable
			if(! ((String)tempMapOfParams.get((String)arrOfVars[1])).equalsIgnoreCase("any") ){
				objAntecedent.add(new RuleTerm(fb.getVariable("ambientTemp"), (String)tempMapOfParams.get("ambientTemp"),false ) );
				singleVar = true;
			}
			
			//Check for the ambient light variable
			if(! ((String)tempMapOfParams.get((String)arrOfVars[3])).equalsIgnoreCase("any") ){
				objAntecedent.add(new RuleTerm(fb.getVariable("ambientLight"),(String)tempMapOfParams.get("ambientLight"),false));
				singleVar = true;
			}
			
			// Conjunction between the two antecedent variables
			if(!singleVar){
				RuleConnectionMethod objRuleConnMethod = objAntecedent.getRuleConnectionMethod();
				objRuleConnMethod.connect(fb.getVariable("ambientLight").getValue(), fb.getVariable("ambientTemp").getValue());
				String str;
				if( ((String)tempMapOfParams.get("connector")).equalsIgnoreCase("or")  )
					str = "or";
				else
					str = "and";
				objRuleConnMethod.setName(str);	
			}
			
			//Add the variables and the connector to the rule
			objRule.setAntecedents(objAntecedent);
			objRule.addConsequent(fb.getVariable("blindPosition"), (String)tempMapOfParams.get("blindPosition") , false);	//OutputVariable - Blind Pos
			
			//Add the rule to the rule block 
			objRB.add(objRule);	
					
			//Evaluate the new fuzzy logic and highlight the appropriate LED
			String retStr = "";			
			synchronized(_objRPi){
				retStr = highlightAppropriateLED( (evaluateFuzzylogic(_objRPi.read_ambient_light_intensity(), _objRPi.read_temperature()) ) );
			}
			
			return retStr;
		}
		
	}

	//Edit an existing rule to the fuzzy logic block and evaluate the new fuzzy logic
	//and highlight the appropriate LED according to the new blind position
	public String editRule(JSONRPC2Request req, RpiIndicatorImpl objRp) {

		if(req.getNamedParams().size() != 5){
			System.out.println("Insufficient parameters supplied by client");
			return "Insufficient parameters supplied by client";
		}
		
		synchronized (fb) {
			
			//Extract the parameters sent by the client
			Map<String, Object>tempMapOfParams = req.getNamedParams();	
			Object [] arrOfVars = tempMapOfParams.keySet().toArray();	
			
			//Extract the specified rule from the rule block
			RuleBlock objRB = fb.getFuzzyRuleBlock(ruleBlockName);
			Rule objRule = objRB.getRules().get( Integer.parseInt( tempMapOfParams.get("index").toString() ) );	//Rule to be edited
			
			boolean singleVar = false;
			RuleExpression objAntecedent = new RuleExpression();
			
			//Check for the "ambientTemp" variable
			if(! ((String)tempMapOfParams.get((String)arrOfVars[1])).equalsIgnoreCase("any") ){
				objAntecedent.add(new RuleTerm(fb.getVariable("ambientTemp"), (String)tempMapOfParams.get("ambientTemp"),false ) );//Temp
				singleVar = true;
			}
			
			//Check for the "ambientLight" variable
			if(! ((String)tempMapOfParams.get((String)arrOfVars[3])).equalsIgnoreCase("any") ){
				objAntecedent.add(new RuleTerm(fb.getVariable("ambientLight"), (String)tempMapOfParams.get("ambientLight"), false));//Light
				singleVar = true;
			}
			
			//Check for the connector between the variables
			if(!singleVar){
				RuleConnectionMethod objRuleConnMethod = objAntecedent.getRuleConnectionMethod();
				objRuleConnMethod.connect(fb.getVariable("ambientLight").getValue(), fb.getVariable("ambientTemp").getValue());
				String str;
				if( ((String)tempMapOfParams.get("connector")).equalsIgnoreCase("or") )
					str = "or";
				else
					str = "and";
				objRuleConnMethod.setName(str);
			}
			
			//Set the antecedent variables with the connector between them
			objRule.setAntecedents(objAntecedent);
			
			//Set the consequent term
			RuleTerm objConsequent = new RuleTerm(fb.getVariable("blindPosition"), (String)tempMapOfParams.get("blindPosition") , false);	//OutputVariable - Blind Pos 
			LinkedList<RuleTerm> listOfConsequents = new LinkedList<RuleTerm>();
			listOfConsequents.add(objConsequent);
			
			//Set the consequent variable
			objRule.setConsequents(listOfConsequents);	//Set the consequents
			
			System.out.println("Rule at index "+tempMapOfParams.get("index").toString() + "was edited.");
			
			String retStr = "";			
			synchronized(objRp){
				retStr = highlightAppropriateLED( (evaluateFuzzylogic(objRp.read_ambient_light_intensity(), objRp.read_temperature()) ) );
			}
			
			return retStr;
			
		}
		
	}
	
	public String highlightAppropriateLED(double blindPos) {
		
		String str;
		synchronized (objRpi) {

			objRpi.led_all_off();	//Switch Off All LEDs first
			
			if(blindPos<= 25 && blindPos >0){
				str = "open";
				System.out.println("blindPos: "+blindPos + "- Blind is open");
				objRpi.led_when_low();
			}
			else if(blindPos > 25 && blindPos <=75){
				str = "half-open";
				System.out.println("blindPos: "+blindPos + "- Blind is half open");
				objRpi.led_when_mid();
			}
			else{
				str = "closed";
				System.out.println("blindPos: "+blindPos+"- Blind is closed");
				objRpi.led_when_high();			
			}
			
		}

		return str;
			
	}

	
	
	public double evaluateFuzzylogic(int currentLightItensity, int currentTemp) {
		
		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			return -1;
		}
		
		System.out.println("RuleBlock while evaluating fuzzy logic is as follows:\n");
		System.out.println(fb.getFuzzyRuleBlock(ruleBlockName));
		
		fb.setVariable("ambientTemp", currentTemp);
		fb.setVariable("ambientLight", currentLightItensity);
        
		System.out.println("Current Ambient Temp: "+ currentTemp +". Current Ambient LightIntensity: " +currentLightItensity );

		// Evaluate
		fb.evaluate();

		// Show output variable's chart
		fb.getVariable("blindPosition").defuzzify();
		
		System.out.println("BlindPosition: " + fb.getVariable("blindPosition").getValue() );
		blindPos = (fb.getVariable("blindPosition").getValue()); 
		
		return blindPos;
	}

}
