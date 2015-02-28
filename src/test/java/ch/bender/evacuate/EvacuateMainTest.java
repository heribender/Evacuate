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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mockit.Deencapsulation;
import mockit.Injectable;
import mockit.Tested;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the correct initializing from command line
 *
 * @author Heri
 */
public class EvacuateMainTest
{
    
    /** logger for this class */
    private Logger myLog = LogManager.getLogger( EvacuateMainTest.class );
    
    @Tested
    EvacuateMain                      myClassUnderTest;
    @Injectable
    String[] myArgs = new String[0];

    
    /**
     * Enumeration on some file system objects. Member is initialized in setup()
     */
    enum FSOBJECTS
    {
        /** Existing directory */
        DIR1,
        /** Existing directory */
        DIR2,
        /** Existing directory */
        DIR3,
        /** Existing file */
        FILE1,
        /** Existing file */
        FILE2,
        /** Existing file */
        FILE3,
        /** Not existing object */
        NOT1,
        /** Not existing object */
        NOT2,
        /** Not existing object */
        NOT3;
        
        Path fsObject;

        /**
         * @return Returns the fsObject.
         */
        public Path getFsObject()
        {
            return fsObject;
        }

        /**
         * @param aFsObject The fsObject to set.
         */
        public void setFsObject( Path aFsObject )
        {
            fsObject = aFsObject;
        }
    }
    
    
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
            FSOBJECTS.DIR1.setFsObject( Testconstants.createNewFolder( Testconstants.ROOT_DIR, "existingDir1" ) );
            FSOBJECTS.DIR2.setFsObject( Testconstants.createNewFolder( Testconstants.ROOT_DIR, "existingDir2" ) );
            FSOBJECTS.DIR3.setFsObject( Testconstants.createNewFolder( Testconstants.ROOT_DIR, "existingDir3" ) );
            FSOBJECTS.FILE1.setFsObject( Testconstants.createNewFile( Testconstants.ROOT_DIR, "existingFile1.txt" ) );
            FSOBJECTS.FILE2.setFsObject( Testconstants.createNewFile( Testconstants.ROOT_DIR, "existingFile2.txt" ) );
            FSOBJECTS.FILE3.setFsObject( Testconstants.createNewFile( Testconstants.ROOT_DIR, "existingFile3.txt" ) );
            FSOBJECTS.NOT1.setFsObject( Paths.get( Testconstants.ROOT_DIR.toString(), "notExisting1" ) );
            FSOBJECTS.NOT2.setFsObject( Paths.get( Testconstants.ROOT_DIR.toString(), "notExisting2" ) );
            FSOBJECTS.NOT3.setFsObject( Paths.get( Testconstants.ROOT_DIR.toString(), "notExisting3" ) );
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
     * Test method for {@link ch.bender.evacuate.EvacuateMain#init()}.
     */
    @Test( expected=IllegalArgumentException.class )
    public void testInit0()
    {
        myLog.debug( "No command line arguments at all" );
        String[] args = makeCmdLineArgs();
        Deencapsulation.setField( myClassUnderTest, args );
        myClassUnderTest.init();
    }

    /**
     * Test method for {@link ch.bender.evacuate.EvacuateMain#init()}.
     */
    @Test( expected=IllegalArgumentException.class )
    public void testInit1()
    {
        myLog.debug( "only one command line arguments" );
        String[] args = makeCmdLineArgs( "bla" );
        Deencapsulation.setField( myClassUnderTest, args );
        myClassUnderTest.init();
    }

    /**
     * Test method for {@link ch.bender.evacuate.EvacuateMain#init()}.
     */
    @Test( expected=IllegalArgumentException.class )
    public void testInit2()
    {
        myLog.debug( "only two command line arguments" );
        String[] args = makeCmdLineArgs( "bla", "bli" );
        Deencapsulation.setField( myClassUnderTest, args );
        myClassUnderTest.init();
    }

    /**
     * Test method for {@link ch.bender.evacuate.EvacuateMain#init()}.
     */
    @Test( expected=IllegalArgumentException.class )
    public void testInit3d()
    {
        myLog.debug( "one option and then only two command line arguments" );
        String[] args = makeCmdLineArgs( "-d", "bli", "blu" );
        Deencapsulation.setField( myClassUnderTest, args );
        myClassUnderTest.init();
    }

    boolean myRunnerCalled;

    
    class RunnerMock extends Runner
    {
        /**
         * @see ch.bender.evacuate.Runner#run()
         */
        @Override
        public void run() throws Exception
        {
            myRunnerCalled = true;
        }
        
    }
    
    /**
     * Loops on all posibilities
     * <p>
     * Test method for {@link ch.bender.evacuate.EvacuateMain#init()}.
     * @throws Exception 
     */
    @Test
    public void testInitLoop() throws Exception 
    {
        String[] zeroOptions = makeCmdLineArgs();
        String[] oneOptionsD = makeCmdLineArgs( "-d" );
        String[] oneOptionsM = makeCmdLineArgs( "-m" );
        String[] twoOptions = makeCmdLineArgs( "--move", "--dry-run" );
        
        List<String[]> optionSets = new ArrayList<>();
        optionSets.add( zeroOptions );
        optionSets.add( oneOptionsD );
        optionSets.add( oneOptionsM );
        optionSets.add( twoOptions );

//        doLoopStep( twoOptions, 
//                    FSOBJECTS.DIR1, 
//                    FSOBJECTS.DIR2,
//                    FSOBJECTS.DIR3 );
        
        for ( String[] options : optionSets )
        {
            for ( FSOBJECTS fsObjOrig : FSOBJECTS.values() )
            {
                for ( FSOBJECTS fsObjBackup : FSOBJECTS.values() )
                {
                    for ( FSOBJECTS fsObjTrash : FSOBJECTS.values() )
                    {
                        doLoopStep( options, 
                                    fsObjOrig, 
                                    fsObjBackup,
                                    fsObjTrash );
                    }
                }
            }
        }
        
    }

    private void doLoopStep( String[] aOptions,
                             FSOBJECTS aOrig,
                             FSOBJECTS aBackup,
                             FSOBJECTS aEvacuate ) throws Exception
    {
        String[] args = new String[ aOptions.length + 3 ];
        
        boolean expectedDryRun = false;
        boolean expectedMove = false;
        boolean expectedSuccess = false;
        
        // prepare options in command line:
        for ( int i = 0; i < aOptions.length; i++ )
        {
            if ( !expectedDryRun )
            {
                expectedDryRun = ( "-d".equals( aOptions[i] ) || "--dry-run".equals( aOptions[i] ) );
            }
            
            if ( !expectedMove )
            {
                expectedMove   = ( "-m".equals( aOptions[i] ) || "--move".equals( aOptions[i] ) );
            }
            
            args[i] = aOptions[i];
        }
        
        // prepare file system objects in command line:
        int index = aOptions.length;
        args[index] = aOrig.getFsObject().toString();
        index++;
        args[index] = aBackup.getFsObject().toString();
        index++;
        args[index] = aEvacuate.getFsObject().toString();
        index++;
        
        // evaluate if success is expected or not:
        switch ( aOrig )
        {
            case DIR1:
            case DIR2:
            case DIR3:
                // orig must be existing directory
                switch ( aBackup )
                {
                    case DIR1:
                    case DIR2:
                    case DIR3:
                        // backup must be existing directory
                        switch ( aEvacuate )
                        {
                            case NOT1:
                            case NOT2:
                            case NOT3:
                            case DIR1:
                            case DIR2:
                            case DIR3:
                                // trash must NOT be existing file
                                expectedSuccess = true;
                                //special: all three must be different:
                                if (    ( aOrig == aBackup     ) 
                                     || ( aOrig == aEvacuate   )
                                     || ( aBackup == aEvacuate ) )
                                {
                                    expectedSuccess = false;
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        
        Deencapsulation.setField( myClassUnderTest, args );
        
        try
        {
            myLog.debug( "Calling productive code with args: " + Arrays.asList( args ) );
            myClassUnderTest.init();
            
            if ( !expectedSuccess )
            {
                Assert.fail( "Exception expected" );
            }
            
            Runner myRunner;
            myRunner = new RunnerMock();
            Deencapsulation.setField( myClassUnderTest, "myRunner", myRunner );
            myRunnerCalled = false;

            myClassUnderTest.run();
            
            Assert.assertTrue( myRunnerCalled );
            Assert.assertEquals( "Move",     expectedMove,                       myRunner.isMove() );
            Assert.assertEquals( "DryRun",   expectedDryRun,                     myRunner.isDryRun() );
            Assert.assertEquals( "Orig",     aOrig.getFsObject().toString(),     myRunner.getOrigDir() );
            Assert.assertEquals( "Backup",   aBackup.getFsObject().toString(),   myRunner.getBackupDir() );
            Assert.assertEquals( "Evacuate", aEvacuate.getFsObject().toString(), myRunner.getEvacuateDir() );
        }
        catch ( IllegalArgumentException e )
        {
            if ( expectedSuccess )
            {
                Assert.fail( "No Exception expected!!" );
            }
            
            myLog.debug( "Expected exception received: " + e.getMessage() );
        }
        finally
        {
            // tidy up
            switch ( aEvacuate )
            {
                case NOT1:
                case NOT2:
                case NOT3:
                    try
                    {
                        // productive code has probably created it. Delete it again for next test step
                        if ( aEvacuate.getFsObject().toFile().exists() )
                        {
                            Helper.deleteDirRecursive( aEvacuate.getFsObject() );
                        }
                    }
                    catch ( Exception e )
                    {
                        Assert.fail( e.getMessage() );
                    }
                    break;
                default:
                    break;
            }
            
            Deencapsulation.setField( myClassUnderTest, "myMove", false );
            Deencapsulation.setField( myClassUnderTest, "myDryRun", false );
        }
        
    }
    
    private String[] makeCmdLineArgs( String...aStrings )
    {
        return aStrings;
    }
    
}
