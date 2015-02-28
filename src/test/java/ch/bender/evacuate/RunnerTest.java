/**
 * Copyright (c) 2015 by the original author or authors.
 *
 * This code is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */



package ch.bender.evacuate;

import java.nio.file.Files;
import java.nio.file.Path;

import mockit.Injectable;
import mockit.Tested;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO Hey Heri, comment this type please !
 *
 * @author Heri
 */
public class RunnerTest
{
    
    /** logger for this class */
    private Logger myLog = LogManager.getLogger( RunnerTest.class );

    @Tested
    Runner myClassUnderTest;
    
    @Injectable
    private boolean myDryRun;
    @Injectable
    private boolean myMove;
    @Injectable
    private String myOrigDirStr;
    @Injectable
    private String myBackupDirStr;
    @Injectable
    private String myEvacuateDirStr;
    

    /**
     * This method is executed just before each test method
     * 
     * @throws Throwable
     *         On any problem (test method will not be executed)
     */
    @Before
    public void setUp() throws Throwable
    {
        myLog.debug( "entering" );
        try
        {
            if ( Files.exists( Testconstants.ROOT_DIR ) )
            {
                Helper.deleteDirRecursive( Testconstants.ROOT_DIR );
            }
            
            Files.createDirectory( Testconstants.ROOT_DIR );
            
            Path orig = Testconstants.createNewFolder( Testconstants.ROOT_DIR, "orig" );
            Path origSub1 = Testconstants.createNewFolder( orig, "sub1" );
            Path origSub2 = Testconstants.createNewFolder( orig, "sub2" );
            
            Path file1 = Testconstants.createNewFile( orig, "file1.txt" );
            Path fileSub1 = Testconstants.createNewFile( origSub1, "fileSub1.txt" );
            Path fileSub2 = Testconstants.createNewFile( origSub2, "fileSub2.txt" );

            Path backup = Testconstants.createNewFolder( Testconstants.ROOT_DIR, "backup" );
            FileUtils.copyDirectoryToDirectory( origSub1.toFile(), backup.toFile() );
            FileUtils.copyDirectoryToDirectory( origSub2.toFile(), backup.toFile() );
            
            Path evacuate = Testconstants.createNewFolder( Testconstants.ROOT_DIR, "evacuate" );
            
            myDryRun = false;
            myMove = false;
            myOrigDirStr = orig.toString();
            myBackupDirStr = backup.toString();
            myEvacuateDirStr = evacuate.toString();
            
            
        }
        catch ( Throwable t )
        {
            myLog.error( "Throwable caught in setup", t );
            throw t;
        }
    }

    /**
     * This method is executed just after each test method
     * 
     * @throws Throwable
     *         On any problem
     */
    @After
    public final void tearDown() throws Throwable
    {
        myLog.debug( "entering..." );

        try
        {
            if ( Files.exists( Testconstants.ROOT_DIR ) )
            {
                Helper.deleteDirRecursive( Testconstants.ROOT_DIR );
            }
            

            myLog.debug( "leaving..." );
        }
        catch ( Throwable t )
        {
            myLog.error( "Throwable caught in tearDown()", t );
            throw t;
        }
    }

    

    /**
     * testDryRun
     * <p>
     * @throws Exception
     */
    @Test
    public void testDryRun() throws Exception
    {
        myClassUnderTest.setDryRun( true );
//        myClassUnderTest.setMove( false );
//        myClassUnderTest.setOrigDir( orig.toString() );
//        myClassUnderTest.setBackupDir( backup.toString() );
//        myClassUnderTest.setEvacuateDir( evacuate.toString() );
        
        myClassUnderTest.run();
        
    }

}
