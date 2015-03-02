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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TODO Hey Heri, comment this type please !
 *
 * @author Heri
 */
public class EvacuateMain
{
    
    /** logger for this class */
    private static Logger myLog = LogManager.getLogger( EvacuateMain.class );
    
    private String[] myArgs;
    
    private boolean myDryRun;
    private boolean myMove;
    private String myOrigDir;
    private String myBackupDir;
    private String myEvacuateDir;
    private List<String> myExcludePatterns = new ArrayList<>();

    private Runner myRunner = new Runner();

    
    /**
     * main
     * <p>
     * @param args
     * @throws IOException
     */
    public static void main( String[] args ) throws IOException
    {
        try
        {
            myLog.debug( "Evacuate starting" );
            
            EvacuateMain eva = new EvacuateMain( args );
            eva.init();
            eva.run();
            
            myLog.debug( "Successfully terminated" );
        }
        catch (Throwable t )
        {
            myLog.error( t.getMessage(), t );
        }
    }

    /**
     * init
     * <p>
     */
    public void init()
    {
        myDryRun = false;
        myMove = false;
        
        if ( myArgs.length < 3 )
        {
            usage();
            throw new IllegalArgumentException( "Too less parameters" );
        }
        
        int index = 0;
        
        for ( int i = 0; i < myArgs.length; i++ )
        {
            String arg = myArgs[i];
            
            if ( "-d".equals( arg ) || "--dry-run".equals( arg ) )
            {
                myDryRun = true;
                index++;
                continue;
            }
            
            if ( "-m".equals( arg ) || "--move".equals( arg ) )
            {
                myMove = true;
                index++;
                continue;
            }
            
            if ( "-e".equals( arg ) || "--exclude".equals( arg ) )
            {
                index++;
                i++;
                if ( i >= myArgs.length )
                {
                    throw new IllegalArgumentException( "File URL expected after option " + arg );
                }
                
                myExcludePatterns = initExcludePattersFromFile( myArgs[i] );
                index++;
                continue;
            }
        }

        int needed = index + 3;
        
        if ( myArgs.length < needed )
        {
            usage();
            throw new IllegalArgumentException( "Too less parameters" );
        }
        
        myOrigDir = myArgs[index];
        index++;
        myBackupDir = myArgs[index];
        index++;
        myEvacuateDir = myArgs[index];
        index++;

        myLog.debug( "Extracted command line parameters:"
                + "\n    Original directory  : " + myOrigDir 
                + "\n    Backup directory    : " + myBackupDir
                + "\n    Evacuation directory: " + myEvacuateDir
                + "\n    Dry-Run             : " + myDryRun
                + "\n    Move                : " + myMove
                + "\n    ExcludePatterns     : " + myExcludePatterns
                );
        
        File origDir = new File( myOrigDir );
        if ( !origDir.exists() )
        {
            usage();
            throw new IllegalArgumentException( "Original directory does not exist: " + myOrigDir );
        }
        
        File backupDir = new File( myBackupDir );
        if ( !backupDir.exists() )
        {
            usage();
            throw new IllegalArgumentException( "Backup directory does not exist: " + myBackupDir );
        }

        File evacuateDir = new File( myEvacuateDir );
        if ( !evacuateDir.exists() )
        {
            
            if ( !evacuateDir.mkdirs() )
            {
                usage();
                throw new IllegalArgumentException( "Evacuation directory cannot be created: " + myEvacuateDir );
            }
        }
        
        if ( !origDir.isDirectory() )
        {
            usage();
            throw new IllegalArgumentException( "Evacuation directory is not a directory: " + myOrigDir );
        }

        if ( !backupDir.isDirectory() )
        {
            usage();
            throw new IllegalArgumentException( "Evacuation directory is not a directory: " + myBackupDir );
        }

        if ( !evacuateDir.isDirectory() )
        {
            usage();
            throw new IllegalArgumentException( "Evacuation directory is not a directory: " + myEvacuateDir );
        }

        if (    ( origDir.equals( backupDir )     )
             || ( origDir.equals( evacuateDir )   ) 
             || ( backupDir.equals( evacuateDir ) ) ) 
        {
            usage();
            throw new IllegalArgumentException( "At least two of the given directories are equal." );
        }
        
    }

    /**
     * initExcludePattersFromFile
     * <p>
     * @param aFilename
     * @return
     */
    private List<String> initExcludePattersFromFile( String aFilename )
    {
        List<String> result = new ArrayList<>();
        
        Path path = Paths.get( aFilename );
        
        if ( Files.notExists( path ) )
        {
            throw new IllegalArgumentException( "File " + aFilename + " does not exist" );
        }
        
        try
        {
            result = FileUtils.readLines( path.toFile() );
        }
        catch ( IOException e )
        {
            throw new IllegalArgumentException( "File " + aFilename + " cannot be read", e );
        }
        
        return result;
        
    }

    /**
     * usage
     * <p>
     */
    private void usage()
    {
        StringBuilder sb = new StringBuilder( "\n\nUsage:" );
        sb.append( "\n" );
        sb.append( "\n    evacuate.bat [options] OrigDir BackupDir EvacuateDir" );
        sb.append( "\n" );
        sb.append( "\n    where:" );
        sb.append( "\n        OrigDir    : original directory" );
        sb.append( "\n        BackupDir  : Backup directory" );
        sb.append( "\n        EvacuateDir: Evacuation directory" );
        sb.append( "\n    Options:" );
        sb.append( "\n        -d, --dry-run: no file operation is done, files to evacuate are listed on console" );
        sb.append( "\n        -m, --move   : evacuated files are moved from backup to evacuate dir instead of copy" );
        sb.append( "\n        -e, --exclude: exclude file. Option must be followed by valid file URL" );
        sb.append( "\n\n" );
        
        myLog.info( sb.toString() );
        
    }

    /**
     * run
     * <p>
     * @throws Exception 
     */
    public void run() throws Exception
    {
        myRunner.setOrigDir( myOrigDir );
        myRunner.setBackupDir( myBackupDir );
        myRunner.setEvacuateDir( myEvacuateDir );
        myRunner.setDryRun( myDryRun );
        myRunner.setMove( myMove );
        myRunner.setExcludePatterns( myExcludePatterns );
        
        myRunner.run();
        
    }

    /**
     * Constructor
     *
     * @param aArgs
     */
    public EvacuateMain( String[] aArgs )
    {
        super();
        myArgs = aArgs;
    }

}
