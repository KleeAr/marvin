package ar.com.klee.expressions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionMatcher {

	List<String> variableNames = new ArrayList<String>();
	private final Pattern expressionPattern;
	
	public ExpressionMatcher(String template) {
		
		Pattern pattern = Pattern.compile("\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(template);
		while(matcher.find()) {
			variableNames.add(matcher.group(1));
		}
		this.expressionPattern = Pattern.compile(matcher.replaceAll("(.*?)"));
		
	}

	public Map<String, String> getValuesFromExpression(String expression) {
		Matcher matcher = this.expressionPattern.matcher(expression);
		Map<String, String> variables = new HashMap<String, String>();
		if(!matcher.matches()) {
			return Collections.emptyMap();
		} else {
			for(int i = 0; i < matcher.groupCount(); i++) {
				variables.put(variableNames.get(i), matcher.group(i + 1));
			}
		}
		return variables;
	}

	public boolean matches(String expression) {
		Matcher matcher = this.expressionPattern.matcher(expression);
		return matcher.matches();
	}

}
