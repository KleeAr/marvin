package ar.com.klee.marvinSimulator.expressions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.com.klee.marvinSimulator.expressions.exceptions.ExpressionMatcherException;


/**
 * 
 * 
 * 
 * This class is intended to help with the recognition of commands and 
 * to help obtaining the values stored within the command
 * 
 * @author msalerno
 *
 */
public class ExpressionMatcher {

	List<String> variableNames = new ArrayList<String>();
	private final Pattern expressionPattern;
	private final String template;
	
	/**
	 * 
	 * @param template the template used to match variables
	 */
	public ExpressionMatcher(String template) {
		
		Pattern pattern = Pattern.compile("\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(template);
		while(matcher.find()) {
			variableNames.add(matcher.group(1));
		}
		this.expressionPattern = Pattern.compile(matcher.replaceAll("(.*?)"));
		this.template = template;
		
	}

	/**
	 * 
	 * @param expression The source expression
	 * @return a Map<String, String> were the key is the variable name and the value is the variable's value
	 * @throws ExpressionMatcherException if the expression doesn't match with the template
	 * 
	 */
	public Map<String, String> getValuesFromExpression(String expression) {
		Matcher matcher = this.expressionPattern.matcher(expression);
		Map<String, String> variables = new HashMap<String, String>();
		if(!matcher.matches()) {
			throw new ExpressionMatcherException(MessageFormat
					.format("The expression '{0}' didn't match with the template '{1}'",
							expression, template));
		} else {
			for(int i = 0; i < matcher.groupCount(); i++) {
				variables.put(variableNames.get(i), matcher.group(i + 1));
			}
		}
		return variables;
	}

	/**
	 * 
	 * @param expression the expression to compare with the template
	 * @return <code>true</code> if the expression matches the template
	 */
	public boolean matches(String expression) {
		Matcher matcher = this.expressionPattern.matcher(expression);
		return matcher.matches();
	}

    /**
     *
     * @param expression the expression to compare with the template
     * @return <code>true</code> if the expression is similar to the template
     */
    public boolean isSimilar(String expression) {
        return expression.startsWith(template.split(" ")[0]);
    }

    /**
     * @return a suggestion string that matches this template
     */
    public String getSuggestion() {
        return template.replaceAll("(\\{|\\})", "");
    }
}
