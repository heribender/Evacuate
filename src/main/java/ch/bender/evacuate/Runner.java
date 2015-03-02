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
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.FileUtils;
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
    Logger myLog = LogManager.getLogger( Runner.class );
    
    private static final int MAX_TRASH_VERSIONS = 10;
    
    private boolean myDryRun;
    private boolean myMove;
    private String myOrigDirStr;
    private String myBackupDirStr;
    private String myEvacuateDirStr;
    private List<String> myExcludePatterns = new ArrayList<>();

    private Path myOrigDir;
    private Path myBackupDir;
    private Path myEvacuateDir;
    private Map<Path,Path> myEvacuateCandidates;
    private Map<Path,Throwable> myFailedChainPreparations;
    private Collection<CompletableFuture<?>> myFutures;

    /**
     * run
     * <p>
     * @throws Exception 
     */
    public void run() throws Exception
    {
        checkDirectories();
        
        myEvacuateCandidates = new TreeMap<>();
        myFailedChainPreparations = Collections.synchronizedMap( new HashMap<>() );
        myFutures = new HashSet<>();
        
        Files.walkFileTree( myBackupDir, new SimpleFileVisitor<Path>()
        {
            /**
             * @see java.nio.file.SimpleFileVisitor#visitFileFailed(java.lang.Object, java.io.IOException)
             */
            @Override
            public FileVisitResult visitFileFailed( Path aFile, IOException aExc )
                throws IOException
            {
                if ( "System Volume Information".equals( ( aFile.getFileName().toString() ) ) )
                {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                
                throw aExc;
            }

            @Override
            public FileVisitResult visitFile( Path file,
                                              BasicFileAttributes attrs )
                throws IOException
            {
                return Runner.this.visitFile( file, attrs );
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                if ( "System Volume Information".equals( ( dir.getFileName() ) ) )
                {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                
                return Runner.this.preVisitDirectory( dir, attrs );
            }

        } );
        
        if ( myEvacuateCandidates.size() == 0 )
        {
            myLog.info( "No candidates for evacuation found" );
        }
        else
        {
            StringBuilder sb = new StringBuilder( "\nFound candidates for evacuation:" );
            myEvacuateCandidates.keySet().forEach( p -> sb.append( "\n    " + p.toString() ) );
            myLog.info( sb.toString() );
        }
        
        if ( myDryRun )
        {
            myLog.debug( "DryRun flag is set. Doing nothing" );
            return;
        }

        if ( myFutures.size() > 0 )
        {
            myLog.debug( "Waiting for all async tasks to complete" );
            CompletableFuture.allOf( myFutures.toArray( new CompletableFuture[myFutures.size()] ) ).get();
        }
        
        if ( myFailedChainPreparations.size() > 0 )
        {
            for ( Path path : myFailedChainPreparations.keySet() )
            {
                myLog.error( "exception occured", myFailedChainPreparations.get( path ) );
            }

            throw new Exception( "chain preparation failed. See above error messages" );
        }

        
        for ( Path src : myEvacuateCandidates.keySet() )
        {
            Path dst = myEvacuateCandidates.get( src );
            Path dstParent = dst.getParent();
            
            if ( Files.notExists( dstParent ) )
            {
                Files.createDirectories( dstParent );  // FUTURE: overtake file attributes from src
            }
            
            if ( myMove )
            {
                try
                {
                    myLog.debug( "Moving file system object \"" + src.toString() + "\" to \"" + dst.toString() + "\"" );
                    Files.move( src, dst, StandardCopyOption.ATOMIC_MOVE );
                }
                catch ( AtomicMoveNotSupportedException e )
                {
                    myLog.warn( "Atomic move not supported. Try copy and then delete" );
                    
                    if ( Files.isDirectory( src ) )
                    {
                        myLog.debug( "Copying folder \"" + src.toString() + "\" to \"" + dst.toString() + "\"" );
                        FileUtils.copyDirectory( src.toFile(), dst.toFile() );
                        myLog.debug( "Delete folder \"" + src.toString() + "\"" );
                        FileUtils.deleteDirectory( src.toFile() );
                    }
                    else
                    {
                        myLog.debug( "Copy file \"" + src.toString() + "\" to \"" + dst.toString() + "\"" );
                        FileUtils.copyFile( src.toFile(), dst.toFile() );
                        myLog.debug( "Delete file \"" + src.toString() + "\"" );
                        Files.delete( src );
                    }
                }
                
            }
            else
            {
                if ( Files.isDirectory( src ) )
                {
                    myLog.debug( "Copying folder \"" + src.toString() + "\" to \"" + dst.toString() + "\"" );
                    FileUtils.copyDirectory( src.toFile(), dst.toFile() );
                }
                else
                {
                    myLog.debug( "Copy file \"" + src.toString() + "\" to \"" + dst.toString() + "\"" );
                    FileUtils.copyFile( src.toFile(), dst.toFile() );
                }
            }
        }
        
    }

    /**
     * visitFile
     * <p>
     * @param aFile
     * @param aAttrs
     * @return
     * @throws IOException
     */
    FileVisitResult visitFile( Path aFile, BasicFileAttributes aAttrs )
        throws IOException
    {
//        myLog.debug( "Visiting file " + aFile.toString() );
        return visit( aFile );
    }

    /**
     * preVisitDirectory
     * <p>
     * @param aDir
     * @param aAttrs
     * @return
     * @throws IOException
     */
    FileVisitResult preVisitDirectory( Path aDir, BasicFileAttributes aAttrs )
        throws IOException
    {
        if ( aDir.equals( myBackupDir ) )
        {
            myLog.debug( "Visiting the backup root. This directory is never subject of evactuation" );
            return FileVisitResult.CONTINUE;
        }

        myLog.debug( "Visiting directory " + aDir.toString() );
        return visit( aDir );
    }

    /**
     * visit
     * <p>
     * @param aPath
     * @return
     */
    private FileVisitResult visit( Path aPath )
    {
        Path subDirToBackupRoot = myBackupDir.relativize( aPath );
        Path origPendant = myOrigDir.resolve( subDirToBackupRoot );
        
        if ( Files.notExists( origPendant ) )
        {
            evacuate( aPath );
            
            if ( Files.isDirectory( aPath ) )
            {
                return FileVisitResult.SKIP_SUBTREE;
            }
            
            // else is file:
            return FileVisitResult.CONTINUE;
        }

        return FileVisitResult.CONTINUE;
    }

    /**
     * evacuateDir
     * <p>
     * @param aDir
     * @throws Exception 
     */
    private void evacuate( Path aDir )
    {
        Path subDirToBackupRoot = myBackupDir.relativize( aDir );
        Path evacuateTarget = myEvacuateDir.resolve( subDirToBackupRoot );
        
        myEvacuateCandidates.put( aDir, evacuateTarget );
        
        if ( myDryRun )
        {
            return;
        }
        
        if ( Files.exists( evacuateTarget ) )
        {
            myLog.debug( "adding Future (trash chain preparation): " + evacuateTarget.toString() );
            CompletableFuture<Void> future = CompletableFuture.runAsync( 
                   () -> Helper.prepareTrashChain( evacuateTarget, 
                                                   MAX_TRASH_VERSIONS,
                                                   myFailedChainPreparations ) );
            myFutures.add( future );
        }
        
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
    
    /**
     * @return Returns the excludePatterns.
     */
    public List<String> getExcludePatterns()
    {
        return myExcludePatterns;
    }

    /**
     * @param aExcludePatterns The excludePatterns to set.
     */
    public void setExcludePatterns( List<String> aExcludePatterns )
    {
        myExcludePatterns = aExcludePatterns;
    }

    

}
