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
        if ( myArgs.length < 3 )
        {
            usage();
            throw new IllegalArgumentException( "Too less parameters" );
        }
        
        int index = 0;
        
        for ( int i = 0; i < (myArgs.length - 3); i++ )
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

        myLog.debug( "Command line parameters:"
                + "\n    Original directory  : " + myOrigDir 
                + "\n    Backup directory    : " + myBackupDir
                + "\n    Evacuation directory: " + myEvacuateDir
                + "\n    Dry-Run             : " + myDryRun
                + "\n    Move                : " + myMove );
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
        sb.append( "\n\n" );
        
        
        myLog.info( sb.toString() );
        
    }

    /**
     * run
     * <p>
     * @throws Exception 
     */
    private void run() throws Exception
    {
        Runner runner = new Runner();
        runner.setOrigDir( myOrigDir );
        runner.setBackupDir( myBackupDir );
        runner.setEvacuateDir( myEvacuateDir );
        runner.setDryRun( myDryRun );
        runner.setMove( myMove );
        
        runner.run();
        
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
