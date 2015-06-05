package ar.com.klee.expressions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import ar.com.klee.expressions.exceptions.ExpressionMatcherException;

/**
 * 
 * Test class for the class ExpressionMatcherTest
 * 
 * @author msalerno
 *
 */
public class ExpressionMatcherTest {

	private static final String TEMPLATE = "my name is {username} and i am {userage} years old";
	private static final String MATIAS = "my name is Matias and i am 25 years old";
	private static final String FEDE = "my name is Federico and i am 23 years old";
	
	// Class under test
    private ExpressionMatcher expressionMatcher = new ExpressionMatcher(TEMPLATE);

    @Test
    public void testGetValuesFromExpression() {

        
        Map<String, String> variables = expressionMatcher.getValuesFromExpression(MATIAS);
        
        assertEquals("Matias", variables.get("username"));
        assertEquals("25", variables.get("userage"));
        
        variables = expressionMatcher.getValuesFromExpression(FEDE);
        
        assertEquals("Federico", variables.get("username"));
        assertEquals("23", variables.get("userage"));
     }
    
    @Test(expected = ExpressionMatcherException.class)
    public void testGetValuesFromExpressionWhenDoesntMatch() {
    	// Will throw exception because it doesn't match
    	expressionMatcher.getValuesFromExpression("My name is Barry Allen, and I'm the fastest man alive");
     }
    
    @Test
    public void testMatches() {
        assertTrue(expressionMatcher.matches(MATIAS));
        assertFalse("Must not match, since expression says 'was' instead of 'is'", expressionMatcher.matches("My name was Matias and i am 25 years old"));
    }
	
}
