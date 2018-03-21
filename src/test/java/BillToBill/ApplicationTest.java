package BillToBill;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ApplicationTest extends TestCase
{

    public ApplicationTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( ApplicationTest.class );
    }

    public void testApp()
    {
        assertTrue( true );
    }
}
