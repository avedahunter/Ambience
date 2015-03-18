package edu.rit.csci759.fuzzylogic;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.fcl.FclObject;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleExpression;
import net.sourceforge.jFuzzyLogic.rule.RuleTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethod;

public class MyTipperClass {
	public static void main(String[] args) throws Exception {
		String filename = "FuzzyLogic/tipper.fcl";
		FIS fis = FIS.load(filename, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock(null);

		// Set inputs
		fb.setVariable("food", 8.5);
		fb.setVariable("service", 7.5);

		// Evaluate
		fb.evaluate();
		

		// Show output variable's chart
		fb.getVariable("tip").defuzzify();

		// Print ruleSet
		//System.out.println(fb);
		System.out.println("Tip: " + fb.getVariable("tip").getValue());
		
		//--------------------------------------------------------------------------------------------
		RuleBlock objRb = fb.getFuzzyRuleBlock("No1");
		Rule objRule = new Rule("4", objRb);
		
		RuleExpression objAnt = new RuleExpression();
		objAnt.add(new RuleTerm(fb.getVariable("service"), "good", false));
		objAnt.add(new RuleTerm(fb.getVariable("food"), "delicious", false));

		RuleConnectionMethod objRuleConnMethod = objAnt.getRuleConnectionMethod();
		objRuleConnMethod.connect(fb.getVariable("food").getValue(), fb.getVariable("service").getValue());
		objRuleConnMethod.setName("or");
		
		objRule.setAntecedents(objAnt);
		objRule.addConsequent(fb.getVariable("tip"), "generous", false);
		
		
		objRb.add(objRule);
		//--------------------------------------------------------------------------------------------
		
		
		System.out.println("NewRuleBlock: " + objRb);
		
		fb.evaluate();
		

		// Show output variable's chart
		fb.getVariable("tip").defuzzify();
		System.out.println("NewTip: " + fb.getVariable("tip").getValue());
		
		/*System.out.println("________");
		System.out.println("BRule: "+objRb.getRules().get(0) );
		System.out.println("Rule:Ant1 "+objRb.getRules().get(0).getAntecedents().getTerm1());
		System.out.println("Rule:Ant2 "+objRb.getRules().get(0).getAntecedents().getTerm2());
		objRb.getRules().get(0).getAntecedents().getRuleConnectionMethod().setName("and");
		System.out.println("Rule:Ant3 "+objRb.getRules().get(0).getAntecedents().getRuleConnectionMethod().getName());
		System.out.println("ARule: "+objRb.getRules().get(0) );
		
		System.out.println("Rule:Con "+objRb.getRules().get(0).getConsequents() );
		System.out.println("Rule:Deg "+objRb.getRules().get(0).getDegreeOfSupport() );
		System.out.println("Rule:Wt "+objRb.getRules().get(0).getWeight());*/
		
		//objRb.getRules().add(objRule);
		
		
		
	}
}
