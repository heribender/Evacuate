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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * TODO Hey Heri, comment this type please !
 *
 * @author Heri
 */
public class Runner
{
    
    /** logger for this class */
    private Logger myLog = LogManager.getLogger( Runner.class );
    
    private boolean myDryRun;
    private boolean myMove;
    private String myOrigDirStr;
    private String myBackupDirStr;
    private String myEvacuateDirStr;
    private Path myOrigDir;
    private Path myBackupDir;
    private Path myEvacuateDir;
    
    /**
     * run
     * <p>
     * @throws Exception 
     */
    public void run() throws Exception
    {
        checkDirectories();
        
        
        
    }

    /**
     * checkDirectories
     * <p>
     * @throws IOException
     */
    void checkDirectories() throws IOException
    {
        myOrigDir = Paths.get( myOrigDirStr );
        myBackupDir = Paths.get( myBackupDirStr );
        myEvacuateDir = Paths.get( myEvacuateDirStr );
        
        if ( !Files.exists( myOrigDir ) )
        {
            throw new IllegalArgumentException( "Original directory cannot be found: " + myOrigDirStr );
        }
        
        if ( !Files.isDirectory( myOrigDir ) )
        {
            throw new IllegalArgumentException( "Original directory is not a directory: " + myOrigDirStr );
        }
        
        if ( !Files.exists( myBackupDir ) )
        {
            throw new IllegalArgumentException( "Backup directory cannot be found: " + myBackupDir );
        }
        
        if ( !Files.isDirectory( myBackupDir ) )
        {
            throw new IllegalArgumentException( "Original directory is not a directory: " + myBackupDir );
        }
        
        if ( !Files.exists( myEvacuateDir ) )
        {
            Files.createDirectories( myEvacuateDir );
        }
        
        if ( !Files.isDirectory( myEvacuateDir ) )
        {
            throw new IllegalArgumentException( "Evacuation directory is not a directory: " + myEvacuateDir );
        }
    }

    /**
     * @return Returns the dryRun.
     */
    public boolean isDryRun()
    {
        return myDryRun;
    }
    
    /**
     * @param aDryRun The dryRun to set.
     */
    public void setDryRun( boolean aDryRun )
    {
        myDryRun = aDryRun;
    }
    
    /**
     * @return Returns the move.
     */
    public boolean isMove()
    {
        return myMove;
    }

    /**
     * @param aMove The move to set.
     */
    public void setMove( boolean aMove )
    {
        myMove = aMove;
    }

    /**
     * @return Returns the origDir.
     */
    public String getOrigDir()
    {
        return myOrigDirStr;
    }
    
    /**
     * @param aOrigDir The origDir to set.
     */
    public void setOrigDir( String aOrigDir )
    {
        myOrigDirStr = aOrigDir;
    }
    
    /**
     * @return Returns the backupDir.
     */
    public String getBackupDir()
    {
        return myBackupDirStr;
    }
    
    /**
     * @param aBackupDir The backupDir to set.
     */
    public void setBackupDir( String aBackupDir )
    {
        myBackupDirStr = aBackupDir;
    }
    
    /**
     * @return Returns the evacuateDir.
     */
    public String getEvacuateDir()
    {
        return myEvacuateDirStr;
    }
    
    /**
     * @param aEvacuateDir The evacuateDir to set.
     */
    public void setEvacuateDir( String aEvacuateDir )
    {
        myEvacuateDirStr = aEvacuateDir;
    }
    

}
