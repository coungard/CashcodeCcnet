package BillToBill;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LauncherTest extends TestCase
{

    public LauncherTest(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( LauncherTest.class );
    }

    public void testApp()
    {
        assertTrue( true );
    }
}
