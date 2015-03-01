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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Testcases for the Helper class
 *
 * @author Heri
 */
public class HelperTest
{
    
    /** logger for this class */
    private Logger myLog = LogManager.getLogger( HelperTest.class );
    private Path myBaseDir;
    private Path myBaseFile;
    private Path myDir_01;
    private Path myDir_02;
    private Path myFile01;
    private Path myFile02;
    private Map<Path,Throwable> myFailedChainPreparations;

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
            
            myBaseDir = Testconstants.createNewFolder( Testconstants.ROOT_DIR, "pre.orig" );
            myDir_01 = Testconstants.createNewFolder( Testconstants.ROOT_DIR, "pre.orig_01" );
            myDir_02 = Testconstants.createNewFolder( Testconstants.ROOT_DIR, "pre.orig_02" );
            
            myBaseFile = Testconstants.createNewFile( myBaseDir, "pre.file.txt" );
            myFile01 = Testconstants.createNewFile( myBaseDir, "pre.file_01.txt" );
            myFile02 = Testconstants.createNewFile( myBaseDir, "pre.file_02.txt" );

            myFailedChainPreparations = new HashMap<>();
            
            
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
     * Test method for {@link ch.bender.evacuate.Helper#prepareTrashChain(java.nio.file.Path, int)}.
     * @throws IOException 
     */
    @Test
    public void testPrepareTrashChainDirectory10()
    {
        myLog.debug( "trash chaning directory (10): " + myBaseDir.toString() );
        Helper.prepareTrashChain( myBaseDir, 10, myFailedChainPreparations );
        
        if ( myFailedChainPreparations.size() > 0 )
        {
            for ( Path path : myFailedChainPreparations.keySet() )
            {
                myLog.error( "exception occured", myFailedChainPreparations.get( path ) );
            }
            
            Assert.fail();
        }

        Assert.assertTrue( "baseDir file not move to baseDir_01", Files.notExists( myBaseDir ) );
        Assert.assertTrue( Files.exists( myDir_01 ) );
        Assert.assertTrue( Files.exists( Paths.get( myDir_01.toString(), myBaseFile.getFileName().toString() ) ) );
        Assert.assertTrue( Files.exists( myDir_02 ) );
        Path dir3 = Helper.appendNumberSuffix( myBaseDir, 3 );
        Assert.assertTrue( dir3.toString() + " does not exist!", Files.exists( dir3 ) );
    }

    /**
     * Test method for {@link ch.bender.evacuate.Helper#prepareTrashChain(java.nio.file.Path, int)}.
     * @throws IOException 
     */
    @Test
    public void testPrepareTrashChainFiles10()
    {
        myLog.debug( "trash chaning files (10): " + myBaseDir.toString() );
        Helper.prepareTrashChain( myBaseFile, 10, myFailedChainPreparations );
        
        if ( myFailedChainPreparations.size() > 0 )
        {
            for ( Path path : myFailedChainPreparations.keySet() )
            {
                myLog.error( "exception occured", myFailedChainPreparations.get( path ) );
            }
            
            Assert.fail();
        }

        Assert.assertTrue( "base file not moved to basefile_01", Files.notExists( myBaseFile ) );
        Assert.assertTrue( Files.exists( myFile01 ) );
        Assert.assertTrue( Files.exists( myFile02 ) );
        Path file3 = Helper.appendNumberSuffix( myBaseFile, 3, true );
        Assert.assertTrue( file3.toString() + " does not exist!", Files.exists( file3 ) );
    }

    /**
     * Test method for {@link ch.bender.evacuate.Helper#prepareTrashChain(java.nio.file.Path, int)}.
     * @throws IOException 
     */
    @Test
    public void testPrepareTrashChainDirectory3()
    {
        myLog.debug( "trash chaning directory (3): " + myBaseDir.toString() );
        Helper.prepareTrashChain( myBaseDir, 3, myFailedChainPreparations );
        
        if ( myFailedChainPreparations.size() > 0 )
        {
            for ( Path path : myFailedChainPreparations.keySet() )
            {
                myLog.error( "exception occured", myFailedChainPreparations.get( path ) );
            }
            
            Assert.fail();
        }

        Assert.assertTrue( "baseDir file not move to baseDir_01", Files.notExists( myBaseDir ) );
        Assert.assertTrue( Files.exists( myDir_01 ) );
        Assert.assertTrue( Files.exists( Paths.get( myDir_01.toString(), myBaseFile.getFileName().toString() ) ) );
        Assert.assertTrue( Files.exists( myDir_02 ) );
        Path dir3 = Helper.appendNumberSuffix( myBaseDir, 3 );
        Assert.assertTrue( dir3.toString() + " does exist!", Files.notExists( dir3 ) );
    }

    /**
     * Test method for {@link ch.bender.evacuate.Helper#prepareTrashChain(java.nio.file.Path, int)}.
     * @throws IOException 
     */
    @Test
    public void testPrepareTrashChainFiles3()
    {
        myLog.debug( "trash chaning files (3): " + myBaseDir.toString() );
        Helper.prepareTrashChain( myBaseFile, 3, myFailedChainPreparations );
        
        if ( myFailedChainPreparations.size() > 0 )
        {
            for ( Path path : myFailedChainPreparations.keySet() )
            {
                myLog.error( "exception occured", myFailedChainPreparations.get( path ) );
            }
            
            Assert.fail();
        }

        Assert.assertTrue( "base file not movee to basefile_01", Files.notExists( myBaseFile ) );
        Assert.assertTrue( Files.exists( myFile01 ) );
        Assert.assertTrue( Files.exists( myFile02 ) );
        Path file3 = Helper.appendNumberSuffix( myBaseFile, 3, true );
        Assert.assertTrue( file3.toString() + " does exist!", Files.notExists( file3 ) );
    }

    /**
     * Test method for {@link ch.bender.evacuate.Helper#appendNumberSuffix(java.nio.file.Path, int)}.
     */
    @Test
    public void testAppendNumberSuffixDir()
    {
        myLog.debug( "numbering directory: " + myBaseDir.toString() );
        Path received = Helper.appendNumberSuffix( myBaseDir, 1 );
        myLog.debug( "received: " + received.toString() );
        Assert.assertEquals( myBaseDir.toString() + "_01", received.toString() );
    }

    /**
     * Test method for {@link ch.bender.evacuate.Helper#appendNumberSuffix(java.nio.file.Path, int)}.
     */
    @Test
    public void testAppendNumberSuffixFile()
    {
        myLog.debug( "numbering file: " + myBaseFile.toString() );
        Path received = Helper.appendNumberSuffix( myBaseFile, 1 );
        myLog.debug( "received: " + received.toString() );
        Assert.assertEquals( Paths.get( myBaseFile.getParent().toString(), "pre.file_01.txt" ).toString(), 
                             received.toString() );
    }

    /**
     * Test method for {@link ch.bender.evacuate.Helper#appendNumberSuffix(java.nio.file.Path, int)}.
     */
    @Test
    public void testAppendNumberSuffixFileNoExtension()
    {
        Path file = Paths.get( "filename" );
        myLog.debug( "numbering file: " + file );
        Path received = Helper.appendNumberSuffix( file, 1, true );
        myLog.debug( "received: " + received.toString() );
        Assert.assertEquals( "filename_01", received.toString() );
    }

    /**
     * Tests 
     * <p>
     * 
     * @throws Throwable on any problem
     */
    @Test
    public void test() throws Throwable
    {

//        myLog.debug( ">>>>>>>>>>>>>>>> starting" );
//
//        try
//        {
//            Path path = Paths.get( "T:/" );
//            Helper.deleteDirRecursive( path );
//        }
//        finally
//        {
//            myLog.debug( ">>>>>>>>>>>>>>>> finished" );
//        }
    }

    


}
