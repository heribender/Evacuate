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
import java.nio.file.Paths;
import java.util.Arrays;

import mockit.Injectable;
import mockit.Tested;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
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

    private Path myOrigFile1;

    private Path myOrigFileSub1;
    private Path myOrigFileSub12;

    private Path myOrigFileSub2;
    private Path myOrigFileSub2Sub1;

    private Path myOrigDir;

    private Path myOrigDirSub1;

    private Path myOrigDirSub2;
    private Path myOrigDirSub2Sub1;

    private Path myBackupDir;

    private Path myEvacuateDir;
    

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
            
            /*
             * Preparing the test sandbox in current directory:
             * 
             * testsandbox
             *   +- orig
             *      +- sub1
             *         +- fileSub12.txt
             *   +- backup
             *      +- sub1
             *         +- fileSub1.txt
             *         +- fileSub12.txt
             *      +- sub2
             *         +- sub2sub1
             *            +- fileSub2Sub1.txt
             *         +- fileSub2.txt
             *      +- file1.txt
             *   +- evacuate
             *       <empty>
             */
            Files.createDirectory( Testconstants.ROOT_DIR );
            
            myOrigDir = Testconstants.createNewFolder( Testconstants.ROOT_DIR, "orig" );
            myOrigDirSub1 = Testconstants.createNewFolder( myOrigDir, "sub1" );
            myOrigDirSub2 = Testconstants.createNewFolder( myOrigDir, "sub2" );
            myOrigDirSub2Sub1 = Testconstants.createNewFolder( myOrigDirSub2, "sub2sub1" );
            
            myOrigFile1 = Testconstants.createNewFile( myOrigDir, "file1.txt" );
            myOrigFileSub1 = Testconstants.createNewFile( myOrigDirSub1, "fileSub1.txt" );
            myOrigFileSub12 = Testconstants.createNewFile( myOrigDirSub1, "fileSub12.txt" );
            myOrigFileSub2 = Testconstants.createNewFile( myOrigDirSub2, "fileSub2.txt" );
            myOrigFileSub2Sub1 = Testconstants.createNewFile( myOrigDirSub2Sub1, "fileSub2Sub1.txt" );
            
            myBackupDir = Testconstants.createNewFolder( Testconstants.ROOT_DIR, "backup" );
            FileUtils.copyFileToDirectory( myOrigFile1.toFile(), myBackupDir.toFile() );
            FileUtils.copyDirectoryToDirectory( myOrigDirSub1.toFile(), myBackupDir.toFile() );
            FileUtils.copyDirectoryToDirectory( myOrigDirSub2.toFile(), myBackupDir.toFile() );
            
            myEvacuateDir = Testconstants.createNewFolder( Testconstants.ROOT_DIR, "evacuate" );

            // delete some objects from orig dir:
            Helper.deleteDirRecursive( myOrigDirSub2 );
            Files.delete( myOrigFile1 );
            Files.delete( myOrigFileSub1 );
            
            myDryRun = false;
            myMove = false;
            myOrigDirStr = myOrigDir.toString();
            myBackupDirStr = myBackupDir.toString();
            myEvacuateDirStr = myEvacuateDir.toString();
            
            
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
        myClassUnderTest.run();
        Assert.assertEquals( 0, myEvacuateDir.toFile().listFiles().length );
    }

    /**
     * testDryRun
     * <p>
     * @throws Exception
     */
    @Test
    public void testMove() throws Exception
    {
        myClassUnderTest.setMove( true );
        myClassUnderTest.run();
        Assert.assertTrue( "Nothing moved!!!", myEvacuateDir.toFile().listFiles().length > 0);
        
        Assert.assertTrue( "Src still exists", Files.notExists( Paths.get( "testsandbox", "backup", "sub1", "fileSub1.txt" ) ) );
        Assert.assertTrue( "Dst not exists", Files.exists( Paths.get( "testsandbox", "evacuate", "sub1", "fileSub1.txt" ) ) );
        Assert.assertTrue( "Src still exists", Files.notExists( Paths.get( "testsandbox", "backup", "file1.txt" ) ) );
        Assert.assertTrue( "Dst not exists", Files.exists( Paths.get( "testsandbox", "evacuate", "file1.txt" ) ) );
        Assert.assertTrue( "Src still exists", Files.notExists( Paths.get( "testsandbox", "backup", "sub2" ) ) );
        Assert.assertTrue( "Dst not exists", Files.exists( Paths.get( "testsandbox", "evacuate", "sub2" ) ) );
        // one check on a implicitely moved file:
        Assert.assertTrue( "Implicitely moved file not exists", Files.exists( Paths.get( "testsandbox", 
                                                                                         "evacuate", 
                                                                                         "sub2", 
                                                                                         "sub2sub1", 
                                                                                         "fileSub2Sub1.txt" ) ) );
        
    }

    /**
     * testDryRun
     * <p>
     * @throws Exception
     */
    @Test
    public void testExclude() throws Exception
    {
        myClassUnderTest.setMove( true );
        myClassUnderTest.setExcludePatterns( Arrays.asList( new String[] { "**/FileSub?.t*", "**/sub2" } ) );
        myClassUnderTest.run();
        Assert.assertTrue( "Nothing moved!!!", myEvacuateDir.toFile().listFiles().length > 0);
        
        Assert.assertTrue( "Not excluded", Files.exists( Paths.get( "testsandbox", "backup", "sub1", "fileSub1.txt" ) ) );
        Assert.assertTrue( "Not excluded", Files.notExists( Paths.get( "testsandbox", "evacuate", "sub1", "fileSub1.txt" ) ) );
        Assert.assertTrue( "Src still exists", Files.notExists( Paths.get( "testsandbox", "backup", "file1.txt" ) ) );
        Assert.assertTrue( "Dst not exists", Files.exists( Paths.get( "testsandbox", "evacuate", "file1.txt" ) ) );
        Assert.assertTrue( "Not excluded", Files.exists( Paths.get( "testsandbox", "backup", "sub2" ) ) );
        Assert.assertTrue( "Not excluded", Files.notExists( Paths.get( "testsandbox", "evacuate", "sub2" ) ) );
        
    }

