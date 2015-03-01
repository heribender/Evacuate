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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Several independant helper methods 
 *
 * @author Heri
 */
public final class Helper
{
    
    /** logger for this class */
    static Logger myLog = LogManager.getLogger( Helper.class );

    /**
     * Deletes a whole directory (recursively)
     * <p>
     * @param aDir
     *        a folder to be deleted (must not be null)
     * @throws IOException
     */
    public static void deleteDirRecursive( Path aDir ) throws IOException
    {
        if ( aDir == null )
        {
            throw new IllegalArgumentException( "aDir must not be null" );
        }
        
        if ( Files.notExists( aDir ) )
        {
            return;
        }
        
        if ( !Files.isDirectory( aDir ) )
        {
            throw new IllegalArgumentException( "given aDir is not a directory" );
        }
        
        Files.walkFileTree( aDir, new SimpleFileVisitor<Path>()
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

            /**
             * @see java.nio.file.SimpleFileVisitor#preVisitDirectory(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
             */
            @Override
            public FileVisitResult preVisitDirectory( Path aFile,
                                                      BasicFileAttributes aAttrs )
                throws IOException
            {
                if ( "System Volume Information".equals( ( aFile.getFileName() ) ) )
                {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile( Path file,
                                              BasicFileAttributes attrs )
                throws IOException
            {
                Files.delete( file );
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory( Path dir, IOException exc )
                throws IOException
            {
                if ( dir.isAbsolute() && dir.getRoot().equals( dir ) )
                {
                    myLog.debug( "root cannot be deleted: " + dir.toString() );
                    return FileVisitResult.CONTINUE;
                }
                
                Files.delete( dir );
                return FileVisitResult.CONTINUE;
            }

        } );
    }

    /**
     * If the given target is already present, the method retains this older version in a kind of
     * FIFO buffer (but persistent on disk). The given MaxBackups number indicates how many such
     * backups are kept.
     * <p>
     * This routine is valid for files and directories. With files, the numbering suffix is done
     * before the last dot in the file name, with directories the number suffix is appended at the
     * end. 
     * <p>
     * Example: target is "Target.txt" and there are already present:
     * <pre>
     *     Target.txt
     *     Target_01.txt
     *     Target_02.txt
     * <pre>
     * Target_02.txt is renamed to Target_03.txt, Target_01.txt to Target_02.txt and Target.txt Target_01.txt.
     * <p>
     * If MaxBackup would be 3, then Target_02.txt would have been deleted instead renamed.
     * <p>   
     * 
     * @param aTarget
     * @param aMaxBackups
     * @param aFailedPreparations 
     * @throws IOException
     */
    public static void prepareTrashChain( Path aTarget, int aMaxBackups, Map<Path,Throwable> aFailedPreparations )
    {
        myLog.debug( "preparing trash chain for " + aTarget.toString() );
        
        try
        {
            
            int i = aMaxBackups-1;
            
            while ( i > 0 )
            {
                Path targetUpper = appendNumberSuffix( aTarget, i );
                Path targetLower = ( i > 1 ) ? appendNumberSuffix( aTarget, i-1 ) 
                                             : aTarget;

                i--;

                if ( Files.notExists( targetUpper ) && Files.notExists( targetLower ) )
                {
                    continue;
                }
                
                if ( Files.exists( targetUpper ) )
                {
                    myLog.info( "There are already " + (i+2) + " trashed versions of " + aTarget.toString() + ". Deleting the oldest one" );

                    if ( Files.exists( targetUpper ) )
                    {
                        if ( Files.isDirectory( targetUpper ) )
                        {
                            Helper.deleteDirRecursive( targetUpper );
                        }
                        else
                        {
                            Files.delete( targetUpper );
                        }
                    }
                }
                
                if ( Files.notExists( targetLower ) )
                {
                    continue;
                }

                myLog.debug( "Renaming " + targetLower.toString() + " to " + targetUpper.toString() );
                Files.move( targetLower, targetUpper, StandardCopyOption.ATOMIC_MOVE );
            }
        }
        catch ( Throwable e )
        {
            aFailedPreparations.put( aTarget, e );
        }
    }

    /**
     * Appends a number suffix to the name of the Path object.
     * <p>
     * This routine is valid for files and directories. With files, the numbering suffix is done
     * before the last dot in the file name, with directories the number suffix is appended at the
     * end. 
     * <p>
     * If the path object does not exist it is assumed to be a directory. Numbering a non existing
     * file can be done by overloaded method {@link #appendNumberSuffix(Path, int, boolean)} setting
     * third parameter to true.
     * <p>
     * Example: 
     * <pre>
     *     path/to/prefix.directory    ->     path/to/prefix.directory_01    
     *     path/to/prefix.file.txt     ->     path/to/prefix.file_01.txt    
     * <pre>
     * 
     * @param aPath
     *        the path whose name should be suffixed
     * @param aNumber
     *        the number to suffix
     * @return the new path
     */
    public static Path appendNumberSuffix( Path aPath, int aNumber )
    {
        boolean beforeExtension = false;
        
        if ( Files.exists( aPath ) && !Files.isDirectory( aPath ) )
        {
            beforeExtension = true;
        }

        return appendNumberSuffix( aPath, aNumber, beforeExtension );
    }
    
    /**
     * Appends a number suffix to the name of the Path object. If the flag BeforeExtension is
     * <code>true</code>, the number is appended before the last dot in the file name.
     * <p>
     * See examples in {@link #appendNumberSuffix(Path, int)}
     * <p>
     * 
     * @param aPath
     *        the path whose name should be suffixed
     * @param aNumber
     *        the number to suffix
     * @param aBeforeExtension 
     *        <code>true</code>: the extension stays the last part of the filename.
     * @return the new path
     */
    public static Path appendNumberSuffix( Path aPath, int aNumber, boolean aBeforeExtension )
    {
        DecimalFormat df = new DecimalFormat( "00" );
        String suffix = "_" + df.format( aNumber );
        
        String parent = aPath.getParent() == null ? "" : aPath.getParent().toString();
        String extension = "";
        String name = aPath.getFileName().toString();
        
        if ( aBeforeExtension )
        {
            extension = FilenameUtils.getExtension(name);
            
            if ( extension.length() > 0 )
            {
                extension = "." + extension;
            }
            name = FilenameUtils.getBaseName(name);
        }
        
        return Paths.get( parent, name + suffix + extension );
    }

    /**
     * Constructor
     *
     */
    private Helper()
    {
        super();
    }

}