//    /**
//     * testDryRun
//     * <p>
//     * @throws Exception
//     */
//    @Test
//    public void testMoveOtherPartition() throws Exception
//    {
//        myClassUnderTest.setMove( true );
//        myEvacuateDir = Paths.get( "T:/" );
//        Deencapsulation.setField( myClassUnderTest, "myEvacuateDirStr", myEvacuateDir.toString() );
//        
//        try
//        {
//            myClassUnderTest.run();
//            Assert.assertTrue( "Nothing moved!!!", myEvacuateDir.toFile().listFiles().length > 0);
//            
//            Assert.assertTrue( "Src still exists", Files.notExists( Paths.get( "testsandbox", "backup", "sub1", "fileSub1.txt" ) ) );
//            Assert.assertTrue( "Dst not exists", Files.exists( Paths.get( "T:", "sub1", "fileSub1.txt" ) ) );
//            Assert.assertTrue( "Src still exists", Files.notExists( Paths.get( "testsandbox", "backup", "file1.txt" ) ) );
//            Assert.assertTrue( "Dst not exists", Files.exists( Paths.get( "T:", "file1.txt" ) ) );
//            Assert.assertTrue( "Src still exists", Files.notExists( Paths.get( "testsandbox", "backup", "sub2" ) ) );
//            Assert.assertTrue( "Dst not exists", Files.exists( Paths.get( "T:", "sub2" ) ) );
//            // one check on a implicitely moved file:
//            Assert.assertTrue( "Implicitely moved file not exists", Files.exists( Paths.get( "T:", 
//                                                                                             "sub2", 
//                                                                                             "sub2sub1", 
//                                                                                             "fileSub2Sub1.txt" ) ) );
//        }
//        finally
//        {
//            Helper.deleteDirRecursive( myEvacuateDir );
//        }
//        
//    }

    /**
     * testDryRun
     * <p>
     * @throws Exception
     */
    @Test
    public void testCopy1() throws Exception
    {
        myClassUnderTest.run();
        Assert.assertTrue( "Nothing copied!!!", myEvacuateDir.toFile().listFiles().length > 0);
        
        Assert.assertTrue( "Src deleted", Files.exists( Paths.get( "testsandbox", "backup", "sub1", "fileSub1.txt" ) ) );
        Assert.assertTrue( "Dst not exists", Files.exists( Paths.get( "testsandbox", "evacuate", "sub1", "fileSub1.txt" ) ) );
        Assert.assertTrue( "Src deleted", Files.exists( Paths.get( "testsandbox", "backup", "file1.txt" ) ) );
        Assert.assertTrue( "Dst not exists", Files.exists( Paths.get( "testsandbox", "evacuate", "file1.txt" ) ) );
        Assert.assertTrue( "Src deleted", Files.exists( Paths.get( "testsandbox", "backup", "sub2" ) ) );
        Assert.assertTrue( "Dst not exists", Files.exists( Paths.get( "testsandbox", "evacuate", "sub2" ) ) );
        // one check on a implicitely moved file:
        Assert.assertTrue( "Implicitely moved file not exists", Files.exists( Paths.get( "testsandbox", 
                                                                                         "evacuate", 
                                                                                         "sub2", 
                                                                                         "sub2sub1", 
                                                                                         "fileSub2Sub1.txt" ) ) );
    }
    
    /**
     * testing the backup chain
     * <p>
     * @throws Exception
     */
    @Test
    public void testCopy12() throws Exception
    {

        myLog.debug( ">>>>>>>>>>>>>>>> starting" );

        try
        {
            myClassUnderTest.run();
            myClassUnderTest.run();
            myClassUnderTest.run();
            myClassUnderTest.run();
            myClassUnderTest.run();
            myClassUnderTest.run();
            myClassUnderTest.run();
            myClassUnderTest.run();
            myClassUnderTest.run();
            myClassUnderTest.run();
            myClassUnderTest.run();
            myClassUnderTest.run();
            
            assertBackupChain( myOrigFile1, 10 );
            assertBackupChain( myOrigDirSub2, 10 );
            
            myLog.debug( ">>>>>>>>>>>>>>>> finished successfully" );
        }
        catch ( AssertionError e )
        {
            myLog.warn( "<<<<<<<<<<<<<<<< ERROR occured", e );
            // rethrow since it is an already thrown assertion
            throw e;
        }
        catch ( Throwable e )
        {
            myLog.warn( "<<<<<<<<<<<<<<<< ERROR occured", e );
            Assert.fail( "Throwable caught: "
                         + e );
        }
    }

    /**
     * assertBackupChain
     * <p>
     * @param aOrigFile1
     * @param aI
     */
    private void assertBackupChain( Path aPath, int aTimes )
    {
        Path subDirToBackupRoot = myOrigDir.relativize( aPath );
        Path dst = myEvacuateDir.resolve( subDirToBackupRoot );
        Assert.assertTrue( "Dst '" + dst.toString() + "' not exists", Files.exists( dst ) );
        
        for ( int i = 1; i < aTimes; i++ )
        {
            Path chained = Helper.appendNumberSuffix( dst, i ); 
            Assert.assertTrue( "chained '" + chained.toString() + "' not exists", Files.exists( chained ) );
        }
        
        Path chained = Helper.appendNumberSuffix( dst, aTimes ); 
        Assert.assertTrue( "chained '" + chained.toString() + "' should not exist", Files.notExists( chained ) );
        
    }

}
